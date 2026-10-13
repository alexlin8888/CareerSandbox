package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/** Entry of the new sandbox flow. */
@Composable
fun SandboxV2Entry(navController: NavHostController) {
    SandboxSelectScreen(onStart = { /* TODO: navigate to the background screen */ })
}