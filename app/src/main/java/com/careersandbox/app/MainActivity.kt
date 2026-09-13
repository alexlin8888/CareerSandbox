package com.careersandbox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import com.careersandbox.app.ui.components.BottomNavSpace
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.careersandbox.app.navigation.CareerSandboxNavHost
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.BottomNav
import com.careersandbox.app.ui.components.TourState
import com.careersandbox.app.ui.components.FeatureTourOverlay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.careersandbox.app.ui.components.shouldShowBottomNav
import com.careersandbox.app.ui.theme.CareerSandboxTheme
import androidx.lifecycle.lifecycleScope
import com.careersandbox.app.data.local.SessionManager
import com.careersandbox.app.data.local.SettingsStore
import kotlinx.coroutines.launch
import com.careersandbox.app.ui.theme.PaperWhite


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        SessionManager.init(applicationContext)
        SettingsStore.init(applicationContext)
        lifecycleScope.launch { SessionManager.load() }
        setContent {
            CareerSandboxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PaperWhite
                ) {
                    CareerSandboxApp()
                }
            }
        }
    }
}

@Composable
fun CareerSandboxApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 處理系統返回鍵:在非首頁的 hub 頁,返回鍵跳回 HOME(不退 App)
    val isHubButNotHome = currentRoute in listOf(
        Routes.RESUME_HUB, Routes.INTERVIEW_HUB, Routes.WORKPLACE_SANDBOX, Routes.PROFILE,
    )
    BackHandler(enabled = isHubButNotHome) {
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val context = LocalContext.current
    val onHome = currentRoute == Routes.HOME
    var tourVisible by remember { mutableStateOf(false) }
    // 首次啟動、且回到首頁時自動播一次
    LaunchedEffect(onHome) {
        if (onHome && TourState.shouldShowOnLaunch(context)) tourVisible = true
    }
    // 設定頁手動重看
    LaunchedEffect(TourState.forceShow) {
        if (TourState.forceShow) { tourVisible = true; TourState.forceShow = false }
    }

    val showNav = shouldShowBottomNav(currentRoute)
    val isHome = currentRoute == Routes.HOME
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .then(if (isHome) Modifier else Modifier.statusBarsPadding()),
        ) {
            CareerSandboxNavHost(navController = navController)
        }

        if (showNav) {
            BottomNav(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        FeatureTourOverlay(visible = tourVisible) {
            tourVisible = false
            TourState.markSeen(context)
        }
    }
}
