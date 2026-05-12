package com.careersandbox.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.careersandbox.app.ui.screens.home.ExplorePlaceholderScreen
import com.careersandbox.app.ui.screens.home.HomeHubScreen
import com.careersandbox.app.ui.screens.interview.*
import com.careersandbox.app.ui.screens.onboarding.LoginScreen
import com.careersandbox.app.ui.screens.onboarding.OnboardingScreen
import com.careersandbox.app.ui.screens.onboarding.SplashScreen
import com.careersandbox.app.ui.screens.profile.ProfileScreen
import com.careersandbox.app.ui.screens.resume.*

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
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
        composable(Routes.PROFILE) { ProfileScreen() }

        composable(Routes.EXPERIENCE_LIST) { ExperienceListScreen(navController) }
        composable(Routes.EXPERIENCE_EDIT) { ExperienceEditScreen(navController) }
        composable(Routes.RESUME_EDITOR) { ResumeEditorScreen(navController) }
        composable(Routes.JD_CUSTOMIZE) { JdCustomizeScreen(navController) }

        composable(Routes.INTERVIEW_SETUP_INDIVIDUAL) { InterviewSetupScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_INDIVIDUAL) { InterviewLiveIndividualScreen(navController) }
        composable(Routes.INTERVIEW_LIVE_GROUP) { InterviewLiveGroupScreen(navController) }
        composable(Routes.INTERVIEW_REPORT) { InterviewReportScreen(navController) }
    }
}
