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
      // 1. Initial Specialties
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
        nameAr = "الإعلام الآلي والذكاء الاصطناعي",
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

      // 3. Initial Student Profile (Configured for closed academic view)
      dao.insertStudentProfile(
        StudentProfile(
          id = 1,
          fullName = "محمد البشير بن علي",
          studentId = "202631084592",
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

      // 4. Initial Modules for the student's closed track
      val mod1 = ModuleCourse(
        id = 1,
        specialtyId = 1,
        academicYearId = 2,
        name = "النحو والصرف المعمق",
        code = "ARB201",
        coefficient = 3.0,
        credits = 5,
        professorName = "أ.د. عبد القادر الفاسي",
        professorEmail = "a.fassi@ens-bouzareah.dz",
        category = "أساسي",
        description = "دراسة نظرية العامل، الإعراب، وبناء الجملة العربية التراثية والمعاصرة.",
        isCachedOffline = true
      )
      val mod2 = ModuleCourse(
        id = 2,
        specialtyId = 1,
        academicYearId = 2,
        name = "الأدب العربي القديم والمعلقات",
        code = "ARB202",
        coefficient = 3.0,
        credits = 5,
        professorName = "د. فاطمة الزهراء بن عيسى",
        professorEmail = "f.benissa@ens-bouzareah.dz",
        category = "أساسي",
        description = "تحليل نصوص العصر الجاهلي وصدر الإسلام، البناء الفني والموسيقي للقصيدة.",
        isCachedOffline = true
      )
      val mod3 = ModuleCourse(
        id = 3,
        specialtyId = 1,
        academicYearId = 2,
        name = "علم الدلالة والمعاجم",
        code = "ARB203",
        coefficient = 2.0,
        credits = 4,
        professorName = "د. كريم بلقاسم",
        professorEmail = "k.belkacem@ens-bouzareah.dz",
        category = "منهجي",
        description = "مناهج البحث الدلالي الحديث ونشأة المعاجم اللغوية وتطورها.",
        isCachedOffline = true
      )
      val mod4 = ModuleCourse(
        id = 4,
        specialtyId = 1,
        academicYearId = 2,
        name = "البلاغة والنقد الأدبي",
        code = "ARB204",
        coefficient = 2.0,
        credits = 3,
        professorName = "د. نور الهدى صالحي",
        professorEmail = "n.salhi@ens-bouzareah.dz",
        category = "استكشافي",
        description = "علم المعاني والبيان والبديع، وتطبيقات النقد التحليلي الحديث.",
        isCachedOffline = true
      )
      dao.insertModules(listOf(mod1, mod2, mod3, mod4))

      // 5. Initial Lectures
      val lec1 = Lecture(
        id = 1,
        moduleId = 1,
        weekNumber = 1,
        title = "المحاضرة 01: نشأة النحو ونظرية العامل",
        summary = "استعراض لأسس نشأة علم النحو العربي عند البصريين والكوفيين ومفهوم العامل النحوي وتأثيره.",
        pdfFileName = "nahw_lecture_01.pdf",
        durationMinutes = 90,
        date = "الأحد 12 أكتوبر 2026",
        isCachedOffline = true
      )
      val lec2 = Lecture(
        id = 2,
        moduleId = 1,
        weekNumber = 2,
        title = "المحاضرة 02: الجملة الاسمية وأحكام المبتدأ والخبر",
        summary = "دراسة معمقة لحالات تقديم الخبر وجوباً وجوازاً، ومسوغات الابتداء بالنكرة.",
        pdfFileName = "nahw_lecture_02.pdf",
        durationMinutes = 90,
        date = "الأحد 19 أكتوبر 2026",
        isCachedOffline = true
      )
      val lec3 = Lecture(
        id = 3,
        moduleId = 2,
        weekNumber = 1,
        title = "المحاضرة 01: بنية القصيدة الجاهلية ومعلقة امرئ القيس",
        summary = "تحليل المقدمة الطللية ووصف الرحلة والليل والفرس في معلقة امرئ القيس.",
        pdfFileName = "adab_lecture_01.pdf",
        durationMinutes = 90,
        date = "الثلاثاء 14 أكتوبر 2026",
        isCachedOffline = true
      )
      dao.insertLectures(listOf(lec1, lec2, lec3))

      // 6. Initial Library References (المكتبة والمراجع العامة للتخصص)
      val ref1 = LibraryReference(
        id = 1,
        specialtyId = 1,
        title = "كتاب سيبويه (الكتاب)",
        author = "عمرو بن عثمان بن قنبر (سيبويه)",
        category = "كتاب مرجعي",
        description = "أول وأعظم كتاب منهجي يؤصل لقواعد اللغة العربية والنحو والصرف.",
        pageCount = 920,
        visibilityScope = "تخصص كامل"
      )
      val ref2 = LibraryReference(
        id = 2,
        specialtyId = 1,
        title = "لسان العرب",
        author = "ابن منظور",
        category = "معجم وقاموس",
        description = "أشمل المعاجم اللغوية التراثية في الألفاظ ودلالاتها واستعمالات العرب.",
        pageCount = 4500,
        visibilityScope = "تخصص كامل"
      )
      val ref3 = LibraryReference(
        id = 3,
        specialtyId = 1,
        title = "دلائل الإعجاز في علم المعاني",
        author = "عبد القاهر الجرجاني",
        category = "كتاب مرجعي",
        description = "المرجع التأسيسي لنظرية النظم والبلاغة والدراسات الأسلوبية.",
        pageCount = 540,
        visibilityScope = "تخصص كامل"
      )
      val ref4 = LibraryReference(
        id = 4,
        specialtyId = 1,
        title = "طبقات فحول الشعراء",
        author = "محمد بن سلّام الجمحي",
        category = "كتاب مرجعي",
        description = "تصنيف تاريخي ونقدي لشعراء الجاهلية والإسلام ومقاييس الفصاحة.",
        pageCount = 380,
        visibilityScope = "تخصص كامل"
      )
      dao.insertLibraryReferences(listOf(ref1, ref2, ref3, ref4))

      // 7. Initial Academic Calendar Events (التقويم الأكاديمي)
      val cal1 = AcademicCalendarEvent(
        id = 1,
        title = "الانطلاق الرسمي للسداسي الأول (S1)",
        eventType = "محطة رسمية",
        startDate = "04 أكتوبر 2026",
        endDate = "04 أكتوبر 2026",
        description = "بدء كافة الدروس الحضورية والمحاضرات لجميع المستويات والأطوار.",
        isCurrent = false
      )
      val cal2 = AcademicCalendarEvent(
        id = 2,
        title = "عطلة الشتاء الجامعية",
        eventType = "عطلة جامعية",
        startDate = "18 ديسمبر 2026",
        endDate = "03 جانفي 2027",
        description = "توقف الدروس والنشاطات البيداغوجية لمدة أسبوعين.",
        isCurrent = false
      )
      val cal3 = AcademicCalendarEvent(
        id = 3,
        title = "فترة امتحانات السداسي الأول (S1)",
        eventType = "فترة امتحانات",
        startDate = "10 جانفي 2027",
        endDate = "22 جانفي 2027",
        description = "الامتحانات الكتابية الرسمية للمقاييس الأساسية والمنهجية.",
        isCurrent = true
      )
      val cal4 = AcademicCalendarEvent(
        id = 4,
        title = "المداولات وإعلان نتائج السداسي الأول",
        eventType = "مداولات",
        startDate = "28 جانفي 2027",
        endDate = "31 جانفي 2027",
        description = "نشر نقاط الامتحانات والمعدلات الفردية واستقبال الطعون.",
        isCurrent = false
      )
      dao.insertCalendarEvents(listOf(cal1, cal2, cal3, cal4))

      // 8. Initial Attendance Records (سجل الحضور والغياب)
      val att1 = AttendanceRecord(
        id = 1,
        moduleName = "النحو والصرف المعمق",
        sessionType = "أعمال موجهة TD",
        date = "19 أكتوبر 2026",
        status = "حاضر",
        reason = "",
        maxAllowedAbsences = 3
      )
      val att2 = AttendanceRecord(
        id = 2,
        moduleName = "الأدب العربي القديم",
        sessionType = "أعمال موجهة TD",
        date = "21 أكتوبر 2026",
        status = "غائب",
        reason = "عذر مرضي مقبول",
        maxAllowedAbsences = 3
      )
      dao.insertAttendanceRecord(att1)
      dao.insertAttendanceRecord(att2)

      // 9. Initial Polls (استطلاعات الرأي والتصويت الصفي)
      val poll1 = ClassPoll(
        id = 1,
        creatorName = "الممثل: أمين حمدي",
        question = "ما هو الموعد الأنسب لحصة التعويض لمقياس النحو والصرف (TD)؟",
        optionA = "الثلاثاء 13:00 (قاعة 12)",
        votesA = 18,
        optionB = "الخميس 09:30 (مدرج أ)",
        votesB = 7,
        optionC = "الأربعاء 14:30 (عن بعد)",
        votesC = 4,
        userVotedOption = "الثلاثاء 13:00 (قاعة 12)",
        targetGroup = "الفوج 03",
        isClosed = false,
        createdAt = "أمس"
      )
      val poll2 = ClassPoll(
        id = 2,
        creatorName = "الممثل: أمين حمدي",
        question = "هل نطلب من الأستاذ تأجيل موعد تسليم بحث الأدب العربي ليوم الأحد؟",
        optionA = "نعم، نفضل التأجيل للأحد",
        votesA = 22,
        optionB = "لا، نسلمه في موعده الخميس",
        votesB = 3,
        userVotedOption = null,
        targetGroup = "الفوج 03",
        isClosed = false,
        createdAt = "اليوم"
      )
      dao.insertPolls(listOf(poll1, poll2))

      // 10. Initial App Users for Owner/Admin visibility and promotions
      val user1 = AppUser(
        id = 1,
        fullName = "محمد البشير بن علي",
        email = "mohamedbessghier8@gmail.com",
        studentId = "202631084592",
        specialtyName = "اللغة والأدب العربي",
        yearName = "السنة الثانية (L2)",
        groupNumber = "الفوج 03",
        role = "STUDENT",
        representativeScope = "فوج واحد"
      )
      val user2 = AppUser(
        id = 2,
        fullName = "أمين حمدي",
        email = "amine.hamdi@ens-bouzareah.dz",
        studentId = "202631081102",
        specialtyName = "اللغة والأدب العربي",
        yearName = "السنة الثانية (L2)",
        groupNumber = "الفوج 03",
        role = "REPRESENTATIVE",
        representativeScope = "فوج واحد"
      )
      val user3 = AppUser(
        id = 3,
        fullName = "د. كمال منصوري",
        email = "k.mansouri@ens-bouzareah.dz",
        studentId = "PROF-9921",
        specialtyName = "اللغة والأدب العربي",
        yearName = "كافة السنوات",
        groupNumber = "كافة الأفواج",
        role = "SPECIALTY_ADMIN",
        representativeScope = "تخصص كامل"
      )
      dao.insertUsers(listOf(user1, user2, user3))

      // 11. Initial Schedule & Exams & Announcements
      val sch1 = ScheduleItem(
        id = 1,
        specialtyId = 1,
        academicYearId = 2,
        dayOfWeek = 1,
        startTime = "08:30",
        endTime = "10:00",
        moduleName = "النحو والصرف المعمق",
        type = "محاضرة",
        room = "مدرج الجاحظ",
        professor = "أ.د. عبد القادر الفاسي",
        visibilityScope = "تخصص كامل"
      )
      val sch2 = ScheduleItem(
        id = 2,
        specialtyId = 1,
        academicYearId = 2,
        dayOfWeek = 1,
        startTime = "10:15",
        endTime = "11:45",
        moduleName = "النحو والصرف المعمق",
        type = "أعمال موجهة TD",
        room = "قاعة 14",
        professor = "أ. حسان بوعلام",
        visibilityScope = "فوج واحد",
        targetGroup = "الفوج 03"
      )
      val sch3 = ScheduleItem(
        id = 3,
        specialtyId = 1,
        academicYearId = 2,
        dayOfWeek = 3,
        startTime = "09:00",
        endTime = "10:30",
        moduleName = "الأدب العربي القديم",
        type = "محاضرة",
        room = "مدرج المتنبي",
        professor = "د. فاطمة الزهراء بن عيسى",
        visibilityScope = "تخصص كامل"
      )
      dao.insertScheduleItems(listOf(sch1, sch2, sch3))

      val ex1 = Exam(
        id = 1,
        moduleId = 1,
        moduleName = "النحو والصرف المعمق",
        title = "امتحان السداسي الأول النهائي",
        examDate = "12 جانفي 2027",
        time = "09:00 - 11:00",
        room = "مدرج الجاحظ + قاعة 14",
        coefficient = 3.0
      )
      val ex2 = Exam(
        id = 2,
        moduleId = 2,
        moduleName = "الأدب العربي القديم",
        title = "امتحان السداسي الأول النهائي",
        examDate = "15 جانفي 2027",
        time = "09:00 - 11:00",
        room = "مدرج المتنبي",
        coefficient = 3.0
      )
      dao.insertExams(listOf(ex1, ex2))

      val ann1 = Announcement(
        id = 1,
        title = "توزيع قاعات الامتحان النهائي للسداسي الأول S1",
        content = "يرجى من جميع طلبة السنة الثانية ليسانس الاطلاع على توزيع القاعات والمدرجات والتواجد قبل 15 دقيقة من انطلاق الاختبار.",
        author = "إدارة القسم",
        date = "منذ يومين",
        urgency = "هام",
        visibilityScope = "تخصص كامل"
      )
      val ann2 = Announcement(
        id = 2,
        title = "تمديد أجل إيداع بحوث الأعمال الموجهة TD (الفوج 03)",
        content = "تمت الموافقة من طرف أستاذ المقياس على تمديد تسليم بطاقات القراءة إلى غاية يوم الأحد القادم على الساعة 12:00.",
        author = "ممثل الفوج 03",
        date = "اليوم",
        urgency = "عاجل",
        visibilityScope = "فوج واحد",
        targetGroups = "الفوج 03"
      )
      dao.insertAnnouncements(listOf(ann1, ann2))

      val grade1 = StudentGrade(
        id = 1,
        moduleId = 1,
        moduleName = "النحو والصرف المعمق",
        continuousScore = 15.5,
        examScore = 14.0,
        coefficient = 3.0,
        credits = 5,
        isOfficial = true
      )
      val grade2 = StudentGrade(
        id = 2,
        moduleId = 2,
        moduleName = "الأدب العربي القديم",
        continuousScore = 16.0,
        examScore = 15.0,
        coefficient = 3.0,
        credits = 5,
        isOfficial = true
      )
      dao.insertGrades(listOf(grade1, grade2))
    }
  }
}
