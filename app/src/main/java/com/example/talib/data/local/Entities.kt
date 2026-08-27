package com.example.talib.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specialties")
data class Specialty(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val nameAr: String,
  val code: String,
  val iconName: String = "book",
  val description: String = ""
)

@Entity(tableName = "academic_years")
data class AcademicYear(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val specialtyId: Long,
  val yearName: String,
  val semester: Int = 1
)

@Entity(tableName = "modules")
data class ModuleCourse(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val specialtyId: Long,
  val academicYearId: Long,
  val name: String,
  val code: String,
  val coefficient: Double = 2.0,
  val credits: Int = 4,
  val professorName: String = "",
  val professorEmail: String = "",
  val category: String = "أساسي", // أساسي / منهجي / استكشافي
  val description: String = ""
)

@Entity(tableName = "lectures")
data class Lecture(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val moduleId: Long,
  val weekNumber: Int,
  val title: String,
  val summary: String,
  val pdfFileName: String = "lecture_notes.pdf",
  val pdfUrl: String = "",
  val durationMinutes: Int = 90,
  val date: String = "",
  val isBookmarked: Boolean = false,
  val isDownloaded: Boolean = false
)

@Entity(tableName = "assignments")
data class Assignment(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val moduleId: Long,
  val title: String,
  val dueDate: String,
  val description: String,
  val isCompleted: Boolean = false,
  val maxScore: Double = 20.0
)

@Entity(tableName = "schedules")
data class ScheduleItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val specialtyId: Long,
  val academicYearId: Long,
  val dayOfWeek: Int, // 1: الأحد, 2: الإثنين, 3: الثلاثاء, 4: الأربعاء, 5: الخميس
  val startTime: String,
  val endTime: String,
  val moduleName: String,
  val type: String, // محاضرة / أعمال موجهة TD / أعمال تطبيقية TP
  val room: String,
  val professor: String
)

@Entity(tableName = "exams")
data class Exam(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val moduleId: Long,
  val moduleName: String,
  val title: String,
  val examDate: String,
  val time: String,
  val room: String,
  val coefficient: Double = 2.0,
  val isFinished: Boolean = false
)

@Entity(tableName = "grades")
data class StudentGrade(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val moduleId: Long,
  val moduleName: String,
  val continuousScore: Double = 14.0, // TD / TP out of 20
  val examScore: Double = 15.0, // Exam out of 20
  val coefficient: Double = 2.0,
  val credits: Int = 4
)

@Entity(tableName = "announcements")
data class Announcement(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val author: String,
  val date: String,
  val urgency: String = "عام", // عاجل / هام / عام
  val specialtyId: Long? = null
)

@Entity(tableName = "student_profiles")
data class StudentProfile(
  @PrimaryKey val id: Long = 1,
  val fullName: String = "محمد البشير",
  val studentId: String = "202631084592",
  val university: String = "جامعة الجزائر 1 - بن يوسف بن خدة",
  val faculty: String = "كلية اللغة العربية والآداب واللغات",
  val specialtyName: String = "الأدب العربي واللغويات",
  val selectedSpecialtyId: Long = 1,
  val selectedYearId: Long = 2,
  val academicYearName: String = "السنة الثانية (L2)",
  val groupNumber: String = "الفوج 03",
  val subGroup: String = "الفوج الفرعي B",
  val email: String = "mohamedbessghier8@gmail.com",
  val isAdminMode: Boolean = false
)
