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
  version = 3,
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
      // 1. Initial Student Profile (Matching the updated vision at ENS Bouzareah)
      dao.insertStudentProfile(
        StudentProfile(
          id = 1,
          fullName = "محمد البشير بن علي",
          studentId = "202631084592",
          institution = "المدرسة العليا للأساتذة - بوزريعة (ENS)",
          university = "المدرسة العليا للأساتذة - بوزريعة",
          faculty = "قسم اللغة والأدب العربي",
          specialtyName = "اللغة والأدب العربي",
          profileTrack = "ملمح أستاذ التعليم الابتدائي",
          selectedSpecialtyId = 1,
          selectedYearId = 2,
          academicYearName = "السنة الثانية",
          semesterName = "السداسي الأول (S1)",
          groupNumber = "الفوج 03",
          subGroup = "الفوج الفرعي B",
          email = "mohamedbessghier8@gmail.com",
          isAdminMode = false,
          userRole = "STUDENT",
          themePalette = "ACADEMIC"
        )
      )

      // 2. Specialties
      val specialties = listOf(
        Specialty(1, "الأدب العربي واللغويات", "LIT", "book", "دراسة علوم اللغة، النحو، البلاغة، والأدب المقارن"),
        Specialty(2, "الإعلام الآلي وتطوير البرمجيات", "CS", "laptop", "علوم الحاسوب، هياكل البيانات، الذكاء الاصطناعي، والشبكات"),
        Specialty(3, "الحقوق والعلوم السياسية", "LAW", "scale", "القانون العام والخاص، القانون الدستوري، والعلاقات الدولية"),
        Specialty(4, "العلوم الاقتصادية والتسيير", "ECO", "chart", "المحاسبة، المالية، تسيير المؤسسات، والاقتصاد الكلي"),
        Specialty(5, "العلوم الطبية والصيدلانية", "MED", "heart", "علم التشريح، الفيزيولوجيا، علم الأدوية، والتشخيص السريري")
      )
      dao.insertSpecialties(specialties)

      // 3. Academic Years
      val years = listOf(
        // For Arabic Lit (Specialty 1)
        AcademicYear(1, 1, "السنة الأولى ليسانس (L1)", 1),
        AcademicYear(2, 1, "السنة الثانية ليسانس (L2)", 1),
        AcademicYear(3, 1, "السنة الثالثة ليسانس (L3)", 1),
        AcademicYear(4, 1, "ماستر 1 لغويات تطبيقية (M1)", 1),
        // For CS (Specialty 2)
        AcademicYear(5, 2, "السنة الأولى ليسانس (L1)", 1),
        AcademicYear(6, 2, "السنة الثانية ليسانس (L2)", 1),
        AcademicYear(7, 2, "السنة الثالثة ليسانس (L3)", 1),
        // For Law (Specialty 3)
        AcademicYear(8, 3, "السنة الأولى ليسانس (L1)", 1),
        AcademicYear(9, 3, "السنة الثانية ليسانس (L2)", 1),
        AcademicYear(10, 3, "السنة الثالثة ليسانس (L3)", 1)
      )
      dao.insertAcademicYears(years)

      // 4. Modules (Arabic Lit L2 - YearId 2 & others)
      val modules = listOf(
        ModuleCourse(1, 1, 2, "النحو العربي ومسائله", "ARA-201", 3.0, 6, "أ.د. بلقاسم المنصوري", "mansouri@univ-alger.dz", "أساسي", "دراسة أصول النحو، الإعراب والبناء، ونظريات العوامل"),
        ModuleCourse(2, 1, 2, "الأدب العربي القديم وتاريخه", "ARA-202", 3.0, 5, "د. سعاد بوعلام", "boualam@univ-alger.dz", "أساسي", "نصوص العصر الجاهلي والإسلامي والأموي وتحليلها"),
        ModuleCourse(3, 1, 2, "علم البلاغة والأسلوبية", "ARA-203", 2.0, 4, "د. أحمد زروقي", "zerrouki@univ-alger.dz", "منهجي", "المعاني والبيان والبديع والتحليل الأسلوبي للنصوص"),
        ModuleCourse(4, 1, 2, "اللسانيات العامة والمقارنة", "ARA-204", 2.0, 4, "د. كريمة الشريف", "cherif@univ-alger.dz", "أساسي", "المدارس اللسانية الحديثة، البنيوية والتوليدية"),
        ModuleCourse(5, 1, 2, "منهجية البحث الأدبي", "ARA-205", 1.5, 3, "د. كمال حيمور", "haimour@univ-alger.dz", "منهجي", "تقنيات التوثيق والتحقيق وبناء المذكرات الأكاديمية"),
        ModuleCourse(6, 1, 2, "لغة أجنبية تخصصية (فرنسية/إنجليزية)", "ARA-206", 1.0, 2, "أ. مريم قدور", "kaddour@univ-alger.dz", "استكشافي", "المصطلحات النقدية واللغوية في اللغات الحية"),
        // CS Modules (Specialty 2, Year 6)
        ModuleCourse(7, 2, 6, "خوارزميات وهياكل البيانات 2", "INF-201", 3.5, 6, "د. مراد بن يحيى", "benyahia@univ-alger.dz", "أساسي", "الأشجار، الرسوم البيانية، خوارزميات الترتيب والبحث المتقدم"),
        ModuleCourse(8, 2, 6, "قواعد البيانات ونظم المعلومات", "INF-202", 3.0, 5, "د. سمير قرين", "guerin@univ-alger.dz", "أساسي", "SQL، النمذجة الكيانية، وتصميم قواعد البيانات العلائقية")
      )
      dao.insertModules(modules)

      // 5. Lectures (With Week structure, Cached text & PDFs)
      val lectures = listOf(
        Lecture(1, 1, 1, "مدخل إلى نظرية الإعراب والبناء", "مفهوم العامل والأثر الإعرابي في الجملة العربية، الفروق الجوهرية بين المبني والمعرب.", "nahw_week01_intro.pdf", "https://talib-storage.edu/pdf/nahw_01.pdf", 90, "2026-09-08", isBookmarked = true, isDownloaded = true, isCachedOffline = true, lastViewedTimestamp = System.currentTimeMillis() - 3600000, cachedContentText = "ملخص المحاضرة 1: الإعراب لغة هو الإبانة والإيضاح، واصطلاحاً هو تغير أواخر الكلم لاختلاف العوامل الداخلة عليها لفظاً أو تقديراً. أما البناء فهو لزوم آخر الكلمة حالة واحدة لغير عامل ولا اعتلال."),
        Lecture(2, 1, 2, "المبتدأ والخبر وأحكامهما الدقيقة", "تعدد الخبر، مسوغات الابتداء بالنكرة، ومواضع وجوب تقديم المبتدأ أو الخبر وحذفهما.", "nahw_week02_moubtada.pdf", "https://talib-storage.edu/pdf/nahw_02.pdf", 90, "2026-09-15", isBookmarked = true, isDownloaded = false, isCachedOffline = true, lastViewedTimestamp = System.currentTimeMillis() - 7200000, cachedContentText = "ملخص المحاضرة 2: المبتدأ هو الاسم الصريح أو المؤول بالصريح، المجرد عن العوامل اللفظية غير الزائدة، مخبراً عنه. والخبر هو الجزء المتم الفائدة مع المبتدأ. يجوز الابتداء بالنكرة إذا أفادت."),
        Lecture(3, 1, 3, "النواسخ الفعلية والحرفية (كان وإن وأخواتهما)", "عمل كان وأخواتها، كاد وأخواتها، والأحكام البلاغية والتركيبية لدخول إن وأن.", "nahw_week03_nawasikh.pdf", "https://talib-storage.edu/pdf/nahw_03.pdf", 90, "2026-09-22", isBookmarked = false, isDownloaded = true, isCachedOffline = true, lastViewedTimestamp = 0L, cachedContentText = "ملخص المحاضرة 3: النواسخ جمع ناسخ وهي التي تنسخ حكم المبتدأ والخبر، وتنقسم إلى أفعال ناقصة (كان وأخواتها) ترفع المبتدأ وتنصب الخبر، وحروف مشبهة بالفعل (إن وأخواتها) تنصب المبتدأ وترفع الخبر."),
        Lecture(4, 1, 4, "المفاعيل الخمسة واستعمالاتها في التراكيب", "المفعول به، المفعول المطلق، المفعول لأجله، المفعول فيه، والمفعول معه وتطبيقات إعرابية.", "nahw_week04_mafaeel.pdf", "https://talib-storage.edu/pdf/nahw_04.pdf", 90, "2026-09-29", isBookmarked = false, isDownloaded = false, isCachedOffline = true, lastViewedTimestamp = 0L, cachedContentText = "ملخص المحاضرة 4: المنصوبات من الأسماء: المفعول به وهو ما وقع عليه فعل الفاعل، والمفعول المطلق وهو المصدر المؤكد لعامله أو المبين لنوعه أو عدده، والمفعول له (لأجله)، والمفعول فيه (ظرف الزمان والمكان)، والمفعول معه."),
        Lecture(5, 2, 1, "نشأة الشعر الجاهلي وقضايا التوثيق", "بيئة الشعر العربي القديم، قضية الانتحال عند ابن سلام وطه حسين، وشعراء المعلقات.", "adab_week01_jahili.pdf", "https://talib-storage.edu/pdf/adab_01.pdf", 90, "2026-09-09", isBookmarked = false, isDownloaded = true, isCachedOffline = true, lastViewedTimestamp = System.currentTimeMillis() - 86400000, cachedContentText = "ملخص المحاضرة 1: البيئة الجاهلية وأثر الصحراء في تشكيل المخيال الشعري. مناقشة كتاب طبقات فحول الشعراء لابن سلام وقضية الوضع والانتحال."),
        Lecture(6, 2, 2, "معلقة امرئ القيس: دراسة بنيوية ونقدية", "تحليل مطالع المعلقات، الصورة الفنية، والتشبيهات البدوية في قفا نبك من ذكرى حبيب.", "adab_week02_imru.pdf", "https://talib-storage.edu/pdf/adab_02.pdf", 90, "2026-09-16", isBookmarked = true, isDownloaded = false, isCachedOffline = true, lastViewedTimestamp = 0L, cachedContentText = "ملخص المحاضرة 2: قفا نبك من ذكرى حبيب ومنزل بسقط اللوى بين الدخول فحومل. تحليل الرحلة والغزل ووصف الفرس والليل والسيل."),
        Lecture(7, 3, 1, "علم البيان: التشبيه وأقسامه البلاغية", "أركان التشبيه، التشبيه البليغ والتمثيلي، وجماليات التجسيم والتشخيص.", "balagha_week01_tashbih.pdf", "https://talib-storage.edu/pdf/balagha_01.pdf", 80, "2026-09-10", isBookmarked = false, isDownloaded = false, isCachedOffline = true, lastViewedTimestamp = 0L, cachedContentText = "ملخص المحاضرة 1: أركان التشبيه أربعة: المشبه، المشبه به، أداة التشبيه، ووجه الشبه. وإذا حذفت الأداة ووجه الشبه سمي تشبيهاً بليغاً."),
        Lecture(8, 4, 1, "دي سوسير وتأسيس اللسانيات الحديثة", "الثنائيات السوسيرية: اللسان والكلام، الدال والمدلول، والتزامنية والتعاقبية.", "lissaniyat_week01_saussure.pdf", "https://talib-storage.edu/pdf/lissaniyat_01.pdf", 90, "2026-09-11", isBookmarked = false, isDownloaded = false, isCachedOffline = true, lastViewedTimestamp = 0L, cachedContentText = "ملخص المحاضرة 1: محاضرة في اللسانيات العامة 1916. مبدأ اعتباطية العلامة اللسانية والعلاقات الاستبدالية والسياقية.")
      )
      dao.insertLectures(lectures)

      // 5.5 Cached Course Materials for Offline Reading
      val cachedMaterials = listOf(
        CachedCourseMaterial(
          id = 1,
          moduleId = 1,
          moduleName = "النحو العربي ومسائله",
          title = "ملخص شامل: علامات الإعراب الأصلية والفرعية",
          materialType = "ملخص دراسي",
          summary = "جدول مبسط لحالات الإعراب (الرفع، النصب، الجر، الجزم) في المفرد والمثنى وجمع المذكر السالم والمؤنث والأسماء الخمسة والممنوع من الصرف.",
          fullText = "المفرد: يرفع بالضمة وينصب بالفتحة ويجر بالكسرة.\nالمثنى: يرفع بالألف وينصب ويجر بالياء.\nجمع المذكر السالم: يرفع بالواو وينصب ويجر بالياء.\nجمع المؤنث السالم: يرفع بالضمة وينصب ويجر بالكسرة نيابة عن الفتحة.\nالأسماء الخمسة: ترفع بالواو وتنصب بالألف وتجر بالياء شريطة إضافتها لغير ياء المتكلم.",
          keyConcepts = "الإعراب التقديري، الأسماء الخمسة، الممنوع من الصرف",
          weekNumber = 1,
          lastViewedTimestamp = System.currentTimeMillis() - 1800000
        ),
        CachedCourseMaterial(
          id = 2,
          moduleId = 1,
          moduleName = "النحو العربي ومسائله",
          title = "سلسلة تمارين الأعمال الموجهة TD - الأسبوع 1 و 2",
          materialType = "أعمال موجهة TD",
          summary = "نماذج إعرابية تطبيقية وتمارين استخراج المبتدأ والخبر مع الحلول النموذجية المقررة في حصة الفوج.",
          fullText = "التمرين 1: أعرب قوله تعالى 'الحمد لله رب العالمين'.\nالحمد: مبتدأ مرفوع وعلامة رفعه الضمة الظاهرة على آخره.\nلله: اللام حرف جر، لفظ الجلالة اسم مجرور، والجار والمجرور شبه جملة متعلق بمحذوف خبر في محل رفع.\nرب: نعت مجرور بالكسرة.\nالعالمين: مضاف إليه مجرور وعلامة جره الياء لأنه ملحق بجمع المذكر السالم.",
          keyConcepts = "إعراب نموذجي، شبه الجملة، الملحقات",
          weekNumber = 2,
          lastViewedTimestamp = System.currentTimeMillis() - 3600000
        ),
        CachedCourseMaterial(
          id = 3,
          moduleId = 2,
          moduleName = "الأدب العربي القديم وتاريخه",
          title = "قراءة نقدية في منهج ابن سلام الجمحي",
          materialType = "محاضرة",
          summary = "دراسة كتاب طبقات فحول الشعراء وتقسيم الشعراء إلى طبقات جاهلية وإسلامية ومفهوم الفحولة النقدية.",
          fullText = "اعتمد ابن سلام معايير لغوية وفنية في تصنيف الشعراء إلى 10 طبقات جاهلية و10 طبقات إسلامية، في كل طبقة 4 شعراء. وجعل الطبقة الأولى: امرؤ القيس، النابغة الذبياني، زهير بن أبي سلمى، والأعشى.",
          keyConcepts = "الفحولة الأدبية، الطبقات، التوثيق النقدي",
          weekNumber = 1,
          lastViewedTimestamp = System.currentTimeMillis() - 5400000
        ),
        CachedCourseMaterial(
          id = 4,
          moduleId = 7,
          moduleName = "خوارزميات وهياكل البيانات 2",
          title = "خوارزميات أشجار البحث الثنائية (BST) والتعقيد الزمني",
          materialType = "ملخص دراسي",
          summary = "العمليات الأساسية على الأشجار الثنائية: الإضافة (Insertion)، البحث (Search)، والحذف (Deletion) وحساب O(log n).",
          fullText = "Binary Search Tree (BST):\nفي كل عقدة Node:\n- القيم في الفرع الأيسر أقل من قيمة العقدة الحالية.\n- القيم في الفرع الأيمن أكبر من قيمة العقدة الحالية.\nزمن البحث في الحالة المتوسطة هو O(log n) وفي أسوأ الحالات O(n) عند اختلال التوازن (Unbalanced Tree).",
          keyConcepts = "Binary Search Tree, Big-O, Recursion, In-Order Traversal",
          weekNumber = 2,
          lastViewedTimestamp = System.currentTimeMillis() - 9000000
        )
      )
      dao.insertCachedMaterials(cachedMaterials)

      // 6. Assignments
      val assignments = listOf(
        Assignment(1, 1, "إعراب قصيدة كعب بن زهير (بانت سعاد)", "2026-09-25", "استخراج النواسخ والمفاعيل وإعراب الأبيات العشرة الأولى إعراباً مفصلاً.", isCompleted = true, 20.0),
        Assignment(2, 2, "بحث تحليلي حول أطلال المعلقات السبع", "2026-10-02", "كتابة ورقة بحثية في 5 صفحات حول دلالة البكاء على الأطلال في الشعر القديم.", isCompleted = false, 20.0),
        Assignment(3, 3, "استخراج 5 تشبيهات تمثيلية من القرآن الكريم", "2026-10-08", "بيان وجه الشبه وسر البلاغة وتقديمها في حصة الأعمال الموجهة TD.", isCompleted = false, 20.0)
      )
      dao.insertAssignments(assignments)

      // 7. Schedule Items (Weekly timetable)
      val schedule = listOf(
        // Sunday (1)
        ScheduleItem(1, 1, 2, 1, "08:30", "10:00", "النحو العربي ومسائله", "محاضرة", "مدرج ابن خلدون", "أ.د. بلقاسم المنصوري"),
        ScheduleItem(2, 1, 2, 1, "10:15", "11:45", "النحو العربي ومسائله", "أعمال موجهة TD", "قاعة 12 (الطابق 1)", "د. زينب مداني"),
        ScheduleItem(3, 1, 2, 1, "13:00", "14:30", "منهجية البحث الأدبي", "محاضرة", "قاعة المحاضرات الكبرى", "د. كمال حيمور"),
        // Monday (2)
        ScheduleItem(4, 1, 2, 2, "08:30", "10:00", "الأدب العربي القديم وتاريخه", "محاضرة", "مدرج الجاحظ", "د. سعاد بوعلام"),
        ScheduleItem(5, 1, 2, 2, "10:15", "11:45", "الأدب العربي القديم", "أعمال موجهة TD", "قاعة 08", "أ. سليم بوعزيز"),
        // Tuesday (3)
        ScheduleItem(6, 1, 2, 3, "08:30", "10:00", "علم البلاغة والأسلوبية", "محاضرة", "مدرج سيبويه", "د. أحمد زروقي"),
        ScheduleItem(7, 1, 2, 3, "10:15", "11:45", "اللسانيات العامة والمقارنة", "محاضرة", "مدرج ابن رشد", "د. كريمة الشريف"),
        // Wednesday (4)
        ScheduleItem(8, 1, 2, 4, "09:00", "10:30", "علم البلاغة (تطبيقات)", "أعمال موجهة TD", "قاعة 14", "د. أحمد زروقي"),
        ScheduleItem(9, 1, 2, 4, "11:00", "12:30", "لغة أجنبية تخصصية", "أعمال تطبيقية TP", "مخبر اللغات 2", "أ. مريم قدور"),
        // Thursday (5)
        ScheduleItem(10, 1, 2, 5, "08:30", "10:30", "ورشة القراءة والتحقيق", "أعمال موجهة TD", "مكتبة الكلية (قاعة 3)", "د. كمال حيمور")
      )
      dao.insertScheduleItems(schedule)

      // 8. Exams
      val exams = listOf(
        Exam(1, 1, "النحو العربي ومسائله", "امتحان منتصف السداسي (Control 1)", "2026-10-18", "09:00 - 10:30", "مدرج ابن خلدون", 2.0, isFinished = false),
        Exam(2, 2, "الأدب العربي القديم", "امتحان تقييم الأعمال الموجهة TD", "2026-10-22", "11:00 - 12:30", "مدرج الجاحظ", 1.5, isFinished = false),
        Exam(3, 3, "علم البلاغة والأسلوبية", "امتحان السداسي الأول النهائي", "2026-11-15", "08:30 - 11:30", "مدرج سيبويه", 3.0, isFinished = false),
        Exam(4, 4, "اللسانيات العامة", "الامتحان النهائي للسداسي الأول", "2026-11-19", "13:00 - 15:30", "مدرج ابن رشد", 2.5, isFinished = false)
      )
      dao.insertExams(exams)

      // 9. Grades & GPA (Showing explicit distinction between official administration grades & student estimates)
      val grades = listOf(
        StudentGrade(1, 1, "النحو العربي ومسائله", 16.5, 15.0, 3.0, 6, isOfficial = true, targetScore = 10.0),
        StudentGrade(2, 2, "الأدب العربي القديم وتاريخه", 15.0, 14.5, 3.0, 5, isOfficial = true, targetScore = 10.0),
        StudentGrade(3, 3, "علم البلاغة والأسلوبية", 17.0, 16.0, 2.0, 4, isOfficial = true, targetScore = 10.0),
        StudentGrade(4, 4, "اللسانيات العامة والمقارنة", 14.0, 13.5, 2.0, 4, isOfficial = false, targetScore = 12.0),
        StudentGrade(5, 5, "منهجية البحث الأدبي", 16.0, 17.0, 1.5, 3, isOfficial = false, targetScore = 10.0),
        StudentGrade(6, 6, "لغة أجنبية تخصصية", 18.0, 17.5, 1.0, 2, isOfficial = false, targetScore = 10.0)
      )
      dao.insertGrades(grades)

      // 10. Announcements (With Urgency & Content Visibility Scopes: تخصص كامل / عدة أفواج / فوج محدد)
      val announcements = listOf(
        Announcement(1, "تغيير توقيت محاضرة النحو العربي لهذا الأسبوع", "نعلم طلبة ملمح ابتدائي الفوج 03 أن محاضرة النحو العربي المقررة يوم الأحد ستقدم إلى الساعة 08:30 بمدرج ابن خلدون بدلاً من المدرج 2.", "أ.د. بلقاسم المنصوري", "اليوم - 09:15", "عاجل", 1, isRead = false, visibilityScope = "فوج واحد", targetGroups = "الفوج 03"),
        Announcement(2, "فتح باب الترشح لمنصب ممثل الدفعة (Délégué)", "تعلن إدارة المدرسة العليا للأساتذة عن انطلاق استقبال ملفات الترشح لتمثيل الطلبة في اللجان البيداغوجية.", "إدارة الشؤون الطلابية", "أمس", "هام", null, isRead = false, visibilityScope = "تخصص كامل", targetGroups = "الكل"),
        Announcement(3, "توزيع بطاقات المنحة والنقل للطلبة", "يرجى التقرب من مصلحة الخدمات الجامعية مصحوبين بالوثائق الثبوتية وبطاقة الطالب لاستلام بطاقة المنحة.", "مصلحة المنح والأنشطة", "منذ يومين", "عام", null, isRead = true, visibilityScope = "تخصص كامل", targetGroups = "الكل"),
        Announcement(4, "نشر ملخصات الأسبوع 3 لمقياس اللسانيات والأعمال الموجهة", "تم رفع الملفات الرقمية بصيغة PDF وقائمة المراجع التكميلية في قسم المحاضرات الرقمية ومساحة ملفاتي.", "د. كريمة الشريف", "منذ 3 أيام", "هام", 1, isRead = false, visibilityScope = "عدة أفواج محددة", targetGroups = "الفوج 01، الفوج 03")
      )
      dao.insertAnnouncements(announcements)

      // 11. Initial Student Personal Notes (ملفاتي)
      val notes = listOf(
        StudentNote(1, "قواعد إعراب الجمل التي لها محل من الإعراب", "الجمل سبع: الخبرية، الحالية، المفعولية، المضاف إليها، جواب الشرط الجازم المقترن بالفاء أو إذا الفجائية، التابعة لمفرد، والتابعة لجملة لها محل.", "النحو العربي ومسائله", "منذ يومين", "#1B5E4B"),
        StudentNote(2, "فروق أساسية بين التشبيه البليغ والاستعارة المكنية", "التشبيه البليغ يذكر فيه الطرفان (المشبه والمشبه به) بدون أداة ووجه شبه. إذا حُذف المشبه به ورُمز له بشيء من لوازمه صارت استعارة مكنية.", "علم البلاغة والأسلوبية", "أمس", "#C8956C"),
        StudentNote(3, "ملاحظات تحضير مذكرة التخرج وملمح التعليم الابتدائي", "مراجعة كفاءات المنهاج الدراسي للطور الابتدائي والتركيز على طرائق التدريس النشطة في تدريس القراءة والكتابة.", "منهجية البحث الأدبي", "اليوم", "#10B981")
      )
      notes.forEach { dao.insertNote(it) }
    }
  }
}
