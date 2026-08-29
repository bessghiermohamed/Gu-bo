package com.example.talib.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Specialty::class,
    AcademicYear::class,
    ModuleCourse::class,
    Lecture::class,
    CachedCourseMaterial::class,
    Assignment::class,
    ScheduleItem::class,
    Exam::class,
    StudentGrade::class,
    Announcement::class,
    StudentProfile::class,
    StudentNote::class,
    LibraryReference::class,
    AcademicCalendarEvent::class,
    AttendanceRecord::class,
    StudentIssueReport::class,
    ClassPoll::class,
    AppUser::class
  ],
  version = 5,
  exportSchema = false
)
abstract class TalibDatabase : RoomDatabase() {
  abstract fun talibDao(): TalibDao

  companion object {
    @Volatile
    private var INSTANCE: TalibDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): TalibDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TalibDatabase::class.java,
          "talib_database"
        )
          .addCallback(TalibDatabaseCallback(scope))
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class TalibDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.talibDao())
          }
        }
      }
    }

    suspend fun populateInitialData(dao: TalibDao) {
      // 1. Core Specialties for Algerian Higher Education
      val specArabic = Specialty(
        id = 1,
        nameAr = "اللغة والأدب العربي",
        code = "ARB-ENS",
        iconName = "menu_book",
        description = "المدرسة العليا للأساتذة - تخصص التعليم واللسانيات",
        institution = "المدرسة العليا للأساتذة - بوزريعة",
        faculty = "قسم اللغة والأدب العربي"
      )
      val specInfo = Specialty(
        id = 2,
        nameAr = "الإعلام الآلي وتطوير البرمجيات",
        code = "INF-USTHB",
        iconName = "computer",
        description = "جامعة العلوم والتكنولوجيا هواري بومدين",
        institution = "جامعة العلوم والتكنولوجيا هواري بومدين (USTHB)",
        faculty = "كلية الإعلام الآلي"
      )
      dao.insertSpecialties(listOf(specArabic, specInfo))

      // 2. Academic Years
      val y1 = AcademicYear(id = 1, specialtyId = 1, yearName = "السنة الأولى (L1)", semester = 1)
      val y2 = AcademicYear(id = 2, specialtyId = 1, yearName = "السنة الثانية (L2)", semester = 1)
      val y3 = AcademicYear(id = 3, specialtyId = 1, yearName = "السنة الثالثة (L3)", semester = 1)
      dao.insertAcademicYears(listOf(y1, y2, y3))

      // Clean default profile structure ready for student registration
      dao.insertStudentProfile(
        StudentProfile(
          id = 1,
          fullName = "محمد بن علي",
          studentId = "2026-TLB-8459",
          institution = "المدرسة العليا للأساتذة - بوزريعة (ENS)",
          university = "المدرسة العليا للأساتذة - بوزريعة",
          faculty = "قسم اللغة والأدب العربي",
          specialtyName = "اللغة والأدب العربي",
          profileTrack = "أستاذ التعليم الابتدائي",
          selectedSpecialtyId = 1,
          selectedYearId = 2,
          academicYearName = "السنة الثانية (L2)",
          semesterName = "السداسي الأول (S1)",
          groupNumber = "الفوج 03",
          subGroup = "الفوج الفرعي B",
          email = "mohamedbessghier8@gmail.com",
          isAdminMode = false,
          userRole = "STUDENT",
          themePalette = "ACADEMIC",
          isConfigured = true
        )
      )
    }
  }
}
