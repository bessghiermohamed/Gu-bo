package com.example.talib.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TalibDao {
  // Specialties
  @Query("SELECT * FROM specialties ORDER BY id ASC")
  fun getAllSpecialties(): Flow<List<Specialty>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSpecialty(specialty: Specialty): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSpecialties(specialties: List<Specialty>)

  @Delete
  suspend fun deleteSpecialty(specialty: Specialty)

  // Academic Years
  @Query("SELECT * FROM academic_years WHERE specialtyId = :specialtyId ORDER BY id ASC")
  fun getYearsForSpecialty(specialtyId: Long): Flow<List<AcademicYear>>

  @Query("SELECT * FROM academic_years ORDER BY id ASC")
  fun getAllAcademicYears(): Flow<List<AcademicYear>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAcademicYears(years: List<AcademicYear>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAcademicYear(year: AcademicYear): Long

  // Modules
  @Query("SELECT * FROM modules ORDER BY id ASC")
  fun getAllModules(): Flow<List<ModuleCourse>>

  @Query("SELECT * FROM modules WHERE specialtyId = :specialtyId AND academicYearId = :yearId ORDER BY id ASC")
  fun getModulesForSpecialtyAndYear(specialtyId: Long, yearId: Long): Flow<List<ModuleCourse>>

  @Query("SELECT * FROM modules WHERE id = :id LIMIT 1")
  suspend fun getModuleById(id: Long): ModuleCourse?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModule(module: ModuleCourse): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertModules(modules: List<ModuleCourse>)

  @Delete
  suspend fun deleteModule(module: ModuleCourse)

  // Lectures
  @Query("SELECT * FROM lectures ORDER BY weekNumber ASC, id ASC")
  fun getAllLectures(): Flow<List<Lecture>>

  @Query("SELECT * FROM lectures WHERE moduleId = :moduleId ORDER BY weekNumber ASC, id ASC")
  fun getLecturesForModule(moduleId: Long): Flow<List<Lecture>>

  @Query("SELECT * FROM lectures WHERE isBookmarked = 1")
  fun getBookmarkedLectures(): Flow<List<Lecture>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLecture(lecture: Lecture): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLectures(lectures: List<Lecture>)

  @Update
  suspend fun updateLecture(lecture: Lecture)

  @Delete
  suspend fun deleteLecture(lecture: Lecture)

  // Assignments
  @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
  fun getAllAssignments(): Flow<List<Assignment>>

  @Query("SELECT * FROM assignments WHERE moduleId = :moduleId")
  fun getAssignmentsForModule(moduleId: Long): Flow<List<Assignment>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAssignment(assignment: Assignment): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAssignments(assignments: List<Assignment>)

  @Update
  suspend fun updateAssignment(assignment: Assignment)

  @Delete
  suspend fun deleteAssignment(assignment: Assignment)

  // Schedules
  @Query("SELECT * FROM schedules WHERE specialtyId = :specialtyId AND academicYearId = :yearId ORDER BY dayOfWeek ASC, startTime ASC")
  fun getScheduleForSpecialty(specialtyId: Long, yearId: Long): Flow<List<ScheduleItem>>

  @Query("SELECT * FROM schedules ORDER BY dayOfWeek ASC, startTime ASC")
  fun getAllScheduleItems(): Flow<List<ScheduleItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScheduleItem(item: ScheduleItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScheduleItems(items: List<ScheduleItem>)

  @Delete
  suspend fun deleteScheduleItem(item: ScheduleItem)

  // Exams
  @Query("SELECT * FROM exams ORDER BY examDate ASC")
  fun getAllExams(): Flow<List<Exam>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExam(exam: Exam): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExams(exams: List<Exam>)

  @Update
  suspend fun updateExam(exam: Exam)

  @Delete
  suspend fun deleteExam(exam: Exam)

  // Grades
  @Query("SELECT * FROM grades ORDER BY id ASC")
  fun getAllGrades(): Flow<List<StudentGrade>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGrade(grade: StudentGrade): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGrades(grades: List<StudentGrade>)

  @Update
  suspend fun updateGrade(grade: StudentGrade)

  @Delete
  suspend fun deleteGrade(grade: StudentGrade)

  // Announcements
  @Query("SELECT * FROM announcements ORDER BY id DESC")
  fun getAllAnnouncements(): Flow<List<Announcement>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAnnouncement(announcement: Announcement): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAnnouncements(announcements: List<Announcement>)

  @Delete
  suspend fun deleteAnnouncement(announcement: Announcement)

  // Student Profile
  @Query("SELECT * FROM student_profiles WHERE id = 1 LIMIT 1")
  fun getStudentProfile(): Flow<StudentProfile?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudentProfile(profile: StudentProfile)

  @Update
  suspend fun updateStudentProfile(profile: StudentProfile)

  // --- Offline Course Content Caching & Previously Viewed Tracking ---
  @Query("UPDATE lectures SET lastViewedTimestamp = :timestamp, isCachedOffline = 1 WHERE id = :lectureId")
  suspend fun markLectureAsViewed(lectureId: Long, timestamp: Long)

  @Query("UPDATE modules SET lastViewedTimestamp = :timestamp, isCachedOffline = 1 WHERE id = :moduleId")
  suspend fun markModuleAsViewed(moduleId: Long, timestamp: Long)

  @Query("SELECT * FROM lectures WHERE lastViewedTimestamp > 0 ORDER BY lastViewedTimestamp DESC")
  fun getPreviouslyViewedLectures(): Flow<List<Lecture>>

  @Query("SELECT * FROM modules WHERE lastViewedTimestamp > 0 ORDER BY lastViewedTimestamp DESC")
  fun getPreviouslyViewedModules(): Flow<List<ModuleCourse>>

  @Query("SELECT * FROM lectures WHERE isCachedOffline = 1 OR isDownloaded = 1 ORDER BY weekNumber ASC")
  fun getOfflineAvailableLectures(): Flow<List<Lecture>>

  // Cached Course Materials
  @Query("SELECT * FROM cached_materials WHERE id = :id LIMIT 1")
  suspend fun getCachedMaterialById(id: Long): CachedCourseMaterial?

  @Query("SELECT * FROM cached_materials ORDER BY lastViewedTimestamp DESC")
  fun getAllCachedMaterials(): Flow<List<CachedCourseMaterial>>

  @Query("SELECT * FROM cached_materials WHERE moduleId = :moduleId ORDER BY weekNumber ASC")
  fun getCachedMaterialsForModule(moduleId: Long): Flow<List<CachedCourseMaterial>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCachedMaterial(material: CachedCourseMaterial): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCachedMaterials(materials: List<CachedCourseMaterial>)

  @Query("UPDATE cached_materials SET lastViewedTimestamp = :timestamp WHERE id = :materialId")
  suspend fun markCachedMaterialAsViewed(materialId: Long, timestamp: Long)

  @Delete
  suspend fun deleteCachedMaterial(material: CachedCourseMaterial)

  @Query("DELETE FROM cached_materials")
  suspend fun clearAllCachedMaterials()

  // --- Student Personal Notes (ملفاتي) ---
  @Query("SELECT * FROM student_notes ORDER BY id DESC")
  fun getAllNotes(): Flow<List<StudentNote>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(note: StudentNote): Long

  @Delete
  suspend fun deleteNote(note: StudentNote)

  // --- State Updates ---
  @Query("UPDATE lectures SET isRead = :isRead WHERE id = :lectureId")
  suspend fun updateLectureReadStatus(lectureId: Long, isRead: Boolean)

  @Query("UPDATE lectures SET isBookmarked = :isBookmarked WHERE id = :lectureId")
  suspend fun updateLectureBookmarkStatus(lectureId: Long, isBookmarked: Boolean)

  @Query("UPDATE announcements SET isRead = :isRead WHERE id = :announcementId")
  suspend fun updateAnnouncementReadStatus(announcementId: Long, isRead: Boolean)

  @Query("UPDATE grades SET isOfficial = :isOfficial WHERE id = :gradeId")
  suspend fun updateGradeOfficialStatus(gradeId: Long, isOfficial: Boolean)

  @Query("UPDATE grades SET targetScore = :targetScore WHERE id = :gradeId")
  suspend fun updateGradeTargetScore(gradeId: Long, targetScore: Double)
}

