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
}
