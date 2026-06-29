package com.careersandbox.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.careersandbox.app.ui.screens.competition.CompetitionDetailScreen
import com.careersandbox.app.ui.screens.competition.CompetitionListScreen
import com.careersandbox.app.ui.screens.competition.TeamChatScreen
import com.careersandbox.app.ui.screens.competition.TeamMatchScreen
import com.careersandbox.app.ui.screens.home.ArticleDetailScreen
import com.careersandbox.app.ui.screens.home.HomeHubScreen
import com.careersandbox.app.ui.screens.home.NotificationsAllScreen
import com.careersandbox.app.ui.screens.interview.*
import com.careersandbox.app.ui.screens.onboarding.LoginScreen
import com.careersandbox.app.ui.screens.onboarding.OnboardingScreen
import com.careersandbox.app.ui.screens.onboarding.SplashScreen
import com.careersandbox.app.ui.screens.profile.ProfileScreen
import com.careersandbox.app.ui.screens.profile.SettingsHelpScreen
import com.careersandbox.app.ui.screens.profile.SettingsLogoutScreen
import com.careersandbox.app.ui.screens.profile.SettingsNotificationsScreen
import com.careersandbox.app.ui.screens.profile.SettingsPrivacyScreen
import com.careersandbox.app.ui.screens.profile.SettingsProfileScreen
import com.careersandbox.app.ui.screens.resume.*
import com.careersandbox.app.ui.screens.workplace.NovaBackFrame
import com.careersandbox.app.ui.screens.workplace.Day1OneOnOneScreen
import com.careersandbox.app.ui.screens.workplace.SandboxChatScreen
import com.careersandbox.app.ui.screens.workplace.Day2EmailScreen
import com.careersandbox.app.ui.screens.workplace.Day3MeetingScreen
import com.careersandbox.app.ui.screens.workplace.Day4LunchScreen
import com.careersandbox.app.ui.screens.workplace.Day5ReviewScreen
import com.careersandbox.app.ui.screens.workplace.NovaChatScreen
import com.careersandbox.app.ui.screens.workplace.NovaChatListScreen
import com.careersandbox.app.ui.screens.workplace.NovaMailInboxScreen
import com.careersandbox.app.ui.screens.workplace.NovaMailOpenScreen
import com.careersandbox.app.ui.screens.workplace.NovaCalendarScreen
import com.careersandbox.app.ui.screens.workplace.NovaTeamScreen
import com.careersandbox.app.ui.screens.workplace.NovaMeetScreen
import com.careersandbox.app.ui.screens.workplace.NovaDocScreen
import com.careersandbox.app.ui.screens.workplace.NovaGramScreen
import com.careersandbox.app.ui.screens.workplace.NovaDashboardScreen
import com.careersandbox.app.ui.screens.workplace.WorkplaceSandboxScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CareerSandboxNavHost(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(340, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                    initialOffset = { it / 8 },
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(220)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(340),
                    targetOffset = { -it / 12 },
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(280)) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(340),
                    initialOffset = { -it / 12 },
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(220)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(340),
                    targetOffset = { it / 8 },
                )
        },
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onDone = {
                navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } }
            })
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onSignup = { navController.navigate(Routes.ONBOARDING) },
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onDone = {
                navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
            })
        }
        composable(Routes.HOME) { HomeHubScreen(navController) }
        composable(Routes.RESUME_HUB) { ResumeHubScreen(navController) }
        composable(Routes.RESUME_HIERARCHY) { ResumeHierarchyScreen(navController) }
        composable(Routes.RESUME_ARCH_INTRO) { ResumeArchIntroScreen(navController) }
        composable(Routes.INTERVIEW_HUB) { InterviewHubScreen(navController) }
        composable(Routes.WORKPLACE_SANDBOX) { WorkplaceSandboxScreen(navController) }
        composable(Routes.SANDBOX_CHAT) { SandboxChatScreen(navController) }
        composable(Routes.WORKPLACE_CHAT) { Day1OneOnOneScreen(navController) }
        composable(Routes.WORKPLACE_MEETING) { Day3MeetingScreen(navController) }
        composable(Routes.WORKPLACE_LUNCH) { Day4LunchScreen(navController) }
        composable(Routes.WORKPLACE_EMAIL) { Day2EmailScreen(navController) }
        composable(Routes.NOVA_CHAT) { NovaChatScreen(navController) }
        composable(Routes.NOVA_CHAT_LIST) { NovaBackFrame(navController) { NovaChatListScreen(navController) } }
        composable(Routes.NOVA_MAIL_INBOX) { NovaBackFrame(navController) { NovaMailInboxScreen(navController) } }
        composable(Routes.NOVA_MAIL_OPEN) { NovaMailOpenScreen(navController) }
        composable(Routes.NOVA_CALENDAR) { NovaBackFrame(navController) { NovaCalendarScreen(navController) } }
        composable(Routes.NOVA_TEAM) { NovaTeamScreen(navController) }
        composable(Routes.NOVA_MEET) { NovaMeetScreen(navController) }
        composable(Routes.NOVA_DOC) { NovaDocScreen(navController) }
        composable(Routes.NOVA_GRAM) { NovaBackFrame(navController) { NovaGramScreen(navController) } }
        composable(Routes.NOVA_DASHBOARD) { NovaBackFrame(navController) { NovaDashboardScreen(navController) } }
        composable(Routes.WORKPLACE_REVIEW) { Day5ReviewScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }

        composable(Routes.EXPERIENCE_LIST) { ExperienceListScreen(navController) }
        composable(Routes.EXPERIENCE_EDIT) { ExperienceEditScreen(navController) }
        composable(Routes.JD_CUSTOMIZE) { JdCustomizeScreen(navController) }
        composable(Routes.RESUME_PROFILE) { ResumeProfileScreen(navController) }
        composable(Routes.RESUME_UPLOAD_PROCESSING) { ResumeUploadProcessingScreen(navController) }
        composable(Routes.CAREER_EXPLORATION) { CareerExplorationScreen(navController) }
        composable(Routes.LEARNING_PATH) { LearningPathScreen(navController) }
        composable(Routes.FIT_ANALYSIS) { FitAnalysisScreen(navController) }

        composable(Routes.COMPETITION_LIST) { CompetitionListScreen(navController) }
        composable(Routes.TEAM_CHAT) { TeamChatScreen(navController) }
        composable(Routes.TEAM_MATCH) { TeamMatchScreen(navController) }
        composable(
            route = Routes.COMPETITION_DETAIL,
            arguments = listOf(navArgument("compId") { type = NavType.StringType }),
        ) { backStackEntry ->
            CompetitionDetailScreen(
                navController = navController,
                compId = backStackEntry.arguments?.getString("compId") ?: "",
            )
        }
        composable(Routes.NEW_JOB_APPLICATION) { NewJobApplicationScreen(navController) }
        composable(
            route = Routes.JOB_APPLICATION_DETAIL,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { entry ->
            val jobId = entry.arguments?.getString("jobId") ?: ""
            JobApplicationDetailScreen(navController, jobId)
        }
        composable(
            route = Routes.PDF_EXPORT_DIALOG,
            arguments = listOf(navArgument("versionId") { type = NavType.StringType })
        ) { entry ->
            val versionId = entry.arguments?.getString("versionId") ?: ""
            PdfExportDialogScreen(navController, versionId)
        }

        composable(Routes.INTERVIEW_SETUP_INDIVIDUAL) { InterviewSetupScreen(navController) }
        composable(Routes.INTERVIEW_SETUP_GROUP) { InterviewSetupGroupScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_INDIVIDUAL) { InterviewLiveIndividualScreen(navController) }
        composable(Routes.INTERVIEW_QUICK) { InterviewQuickScreen(navController) }
        composable(Routes.INTERVIEW_VIDEO) { VideoInterviewScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_GROUP) { InterviewLiveGroupScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_PANEL) { InterviewLivePanelScreen(navController) }
        composable(Routes.INTERVIEW_REPORT) { InterviewReportScreen(navController) }
        composable(Routes.INTERVIEW_HISTORY) { InterviewHistoryScreen(navController) }

        composable(Routes.NOTIFICATIONS_ALL) { NotificationsAllScreen(navController) }
        composable(
            route = Routes.ARTICLE_DETAIL,
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(navController, articleId)
        }
        composable(Routes.SETTINGS_PROFILE) { SettingsProfileScreen(navController) }
        composable(Routes.SETTINGS_NOTIFICATIONS) { SettingsNotificationsScreen(navController) }
        composable(Routes.SETTINGS_PRIVACY) { SettingsPrivacyScreen(navController) }
        composable(Routes.SETTINGS_HELP) { SettingsHelpScreen(navController) }
        composable(Routes.SETTINGS_LOGOUT) { SettingsLogoutScreen(navController) }
    }
}
