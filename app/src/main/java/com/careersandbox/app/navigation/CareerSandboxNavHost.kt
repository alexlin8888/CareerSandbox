package com.careersandbox.app.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.careersandbox.app.ui.screens.home.ArticleDetailScreen
import com.careersandbox.app.ui.screens.home.ExplorePlaceholderScreen
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
            fadeIn(animationSpec = tween(250)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(280))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(250)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(280))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(280))
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
        composable(Routes.EXPLORE_HUB) { ExplorePlaceholderScreen() }
        composable(Routes.PROFILE) { ProfileScreen(navController) }

        composable(Routes.EXPERIENCE_LIST) { ExperienceListScreen(navController) }
        composable(Routes.EXPERIENCE_EDIT) { ExperienceEditScreen(navController) }
        composable(Routes.RESUME_EDITOR) { ResumeEditorScreen(navController) }
        composable(Routes.JD_CUSTOMIZE) { JdCustomizeScreen(navController) }
        composable(Routes.RESUME_PROFILE) { ResumeProfileScreen(navController) }
        composable(Routes.RESUME_UPLOAD_PROCESSING) { ResumeUploadProcessingScreen(navController) }

        composable(Routes.INTERVIEW_SETUP_INDIVIDUAL) { InterviewSetupScreen(navController) }
        composable(Routes.INTERVIEW_SETUP_GROUP) { InterviewSetupGroupScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_INDIVIDUAL) { InterviewLiveIndividualScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_GROUP) { InterviewLiveGroupScreen(navController) }
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
