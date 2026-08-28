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
  val description: String = "",
  val isCachedOffline: Boolean = true,
  val lastViewedTimestamp: Long = 0L,
  val syllabusTopics: String = ""
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
  val isDownloaded: Boolean = false,
  val isCachedOffline: Boolean = true,
  val isRead: Boolean = false,
  val lastViewedTimestamp: Long = 0L,
  val cachedContentText: String = ""
)

@Entity(tableName = "cached_materials")
data class CachedCourseMaterial(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val moduleId: Long,
  val moduleName: String,
  val title: String,
  val materialType: String = "محاضرة", // محاضرة / ملخص دراسي / أعمال موجهة TD / امتحان سابق
  val summary: String,
  val fullText: String,
  val keyConcepts: String = "",
  val weekNumber: Int = 1,
  val cachedDate: String = "مخزن محلياً",
  val lastViewedTimestamp: Long = System.currentTimeMillis(),
  val isOfflineAvailable: Boolean = true
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
  val credits: Int = 4,
  val isOfficial: Boolean = false, // تمييز بصري صريح: رسمي من الإدارة أو تقديري من الطالب
  val targetScore: Double = 10.0 // الدرجة المستهدفة للنجاح
)

@Entity(tableName = "announcements")
data class Announcement(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val author: String,
  val date: String,
  val urgency: String = "عام", // عاجل / هام / عام
  val specialtyId: Long? = null,
  val isRead: Boolean = false,
  val visibilityScope: String = "تخصص كامل", // تخصص كامل / عدة أفواج محددة / فوج واحد
  val targetGroups: String = "الكل"
)

@Entity(tableName = "student_profiles")
data class StudentProfile(
  @PrimaryKey val id: Long = 1,
  val fullName: String = "محمد البشير",
  val studentId: String = "202631084592",
  val institution: String = "المدرسة العليا للأساتذة - بوزريعة (ENS)",
  val university: String = "المدرسة العليا للأساتذة - بوزريعة",
  val faculty: String = "قسم اللغة والأدب العربي",
  val specialtyName: String = "اللغة والأدب العربي",
  val profileTrack: String = "ملمح أستاذ التعليم الابتدائي",
  val selectedSpecialtyId: Long = 1,
  val selectedYearId: Long = 2,
  val academicYearName: String = "السنة الثانية (L2)",
  val semesterName: String = "السداسي الأول (S1)",
  val groupNumber: String = "الفوج 03",
  val subGroup: String = "الفوج الفرعي B",
  val email: String = "mohamedbessghier8@gmail.com",
  val isAdminMode: Boolean = false,
  val userRole: String = "STUDENT", // STUDENT / REPRESENTATIVE / SPECIALTY_ADMIN / OWNER
  val themePalette: String = "ACADEMIC", // ACADEMIC (#1B5E4B) or MODERN (#8B5CF6)
  val isConfigured: Boolean = true
)

@Entity(tableName = "student_notes")
data class StudentNote(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val moduleName: String = "عام",
  val createdAt: String = "اليوم",
  val colorHex: String = "#1B5E4B"
)

