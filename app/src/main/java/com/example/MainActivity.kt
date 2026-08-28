package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.talib.ui.components.GlobalLoadingIndicator
import com.example.talib.ui.components.HeaderSection
import com.example.talib.ui.components.TalibBottomNavBar
import com.example.talib.ui.screens.*
import com.example.talib.ui.viewmodel.ScreenRoute
import com.example.talib.ui.viewmodel.TalibViewModel
import com.example.ui.theme.TalibTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val talibViewModel: TalibViewModel = viewModel()

      val isDarkMode by talibViewModel.isDarkMode.collectAsStateWithLifecycle()
      val isAcademicTheme by talibViewModel.isAcademicTheme.collectAsStateWithLifecycle()
      val currentScreen by talibViewModel.currentScreen.collectAsStateWithLifecycle()
      val isLoading by talibViewModel.isLoading.collectAsStateWithLifecycle()
      val loadingMessage by talibViewModel.loadingMessage.collectAsStateWithLifecycle()

      // Handle system back button navigation gracefully
      BackHandler(enabled = currentScreen != ScreenRoute.HOME) {
        talibViewModel.navigateBack()
      }

      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        TalibTheme(darkTheme = isDarkMode, isAcademicTheme = isAcademicTheme) {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
          ) {
            Scaffold(
              modifier = Modifier.fillMaxSize(),
              contentWindowInsets = WindowInsets(0, 0, 0, 0),
              topBar = {
                if (currentScreen != ScreenRoute.AI_CHAT && currentScreen != ScreenRoute.OFFLINE_CACHE && currentScreen != ScreenRoute.MY_FILES) {
                  HeaderSection(
                    isDarkMode = isDarkMode,
                    onToggleTheme = { talibViewModel.toggleTheme() },
                    currentScreen = currentScreen,
                    isLoading = isLoading,
                    onRefresh = { talibViewModel.refreshCourseContent() },
                    onAdminClick = {
                      if (currentScreen == ScreenRoute.ADMIN) {
                        talibViewModel.navigateTo(ScreenRoute.HOME)
                      } else {
                        talibViewModel.navigateTo(ScreenRoute.ADMIN)
                      }
                    },
                    onBackClick = if (currentScreen != ScreenRoute.HOME) {
                      { talibViewModel.navigateBack() }
                    } else null
                  )
                }
              },
              bottomBar = {
                if (currentScreen != ScreenRoute.ADMIN && currentScreen != ScreenRoute.AI_CHAT && currentScreen != ScreenRoute.OFFLINE_CACHE) {
                  TalibBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { route -> talibViewModel.navigateTo(route) }
                  )
                }
              }
            ) { paddingValues ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(paddingValues)
              ) {
                // Global Loading Indicator placed at the top layer
                GlobalLoadingIndicator(
                  isLoading = isLoading,
                  message = loadingMessage,
                  isDarkMode = isDarkMode,
                  modifier = Modifier.align(Alignment.TopCenter)
                )

                AnimatedContent(
                  targetState = currentScreen,
                  transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                  },
                  label = "screen_navigation_anim"
                ) { screen ->
                  when (screen) {
                    ScreenRoute.HOME -> HomeScreen(
                      viewModel = talibViewModel,
                      onNavigate = { route -> talibViewModel.navigateTo(route) }
                    )
                    ScreenRoute.COURSES -> CoursesScreen(
                      viewModel = talibViewModel,
                      onNavigate = { route -> talibViewModel.navigateTo(route) }
                    )
                    ScreenRoute.LECTURES -> LecturesScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.AI_CHAT -> AcademicChatbotScreen(
                      viewModel = talibViewModel,
                      onNavigateBack = { talibViewModel.navigateBack() }
                    )
                    ScreenRoute.MY_FILES -> MyFilesScreen(
                      viewModel = talibViewModel,
                      onNavigate = { route -> talibViewModel.navigateTo(route) }
                    )
                    ScreenRoute.OFFLINE_CACHE -> OfflineVaultScreen(
                      viewModel = talibViewModel,
                      onNavigateBack = { talibViewModel.navigateBack() }
                    )
                    ScreenRoute.ASSIGNMENTS -> AssignmentsScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.SCHEDULE -> ScheduleScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.EXAMS -> ExamsScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.GRADES -> GradesScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.GROUP -> GroupScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.ANNOUNCEMENTS -> AnnouncementsScreen(
                      viewModel = talibViewModel
                    )
                    ScreenRoute.PROFILE -> ProfileScreen(
                      viewModel = talibViewModel,
                      onNavigate = { route -> talibViewModel.navigateTo(route) }
                    )
                    ScreenRoute.ADMIN -> AdminPanelScreen(
                      viewModel = talibViewModel,
                      onNavigate = { route -> talibViewModel.navigateTo(route) }
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
