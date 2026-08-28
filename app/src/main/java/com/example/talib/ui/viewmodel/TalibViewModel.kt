package com.example.talib.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.talib.ai.*
import com.example.talib.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TalibViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TalibRepository
  private val aiService = FirebaseGenkitAcademicService(application)

  init {
    val db = TalibDatabase.getDatabase(application, viewModelScope)
    repository = TalibRepository(db.talibDao())
  }

  // UI Theme state
  private val _isDarkMode = MutableStateFlow(false)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  fun toggleTheme() {
    _isDarkMode.value = !_isDarkMode.value
  }

  // Academic Emerald & Cream vs Modern Violet Palette
  private val _isAcademicTheme = MutableStateFlow(true)
  val isAcademicTheme: StateFlow<Boolean> = _isAcademicTheme.asStateFlow()

  fun toggleThemePalette() {
    _isAcademicTheme.value = !_isAcademicTheme.value
  }

  // Student Notes (ملفاتي)
  val allNotes: StateFlow<List<StudentNote>> = repository.allNotes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun addNote(title: String, content: String, moduleName: String = "عام", colorHex: String = "#1B5E4B") {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertNote(
        StudentNote(
          title = title,
          content = content,
          moduleName = moduleName,
          createdAt = "اليوم",
          colorHex = colorHex
        )
      )
    }
  }

  fun deleteNote(note: StudentNote) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteNote(note)
    }
  }

  // Global Loading State for Backend & Course Content Fetching
  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _loadingMessage = MutableStateFlow<String?>("جاري مزامنة المحتوى الأكاديمي...")
  val loadingMessage: StateFlow<String?> = _loadingMessage.asStateFlow()

  fun setLoading(loading: Boolean, message: String? = null) {
    _loadingMessage.value = message ?: "جاري معالجة الطلب..."
    _isLoading.value = loading
  }

  fun refreshCourseContent(message: String = "جاري جلب المقررات والمحاضرات من الخادم...") {
    viewModelScope.launch {
      _loadingMessage.value = message
      _isLoading.value = true
      // Simulate asynchronous network roundtrip to academic backend
      kotlinx.coroutines.delay(1000)
      _isLoading.value = false
    }
  }

  // Active Navigation Screen
  private val _currentScreen = MutableStateFlow(ScreenRoute.HOME)
  val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

  fun navigateTo(screen: ScreenRoute) {
    _currentScreen.value = screen
  }

  fun navigateBack() {
    _currentScreen.value = ScreenRoute.HOME
  }

  // Student Profile
  val studentProfile: StateFlow<StudentProfile?> = repository.studentProfile
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Specialties
  val specialties: StateFlow<List<Specialty>> = repository.allSpecialties
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Specialty ID & Year ID
  private val _selectedSpecialtyId = MutableStateFlow<Long>(1)
  val selectedSpecialtyId: StateFlow<Long> = _selectedSpecialtyId.asStateFlow()

  private val _selectedYearId = MutableStateFlow<Long>(2)
  val selectedYearId: StateFlow<Long> = _selectedYearId.asStateFlow()

  fun selectSpecialty(specialtyId: Long) {
    if (_selectedSpecialtyId.value != specialtyId) {
      _selectedSpecialtyId.value = specialtyId
      refreshCourseContent("جاري جلب مقررات التخصص من الخادم...")
    }
  }

  fun selectYear(yearId: Long) {
    if (_selectedYearId.value != yearId) {
      _selectedYearId.value = yearId
      refreshCourseContent("جاري تحميل مقررات السنة الدراسية...")
    }
  }

  // Academic Years for chosen specialty
  val academicYearsForSpecialty: StateFlow<List<AcademicYear>> = _selectedSpecialtyId
    .flatMapLatest { specId -> repository.getYearsForSpecialty(specId) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Modules for selected specialty and year
  val currentModules: StateFlow<List<ModuleCourse>> = combine(
    _selectedSpecialtyId,
    _selectedYearId
  ) { specId, yrId ->
    Pair(specId, yrId)
  }.flatMapLatest { (specId, yrId) ->
    repository.getModulesForSpecialtyAndYear(specId, yrId)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allModules: StateFlow<List<ModuleCourse>> = repository.allModules
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Module for details
  private val _selectedModule = MutableStateFlow<ModuleCourse?>(null)
  val selectedModule: StateFlow<ModuleCourse?> = _selectedModule.asStateFlow()

  fun selectModule(module: ModuleCourse?) {
    _selectedModule.value = module
  }

  // Lectures
  val allLectures: StateFlow<List<Lecture>> = repository.allLectures
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val moduleLectures: StateFlow<List<Lecture>> = _selectedModule
    .flatMapLatest { module ->
      if (module != null) {
        repository.getLecturesForModule(module.id)
      } else {
        repository.allLectures
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val bookmarkedLectures: StateFlow<List<Lecture>> = repository.bookmarkedLectures
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Active PDF reading modal
  private val _activePdfLecture = MutableStateFlow<Lecture?>(null)
  val activePdfLecture: StateFlow<Lecture?> = _activePdfLecture.asStateFlow()

  fun openPdfViewer(lecture: Lecture) {
    _activePdfLecture.value = lecture
    recordLectureViewed(lecture)
  }

  fun closePdfViewer() {
    _activePdfLecture.value = null
  }

  // --- Offline Mode & Room Database Course Caching ---
  private val _isOfflineMode = MutableStateFlow(false)
  val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

  fun toggleOfflineMode() {
    _isOfflineMode.value = !_isOfflineMode.value
  }

  val previouslyViewedLectures: StateFlow<List<Lecture>> = repository.previouslyViewedLectures
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val previouslyViewedModules: StateFlow<List<ModuleCourse>> = repository.previouslyViewedModules
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val offlineAvailableLectures: StateFlow<List<Lecture>> = repository.offlineAvailableLectures
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allCachedMaterials: StateFlow<List<CachedCourseMaterial>> = repository.allCachedMaterials
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _selectedCachedMaterial = MutableStateFlow<CachedCourseMaterial?>(null)
  val selectedCachedMaterial: StateFlow<CachedCourseMaterial?> = _selectedCachedMaterial.asStateFlow()

  fun openCachedMaterial(material: CachedCourseMaterial) {
    _selectedCachedMaterial.value = material
    recordMaterialViewed(material)
  }

  fun closeCachedMaterial() {
    _selectedCachedMaterial.value = null
  }

  fun recordLectureViewed(lecture: Lecture) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.markLectureAsViewed(lecture.id, System.currentTimeMillis())
    }
  }

  fun recordModuleViewed(module: ModuleCourse) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.markModuleAsViewed(module.id, System.currentTimeMillis())
    }
  }

  fun recordMaterialViewed(material: CachedCourseMaterial) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.markCachedMaterialAsViewed(material.id, System.currentTimeMillis())
    }
  }

  fun cacheCourseContentForOffline(moduleId: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      setLoading(true, "جاري تحميل وتخزين المقرر في قاعدة الذاكرة المحلية (Room Database)...")
      repository.markModuleAsViewed(moduleId, System.currentTimeMillis())
      kotlinx.coroutines.delay(800)
      setLoading(false)
    }
  }

  fun deleteCachedMaterial(material: CachedCourseMaterial) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteCachedMaterial(material)
    }
  }

  // --- 24/7 Academic AI Chatbot (Firebase Genkit AI) ---
  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
    listOf(
      ChatMessage(
        text = "مرحباً بك في المساعد الأكاديمي الذكي 24/7 لمنصة طالب! 🎓\nأنا مدعوم بمكتبة Firebase Genkit AI لتقديم الدعم الأكاديمي الشامل: شرح المحاضرات، حل تمارين الأعمال الموجهة TD، مراجعة الامتحانات، وتلخيص المقررات على مدار الساعة.\nكيف يمكنني مساعدتك في دراستك اليوم؟",
        isFromUser = false,
        categoryBadge = "المساعد الأكاديمي 24/7",
        suggestedActions = listOf(
          "📚 اشرح لي درس النحو (المبتدأ والخبر)",
          "💻 كيف تعمل خوارزمية البحث الثنائي؟",
          "📊 كيف أحسب معدلي الفصلي في نظام LMD؟",
          "🎯 نصائح ذكية للتحضير للامتحانات"
        )
      )
    )
  )
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  private val _currentChatInput = MutableStateFlow("")
  val currentChatInput: StateFlow<String> = _currentChatInput.asStateFlow()

  fun updateChatInput(input: String) {
    _currentChatInput.value = input
  }

  fun sendAcademicChatMessage(promptText: String? = null) {
    val textToSend = promptText ?: _currentChatInput.value.trim()
    if (textToSend.isBlank() || _isAiThinking.value) return

    val userMessage = ChatMessage(
      text = textToSend,
      isFromUser = true
    )

    _chatMessages.value = _chatMessages.value + userMessage
    if (promptText == null) {
      _currentChatInput.value = ""
    }

    viewModelScope.launch {
      _isAiThinking.value = true
      try {
        val currentProf = studentProfile.value
        val modulesList = currentModules.value.map { it.name }
        val examsList = allExams.value.map { "${it.moduleName}: ${it.title}" }

        val studentContext = StudentAcademicContext(
          studentName = currentProf?.fullName ?: "طالب العلم",
          specialty = currentProf?.specialtyName ?: "الأدب العربي واللغات",
          academicYear = currentProf?.academicYearName ?: "السنة الثانية - ليسانس L2",
          currentModules = modulesList,
          upcomingExams = examsList
        )

        val (answer, followUps) = aiService.askAcademicAssistant(textToSend, studentContext)

        val aiMessage = ChatMessage(
          text = answer,
          isFromUser = false,
          categoryBadge = "المساعد الأكاديمي",
          suggestedActions = followUps,
          isOfflineHandled = _isOfflineMode.value
        )
        _chatMessages.value = _chatMessages.value + aiMessage
      } catch (e: Exception) {
        val errorMessage = ChatMessage(
          text = "عذراً، حدث خطأ أثناء معالجة الطلب الأكاديمي. يمكنك إعادة المحاولة أو مراجعة المحتوى المخزن في الذاكرة بدون إنترنت.",
          isFromUser = false,
          categoryBadge = "تنبيه"
        )
        _chatMessages.value = _chatMessages.value + errorMessage
      } finally {
        _isAiThinking.value = false
      }
    }
  }

  fun clearChatHistory() {
    _chatMessages.value = listOf(
      ChatMessage(
        text = "تم بدء محادثة دراسية جديدة. كيف أساعدك في مقرراتك وامتحاناتك؟",
        isFromUser = false,
        categoryBadge = "المساعد الأكاديمي 24/7",
        suggestedActions = listOf(
          "📚 ما هي أهم أسئلة امتحانات النحو؟",
          "⚡ خطة مراجعة سريعة لأسبوع الامتحانات",
          "🔍 شرح مبسط لنظرية العامل في النحو"
        )
      )
    )
  }

  fun askPredefinedAcademicQuestion(question: String) {
    sendAcademicChatMessage(question)
  }

  // Assignments
  val allAssignments: StateFlow<List<Assignment>> = repository.allAssignments
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Timetable Schedules
  val currentSchedule: StateFlow<List<ScheduleItem>> = combine(
    _selectedSpecialtyId,
    _selectedYearId
  ) { specId, yrId ->
    Pair(specId, yrId)
  }.flatMapLatest { (specId, yrId) ->
    repository.getScheduleForSpecialty(specId, yrId)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Selected Day of Week in Schedule (1 = Sunday ... 5 = Thursday)
  private val _selectedScheduleDay = MutableStateFlow(1)
  val selectedScheduleDay: StateFlow<Int> = _selectedScheduleDay.asStateFlow()

  fun selectScheduleDay(day: Int) {
    _selectedScheduleDay.value = day
  }

  // Exams
  val allExams: StateFlow<List<Exam>> = repository.allExams
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Grades & GPA
  val allGrades: StateFlow<List<StudentGrade>> = repository.allGrades
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val calculatedGPA: StateFlow<Double> = allGrades.map { gradeList ->
    if (gradeList.isEmpty()) 0.0
    else {
      var totalPoints = 0.0
      var totalCoeff = 0.0
      for (grade in gradeList) {
        val moduleAverage = (grade.continuousScore * 0.4) + (grade.examScore * 0.6)
        totalPoints += (moduleAverage * grade.coefficient)
        totalCoeff += grade.coefficient
      }
      if (totalCoeff > 0) totalPoints / totalCoeff else 0.0
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  // Announcements
  val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Search Filter
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  // User Actions
  fun toggleBookmark(lecture: Lecture) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateLectureBookmarkStatus(lecture.id, !lecture.isBookmarked)
    }
  }

  fun toggleLectureRead(lecture: Lecture) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateLectureReadStatus(lecture.id, !lecture.isRead)
    }
  }

  fun toggleDownloaded(lecture: Lecture) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateLecture(lecture.copy(isDownloaded = !lecture.isDownloaded))
    }
  }

  fun toggleAssignment(assignment: Assignment) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateAssignment(assignment.copy(isCompleted = !assignment.isCompleted))
    }
  }

  fun toggleAnnouncementRead(announcement: Announcement) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateAnnouncementReadStatus(announcement.id, !announcement.isRead)
    }
  }

  fun toggleGradeOfficial(grade: StudentGrade) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateGradeOfficialStatus(grade.id, !grade.isOfficial)
    }
  }

  fun updateGradeTarget(grade: StudentGrade, targetScore: Double) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateGradeTargetScore(grade.id, targetScore)
    }
  }

  fun updateGradeScore(grade: StudentGrade, continuousScore: Double, examScore: Double) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateGrade(grade.copy(continuousScore = continuousScore, examScore = examScore))
    }
  }

  fun updateProfile(profile: StudentProfile) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateStudentProfile(profile)
    }
  }

  fun updateAcademicPath(
    institution: String,
    specialtyName: String,
    profileTrack: String,
    yearName: String,
    semesterName: String,
    groupNumber: String
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val current = studentProfile.value
      if (current != null) {
        repository.updateStudentProfile(
          current.copy(
            institution = institution,
            university = institution,
            specialtyName = specialtyName,
            profileTrack = profileTrack,
            academicYearName = yearName,
            semesterName = semesterName,
            groupNumber = groupNumber
          )
        )
      }
    }
  }

  fun updateUserRole(role: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val current = studentProfile.value
      if (current != null) {
        repository.updateStudentProfile(
          current.copy(
            userRole = role,
            isAdminMode = role != "STUDENT"
          )
        )
      }
    }
  }


  // ADMIN / PROFESSOR ACTIONS
  fun addSpecialty(nameAr: String, code: String, desc: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val newId = repository.insertSpecialty(
        Specialty(nameAr = nameAr, code = code, description = desc)
      )
      // Add default years L1, L2, L3 for the new specialty
      repository.insertAcademicYear(AcademicYear(specialtyId = newId, yearName = "السنة الأولى (L1)", semester = 1))
      repository.insertAcademicYear(AcademicYear(specialtyId = newId, yearName = "السنة الثانية (L2)", semester = 1))
      repository.insertAcademicYear(AcademicYear(specialtyId = newId, yearName = "السنة الثالثة (L3)", semester = 1))
    }
  }

  fun addModule(
    specialtyId: Long,
    yearId: Long,
    name: String,
    code: String,
    coefficient: Double,
    credits: Int,
    professorName: String,
    professorEmail: String,
    category: String,
    description: String
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val modId = repository.insertModule(
        ModuleCourse(
          specialtyId = specialtyId,
          academicYearId = yearId,
          name = name,
          code = code,
          coefficient = coefficient,
          credits = credits,
          professorName = professorName,
          professorEmail = professorEmail,
          category = category,
          description = description
        )
      )
      // Also add empty grade tracker entry
      repository.insertGrade(
        StudentGrade(
          moduleId = modId,
          moduleName = name,
          continuousScore = 12.0,
          examScore = 12.0,
          coefficient = coefficient,
          credits = credits
        )
      )
    }
  }

  fun addLecture(
    moduleId: Long,
    weekNumber: Int,
    title: String,
    summary: String,
    pdfFileName: String,
    durationMinutes: Int
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertLecture(
        Lecture(
          moduleId = moduleId,
          weekNumber = weekNumber,
          title = title,
          summary = summary,
          pdfFileName = if (pdfFileName.endsWith(".pdf")) pdfFileName else "$pdfFileName.pdf",
          pdfUrl = "https://talib.edu/pdf/$pdfFileName",
          durationMinutes = durationMinutes,
          date = "اليوم"
        )
      )
    }
  }

  fun publishAnnouncement(
    title: String,
    content: String,
    author: String,
    urgency: String,
    visibilityScope: String = "تخصص كامل",
    targetGroups: String = "الكل"
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertAnnouncement(
        Announcement(
          title = title,
          content = content,
          author = author,
          date = "الآن",
          urgency = urgency,
          specialtyId = _selectedSpecialtyId.value,
          visibilityScope = visibilityScope,
          targetGroups = targetGroups
        )
      )
    }
  }

  fun addScheduleItem(
    dayOfWeek: Int,
    startTime: String,
    endTime: String,
    moduleName: String,
    type: String,
    room: String,
    professor: String
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertScheduleItem(
        ScheduleItem(
          specialtyId = _selectedSpecialtyId.value,
          academicYearId = _selectedYearId.value,
          dayOfWeek = dayOfWeek,
          startTime = startTime,
          endTime = endTime,
          moduleName = moduleName,
          type = type,
          room = room,
          professor = professor
        )
      )
    }
  }

  fun addExam(
    moduleId: Long,
    moduleName: String,
    title: String,
    date: String,
    time: String,
    room: String,
    coeff: Double
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.insertExam(
        Exam(
          moduleId = moduleId,
          moduleName = moduleName,
          title = title,
          examDate = date,
          time = time,
          room = room,
          coefficient = coeff
        )
      )
    }
  }

  fun deleteLecture(lecture: Lecture) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteLecture(lecture)
    }
  }

  fun deleteAnnouncement(announcement: Announcement) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteAnnouncement(announcement)
    }
  }
}

enum class ScreenRoute(val titleAr: String) {
  HOME("الرئيسية"),
  COURSES("المقررات"),
  LECTURES("المحاضرات والملفات"),
  AI_CHAT("المساعد الأكاديمي 24/7"),
  MY_FILES("ملفاتي"),
  OFFLINE_CACHE("المحتوى المحفوظ بدون إنترنت"),
  ASSIGNMENTS("الواجبات"),
  SCHEDULE("الجدول الدراسي"),
  EXAMS("الامتحانات"),
  GRADES("العلامات والمعدل"),
  GROUP("الفوج والزملاء"),
  ANNOUNCEMENTS("الإعلانات"),
  PROFILE("حسابي والملف"),
  ADMIN("لوحة إدارة المحتوى")
}
