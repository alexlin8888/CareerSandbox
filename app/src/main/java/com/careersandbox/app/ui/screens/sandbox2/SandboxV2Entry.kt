package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm

/** Entry of the new sandbox flow (placeholder until screens are built). */
@Composable
fun SandboxV2Entry(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize().background(PaperWarm),
        contentAlignment = Alignment.Center,
    ) {
        Text("Sandbox V2", color = InkSlate)
    }
}