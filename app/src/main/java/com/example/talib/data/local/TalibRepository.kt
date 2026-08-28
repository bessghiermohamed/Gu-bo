package com.example.talib.data.local

import kotlinx.coroutines.flow.Flow

class TalibRepository(private val dao: TalibDao) {
  // Specialties
  val allSpecialties: Flow<List<Specialty>> = dao.getAllSpecialties()
  suspend fun insertSpecialty(specialty: Specialty) = dao.insertSpecialty(specialty)
  suspend fun deleteSpecialty(specialty: Specialty) = dao.deleteSpecialty(specialty)

  // Academic Years
  fun getYearsForSpecialty(specialtyId: Long): Flow<List<AcademicYear>> = dao.getYearsForSpecialty(specialtyId)
  val allAcademicYears: Flow<List<AcademicYear>> = dao.getAllAcademicYears()
  suspend fun insertAcademicYear(year: AcademicYear) = dao.insertAcademicYear(year)

  // Modules
  val allModules: Flow<List<ModuleCourse>> = dao.getAllModules()
  fun getModulesForSpecialtyAndYear(specialtyId: Long, yearId: Long): Flow<List<ModuleCourse>> =
    dao.getModulesForSpecialtyAndYear(specialtyId, yearId)
  suspend fun getModuleById(id: Long): ModuleCourse? = dao.getModuleById(id)
  suspend fun insertModule(module: ModuleCourse) = dao.insertModule(module)
  suspend fun deleteModule(module: ModuleCourse) = dao.deleteModule(module)

  // Lectures
  val allLectures: Flow<List<Lecture>> = dao.getAllLectures()
  fun getLecturesForModule(moduleId: Long): Flow<List<Lecture>> = dao.getLecturesForModule(moduleId)
  val bookmarkedLectures: Flow<List<Lecture>> = dao.getBookmarkedLectures()
  suspend fun insertLecture(lecture: Lecture) = dao.insertLecture(lecture)
  suspend fun updateLecture(lecture: Lecture) = dao.updateLecture(lecture)
  suspend fun deleteLecture(lecture: Lecture) = dao.deleteLecture(lecture)

  // Assignments
  val allAssignments: Flow<List<Assignment>> = dao.getAllAssignments()
  fun getAssignmentsForModule(moduleId: Long): Flow<List<Assignment>> = dao.getAssignmentsForModule(moduleId)
  suspend fun insertAssignment(assignment: Assignment) = dao.insertAssignment(assignment)
  suspend fun updateAssignment(assignment: Assignment) = dao.updateAssignment(assignment)
  suspend fun deleteAssignment(assignment: Assignment) = dao.deleteAssignment(assignment)

  // Schedules
  fun getScheduleForSpecialty(specialtyId: Long, yearId: Long): Flow<List<ScheduleItem>> =
    dao.getScheduleForSpecialty(specialtyId, yearId)
  val allScheduleItems: Flow<List<ScheduleItem>> = dao.getAllScheduleItems()
  suspend fun insertScheduleItem(item: ScheduleItem) = dao.insertScheduleItem(item)
  suspend fun deleteScheduleItem(item: ScheduleItem) = dao.deleteScheduleItem(item)

  // Exams
  val allExams: Flow<List<Exam>> = dao.getAllExams()
  suspend fun insertExam(exam: Exam) = dao.insertExam(exam)
  suspend fun updateExam(exam: Exam) = dao.updateExam(exam)
  suspend fun deleteExam(exam: Exam) = dao.deleteExam(exam)

  // Grades
  val allGrades: Flow<List<StudentGrade>> = dao.getAllGrades()
  suspend fun insertGrade(grade: StudentGrade) = dao.insertGrade(grade)
  suspend fun updateGrade(grade: StudentGrade) = dao.updateGrade(grade)
  suspend fun deleteGrade(grade: StudentGrade) = dao.deleteGrade(grade)

  // Announcements
  val allAnnouncements: Flow<List<Announcement>> = dao.getAllAnnouncements()
  suspend fun insertAnnouncement(announcement: Announcement) = dao.insertAnnouncement(announcement)
  suspend fun deleteAnnouncement(announcement: Announcement) = dao.deleteAnnouncement(announcement)

  // Student Profile
  val studentProfile: Flow<StudentProfile?> = dao.getStudentProfile()
  suspend fun updateStudentProfile(profile: StudentProfile) = dao.updateStudentProfile(profile)

  // Offline Caching & Previously Viewed Materials
  val previouslyViewedLectures: Flow<List<Lecture>> = dao.getPreviouslyViewedLectures()
  val previouslyViewedModules: Flow<List<ModuleCourse>> = dao.getPreviouslyViewedModules()
  val offlineAvailableLectures: Flow<List<Lecture>> = dao.getOfflineAvailableLectures()
  val allCachedMaterials: Flow<List<CachedCourseMaterial>> = dao.getAllCachedMaterials()
  fun getCachedMaterialsForModule(moduleId: Long): Flow<List<CachedCourseMaterial>> =
    dao.getCachedMaterialsForModule(moduleId)

  suspend fun markLectureAsViewed(lectureId: Long, timestamp: Long = System.currentTimeMillis()) =
    dao.markLectureAsViewed(lectureId, timestamp)

  suspend fun markModuleAsViewed(moduleId: Long, timestamp: Long = System.currentTimeMillis()) =
    dao.markModuleAsViewed(moduleId, timestamp)

  suspend fun markCachedMaterialAsViewed(materialId: Long, timestamp: Long = System.currentTimeMillis()) =
    dao.markCachedMaterialAsViewed(materialId, timestamp)

  suspend fun insertCachedMaterial(material: CachedCourseMaterial) = dao.insertCachedMaterial(material)
  suspend fun insertCachedMaterials(materials: List<CachedCourseMaterial>) = dao.insertCachedMaterials(materials)
  suspend fun deleteCachedMaterial(material: CachedCourseMaterial) = dao.deleteCachedMaterial(material)
  suspend fun clearAllCachedMaterials() = dao.clearAllCachedMaterials()

  // Student Notes (ملفاتي)
  val allNotes: Flow<List<StudentNote>> = dao.getAllNotes()
  suspend fun insertNote(note: StudentNote) = dao.insertNote(note)
  suspend fun deleteNote(note: StudentNote) = dao.deleteNote(note)

  // Status updates
  suspend fun updateLectureReadStatus(lectureId: Long, isRead: Boolean) =
    dao.updateLectureReadStatus(lectureId, isRead)

  suspend fun updateLectureBookmarkStatus(lectureId: Long, isBookmarked: Boolean) =
    dao.updateLectureBookmarkStatus(lectureId, isBookmarked)

  suspend fun updateAnnouncementReadStatus(announcementId: Long, isRead: Boolean) =
    dao.updateAnnouncementReadStatus(announcementId, isRead)

  suspend fun updateGradeOfficialStatus(gradeId: Long, isOfficial: Boolean) =
    dao.updateGradeOfficialStatus(gradeId, isOfficial)

  suspend fun updateGradeTargetScore(gradeId: Long, targetScore: Double) =
    dao.updateGradeTargetScore(gradeId, targetScore)
}

