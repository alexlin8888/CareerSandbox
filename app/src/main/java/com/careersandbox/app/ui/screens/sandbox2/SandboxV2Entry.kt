package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.screens.workplace.SoundManager

object SandboxAudio {
    // Swap to R.raw.bgm_neutral if bgm_warm does not fit
    val BGM = R.raw.bgm_warm
}

/** Entry of the new sandbox flow. */
@Composable
fun SandboxV2Entry(navController: NavHostController) {
    val context = LocalContext.current
    SandboxSelectScreen(onStart = {
        SoundManager.playBgm(context, SandboxAudio.BGM)
        navController.navigate(Routes.SANDBOX_V2_BRIEF)
    })
}