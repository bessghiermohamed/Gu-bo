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
    StudentNote::class
  ],
  version = 4,
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
      // 1. Initial Clean Student Profile (Configurable by the student)
      dao.insertStudentProfile(
        StudentProfile(
          id = 1,
          fullName = "طالب جديد",
          studentId = "",
          institution = "المؤسسة الجامعية",
          university = "المؤسسة الجامعية",
          faculty = "الكلية / القسم",
          specialtyName = "التخصص الأكاديمي",
          profileTrack = "المسار الدراسي",
          selectedSpecialtyId = 1,
          selectedYearId = 1,
          academicYearName = "السنة الدراسية",
          semesterName = "السداسي 1",
          groupNumber = "الفوج 01",
          subGroup = "الفوج الفرعي A",
          email = "",
          isAdminMode = false,
          userRole = "STUDENT",
          themePalette = "ACADEMIC"
        )
      )

      // All mock courses, mock lectures, mock announcements, mock exams,
      // mock assignments, mock schedule items, mock grades, mock cached materials,
      // and mock notes are removed. The platform starts completely clean.
    }
  }
}
