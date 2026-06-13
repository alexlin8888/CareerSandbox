package com.careersandbox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareerSandboxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
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

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (shouldShowBottomNav(currentRoute)) {
                    BottomNav(navController)
                }
            },
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                CareerSandboxNavHost(navController = navController)
            }
        }
        FeatureTourOverlay(visible = tourVisible) {
            tourVisible = false
            TourState.markSeen(context)
        }
    }
}
