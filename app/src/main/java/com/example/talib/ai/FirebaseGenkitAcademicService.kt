package com.example.talib.ai

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class StudentAcademicContext(
  val studentName: String = "طالب",
  val specialty: String = "",
  val academicYear: String = "",
  val currentModules: List<String> = emptyList(),
  val upcomingExams: List<String> = emptyList()
)

class FirebaseGenkitAcademicService(private val context: Context) {

  private val tag = "TalibGenkitAI"

  private var generativeModel: GenerativeModel? = null
  private var isFirebaseAiInitialized = false

  init {
    tryInitializeFirebaseAi()
  }

  private fun tryInitializeFirebaseAi() {
    try {
      generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-2.5-flash",
        generationConfig = generationConfig {
          temperature = 0.7f
          topK = 40
          topP = 0.95f
        },
        systemInstruction = content {
          text(
            """
            أنت "المساعد الأكاديمي الذكي 24/7" المدمج في منصة 'طالب' (Talib App) للطلبة الجامعيين في الجزائر والعالم العربي.
            مهمتك تقديم الدعم الدراسي والأكاديمي على مدار الساعة بأسلوب دقيق، علمي، مشجع، وباللغة العربية الفصحى الواضحة مع إدراج المصطلحات الفرنسية أو الإنجليزية عند الحاجة.
            اختصاصاتك تشمل:
            1. شرح الدروس والمفاهيم الصعبة (النحو، الأدب، البلاغة، اللسانيات، الخوارزميات، قواعد البيانات، القانون، الطب، الاقتصاد).
            2. تقديم ملخصات مكثفة للمحاضرات ونقاط المراجعة المركزة قبل الامتحانات.
            3. المساعدة في حل تمارين الأعمال الموجهة (TD) والواجبات المنزلية مع خطوات الشرح.
            4. تقديم نصائح منهجية البحث العلمي وبناء مذكرات التخرج ومراجع التوثيق (APA، منهجية التحقيق).
            5. حساب التقديرات الأكاديمية وتقديم خطط مراجعة واستراتيجيات لرفع المعدل الفصلي.
            قدم إجاباتك بتنسيق منظم يتضمن نقاطاً واضحة وعناوين فرعية لتسهيل القراءة والمراجعة.
            """.trimIndent()
          )
        }
      )
      isFirebaseAiInitialized = true
      Log.i(tag, "Firebase Genkit AI GenerativeModel initialized successfully.")
    } catch (e: Throwable) {
      Log.w(tag, "Firebase AI not initialized or configuration pending: ${e.message}. Fallback engine active.")
      isFirebaseAiInitialized = false
    }
  }

  suspend fun askAcademicAssistant(
    userPrompt: String,
    academicContext: StudentAcademicContext
  ): Pair<String, List<String>> = withContext(Dispatchers.IO) {
    val cleanPrompt = userPrompt.trim()
    if (cleanPrompt.isEmpty()) {
      return@withContext Pair("يرجى كتابة سؤالك أو الموضوع الأكاديمي الذي ترغب في مراجعته.", emptyList())
    }

    // If Firebase AI is active, attempt remote call
    if (isFirebaseAiInitialized && generativeModel != null) {
      try {
        val contextualPrompt = buildString {
          appendLine("سياق الطالب الأكاديمي:")
          appendLine("- الاسم: ${academicContext.studentName}")
          appendLine("- التخصص: ${academicContext.specialty}")
          appendLine("- المستوى: ${academicContext.academicYear}")
          if (academicContext.currentModules.isNotEmpty()) {
            appendLine("- المقاييس المسجلة: ${academicContext.currentModules.joinToString(", ")}")
          }
          if (academicContext.upcomingExams.isNotEmpty()) {
            appendLine("- الامتحانات القريبة: ${academicContext.upcomingExams.joinToString(", ")}")
          }
          appendLine("السؤال: $cleanPrompt")
        }

        val response = generativeModel!!.generateContent(contextualPrompt)
        val text = response.text
        if (!text.isNullOrBlank()) {
          val suggestions = generateDynamicFollowUps(cleanPrompt)
          return@withContext Pair(text, suggestions)
        }
      } catch (e: Throwable) {
        Log.e(tag, "Error during Firebase AI call: ${e.message}. Using Academic Knowledge Engine fallback.")
      }
    }

    // 24/7 Academic Knowledge Engine fallback
    val fallbackAnswer = generateComprehensiveAcademicAnswer(cleanPrompt, academicContext)
    val followUps = generateDynamicFollowUps(cleanPrompt)
    return@withContext Pair(fallbackAnswer, followUps)
  }

  private fun generateComprehensiveAcademicAnswer(
    query: String,
    ctx: StudentAcademicContext
  ): String {
    val q = query.lowercase()

    return when {
      // 1. Nahw & Grammar
      q.contains("مبتدأ") || q.contains("خبر") -> {
        """
        📘 **شرح مفصل: المبتدأ والخبر وأحكامهما**
        
        • **المبتدأ:** اسم صريح أو مؤول مجرد عن العوامل اللفظية غير الزائدة، مخبراً عنه، وحكمه الرفع دائماً.
        • **الخبر:** هو اللفظ المتم للفائدة مع المبتدأ، وحكمه الرفع أيضاً.
        
        🔍 **مسوغات الابتداء بالنكرة:**
        الأصل ألا يبتدأ إلا بمعرفة، لكن يجوز الابتداء بالنكرة إذا أفادت، ومن أشهر مسوغاتها:
        1. **أن توصف النكرة:** مثل: *رجلٌ كريمٌ عندنا*.
        2. **أن تضاف إلى نكرة:** مثل: *طالبُ علمٍ حاضرٌ*.
        3. **أن يسبقها نفي أو استفهام:** مثل: *ما أحدٌ غائبٌ*، أو *هل طالبٌ مجتهدٌ؟*.
        4. **أن يتقدم عليها خبر شبه جملة:** مثل: *في الجامعة قاعاتٌ واسعة*.
        
        💡 **نصيحة الامتحان:** احرص دائماً على تحديد رابط الجملة إذا وقع الخبر جملة اسمية أو فعلية.
        """.trimIndent()
      }

      q.contains("إعراب") || q.contains("اعراب") || q.contains("نحو") -> {
        """
        ✍️ **قواعد الإعراب التأسيسية ومنهجية الحل**
        
        • **الإعراب:** هو الأثر الظاهر أو المقدر الذي يجلبه العامل في آخر الكلمة.
        • **أنواع الإعراب:**
          - **الرفع:** علامته الأصلية الضمة (الواو لجمع المذكر والأسماء الخمسة، الألف للمثنى، ثبوت النون للأفعال الخمسة).
          - **النصب:** علامته الأصلية الفتحة (الياء للمثنى وجمع المذكر، الكسرة لجمع المؤنث السالم، حذف النون للأفعال الخمسة).
          - **الجر:** علامته الأصلية الكسرة (الياء للمثنى وجمع المذكر السالم، والفتحة للممنوع من الصرف).
          - **الجزم:** خاص بالأفعال، علامته السكون (أو حذف حرف العلة وحذف النون).
        
        🎯 **خطوات الإعراب النموذجي:**
        1. حدد نوع الجملة (فعلية أم اسمية).
        2. ابحث عن الأركان الأساسية (فعل + فاعل، أو مبتدأ + خبر).
        3. ابحث عن المتممات والمفاعيل (به، مطلق، لأجله، فيه، معه) أو التوابع (نعت، عطف، توكيد، بدل).
        """.trimIndent()
      }

      // 2. Data structures, CS, Algorithms
      q.contains("خوارزم") || q.contains("algorithm") || q.contains("بحث ثنائي") || q.contains("binary search") -> {
        """
        💻 **خوارزمية البحث الثنائي (Binary Search)**
        
        • **الشرط الأساسي:** يجب أن تكون المصفوفة مرتبة مسبقاً (Sorted Array).
        • **مبدأ العمل (Divide and Conquer):**
          1. حساب العنصر الأوسط `mid = low + (high - low) / 2`.
          2. إذا كان `array[mid] == target`، تم إيجاد العنصر بنجاح.
          3. إذا كان الهدف أصغر، نبحث في النصف الأيسر: `high = mid - 1`.
          4. إذا كان الهدف أكبر، نبحث في النصف الأيمن: `low = mid + 1`.
        
        ⚡ **التعقيد الزمني (Time Complexity):**
        - أفضل حالة (Best): **O(1)**
        - الحالة المتوسطة والأسوأ (Worst & Average): **O(log n)**
        - التعقيد الفضائي (Space Complexity): **O(1)** في الصيغة التكرارية (Iterative).
        
        💡 مقارنة: البحث الخطي (Linear Search) يستغرق O(n)، لذا فالبحث الثنائي أسرع بمراحل في البيانات الكبيرة.
        """.trimIndent()
      }

      q.contains("شجرة") || q.contains("tree") || q.contains("bst") -> {
        """
        🌲 **أشجار البحث الثنائية (Binary Search Tree - BST)**
        
        • **التعريف:** هيكل بيانات غير خطي يتكون من عقد (Nodes).
        • **الخاصية الأساسية:** لكل عقدة X:
          - جميع عناصر الشجرة الفرعية اليسرى أصغر من X.
          - جميع عناصر الشجرة الفرعية اليمنى أكبر من X.
          
        🔄 **طرق المرور الأساسية (Traversals):**
        1. **In-order (يسار -> جذر -> يمين):** يطبع العناصر مرتبة تصاعدياً.
        2. **Pre-order (جذر -> يسار -> يمين):** مفيد في نسخ الشجرة وتسلسلها.
        3. **Post-order (يسار -> يمين -> جذر):** ممتاز في حذف الشجرة وحساب المساحة.
        """.trimIndent()
      }

      // 3. GPA & Exam prep
      q.contains("معدل") || q.contains("حساب") || q.contains("gpa") -> {
        """
        📊 **طريقة حساب المعدل الفصلي والتراكمي (LMD)**
        
        يُحسب المعدل الفصلي وفق القاعدة الوزارية التالية:
        **المعدل الفصلي = مجموع (علامة المقياس × معامله) ÷ مجموع المعاملات**
        
        حيث علامة المقياس = `(علامة الامتحان × 0.6) + (علامة الأعمال الموجهة TD × 0.4)`
        
        🎯 **استراتيجية التفوق:**
        - ركّز على المقاييس ذات المعامل المرتفع (المعامل 3 فأكثر) لأنها تشكل 60% من وزن المعدل.
        - احرص على نيل 14 فأكثر في نقاط المراقبة المستمرة (TD) لضمان قاعدة أمان قوية.
        - تطبيق طالب يحسب لك المعدل آلياً في قسم **العلامات والمعدل**.
        """.trimIndent()
      }

      q.contains("امتحان") || q.contains("مراجعة") || q.contains("نصائح") -> {
        """
        🎯 **خطة المراجعة الذكية والتحضير للامتحانات الجامعية**
        
        1. **تقنية الاسترجاع النشط (Active Recall):** لا تكتفِ بإعادة قراءة الملخصات، بل أغلق الورقة واشرح الدرس بصوت مسموع أو اكتب النقاط الرئيسية من الذاكرة.
        2. **التكرار المتباعد (Spaced Repetition):** راجع المحاضرة بعد 24 ساعة، ثم بعد 3 أيام، ثم قبل الامتحان بأسبوع.
        3. **حل مواضيع السنوات السابقة:** أكثر من 70% من أنماط أسئلة الامتحانات الجامعية تتكرر في هيكلها وصيغتها.
        4. **جدول زمني متوازن:** قسم يومك إلى فترات 45 دقيقة دراسة + 10 دقائق راحة (تقنية بومودورو).
        
        ✨ تذكر: فريق المساعد الذكي متاح 24/7 للإجابة عن أي تساؤل أثناء مراجعتك!
        """.trimIndent()
      }

      q.contains("شعر") || q.contains("أدب") || q.contains("معلقات") -> {
        """
        📜 **الأدب العربي القديم: المعلقات وقضايا الشعر الجاهلي**
        
        • **المعلقات:** قصائد طوال من خيرة ما أنتجته القريحة العربية قبل الإسلام، سميت بذلك لأنها عُلّقت في الأذهان أو على أستار الكعبة.
        • **أصحاب المعلقات السبع المشهورة:**
          1. **امرؤ القيس:** صاحب "قفا نبكِ من ذكرى حبيبٍ ومنزلِ".
          2. **طرفة بن العبد:** "لِخَوْلَةَ أطْلاَلٌ بِبُرْقَةِ ثَهْمَدِ".
          3. **زهير بن أبي سلمى:** حكيم الشعراء، "أَمِنْ أُمِّ أَوْفَى دِمْنَةٌ لَمْ تَكَلَّمِ".
          4. **لبيد بن ربيعة:** "عَفَتِ الدِّيَارُ مَحَلُّهَا فَمُقَامُهَا".
          5. **عمرو بن كلثوم:** صاحب الفخر، "أَلاَ هُبِّي بِصَحْنِكِ فَاصْبَحِيْنَا".
          6. **عنترة بن شداد:** فارس بني عبس، "هَلْ غَادَرَ الشُّعَرَاءُ مِنْ مُتَرَدَّمِ".
          7. **الحارث بن حلزة:** "آذَنَتْنَا بِبَيْنِهَا أَسْمَاءُ".
        
        🔍 **قضية الانتحال:** أثارها ابن سلام في طبقات فحول الشعراء وأعاد طرحها طه حسين في العصر الحديث، وتقوم على فكرة دخول أبيات ليست لشعراء الجاهلية بفعل الرواة أو التعصب القبلي.
        """.trimIndent()
      }

      // Default comprehensive academic response
      else -> {
        """
        🎓 **مرحباً بك يا ${ctx.studentName}!**
        
        بصفتي **المساعد الأكاديمي الذكي 24/7** لمنصة طالب في تخصص **${ctx.specialty}**:
        
        تلقيت استفسارك حول: **"$query"**.
        
        📚 **التوجيه الأكاديمي المقترح:**
        1. **ربط المفهوم بالمقرر الدراسي:** يمكنك مراجعة المحاضرات والملخصات المخزنة في تبويب **المقررات** و**المحاضرات** للحصول على المراجع التخصصية المعتمدة.
        2. **النقاط الجوهرية للتركيز:** حدد المفاهيم الأساسية، القوانين أو القواعد النحوية والاصطلاحية، وقم بربطها بالأمثلة التطبيقية من حصص الأعمال الموجهة TD.
        3. **المراجعة الدائمة:** كافة المحاضرات والملخصات التي تفتحها في التطبيق يتم تخزينها تلقائياً في قاعدة بيانات الذاكرة المحلية (Room Database) لتقرأها وتراجعها حتى لو انقطع اتصال الإنترنت!
        
        هل ترغب في أن أشرح لك جانباً محدداً أو أقدم لك تمريناً تدريبياً في هذا الباب؟
        """.trimIndent()
      }
    }
  }

  private fun generateDynamicFollowUps(prompt: String): List<String> {
    val p = prompt.lowercase()
    return when {
      p.contains("نحو") || p.contains("مبتدأ") || p.contains("إعراب") -> listOf(
        "أعطني نموذج إعراب تطبيقي مفصل",
        "ما الفرق بين الخبر المفرد وشبه الجملة؟",
        "اختبرني بسؤال نحوي في المبتدأ والخبر"
      )
      p.contains("خوارزم") || p.contains("algorithm") || p.contains("بحث") -> listOf(
        "اكتب كود البحث الثنائي بلغة Python/Java",
        "ما الفرق بين O(n) و O(log n)؟",
        "اشرح لي خوارزميات الترتيب السريع Quick Sort"
      )
      p.contains("امتحان") || p.contains("معدل") || p.contains("مراجعة") -> listOf(
        "كيف أحسب علامة TD والامتحان معاً؟",
        "ضع لي جدول مراجعة لـ 5 أيام قبل الامتحان",
        "ما هي أهم الأخطاء الشائعة في الامتحانات؟"
      )
      else -> listOf(
        "لخص لي هذا المفهوم في 3 نقاط",
        "أعطني سؤال امتحان متوقع حوله",
        "كيف أربط هذا بمقرر الفصل الحالي؟"
      )
    }
  }
}
