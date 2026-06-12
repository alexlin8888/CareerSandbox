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
import com.careersandbox.app.ui.screens.workplace.WorkplaceChatScreen
import com.careersandbox.app.ui.screens.workplace.WorkplaceMeetingScreen
import com.careersandbox.app.ui.screens.workplace.WorkplaceLunchScreen
import com.careersandbox.app.ui.screens.workplace.WorkplaceEmailScreen
import com.careersandbox.app.ui.screens.workplace.WorkplaceReviewScreen
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
        composable(Routes.INTERVIEW_HUB) { InterviewHubScreen(navController) }
        composable(Routes.WORKPLACE_SANDBOX) { WorkplaceSandboxScreen(navController) }
        composable(Routes.WORKPLACE_CHAT) { WorkplaceChatScreen(navController) }
        composable(Routes.WORKPLACE_MEETING) { WorkplaceMeetingScreen(navController) }
        composable(Routes.WORKPLACE_LUNCH) { WorkplaceLunchScreen(navController) }
        composable(Routes.WORKPLACE_EMAIL) { WorkplaceEmailScreen(navController) }
        composable(Routes.WORKPLACE_REVIEW) { WorkplaceReviewScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }

        composable(Routes.EXPERIENCE_LIST) { ExperienceListScreen(navController) }
        composable(Routes.EXPERIENCE_EDIT) { ExperienceEditScreen(navController) }
        composable(Routes.RESUME_EDITOR) { ResumeEditorScreen(navController) }
        composable(Routes.JD_CUSTOMIZE) { JdCustomizeScreen(navController) }
        composable(Routes.RESUME_PROFILE) { ResumeProfileScreen(navController) }
        composable(Routes.RESUME_UPLOAD_PROCESSING) { ResumeUploadProcessingScreen(navController) }
        composable(Routes.CAREER_EXPLORATION) { CareerExplorationScreen(navController) }
        composable(Routes.FIT_ANALYSIS) { FitAnalysisScreen(navController) }

        composable(Routes.COMPETITION_LIST) { CompetitionListScreen(navController) }
        composable(Routes.TEAM_CHAT) { TeamChatScreen(navController) }
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
