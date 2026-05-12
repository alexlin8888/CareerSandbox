package com.careersandbox.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careersandbox.app.ui.components.EmptyState
import com.careersandbox.app.ui.theme.InkBlack
import com.careersandbox.app.ui.theme.PaperOff

@Composable
fun ExplorePlaceholderScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(PaperOff),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "探索",
            style = MaterialTheme.typography.headlineLarge,
            color = InkBlack,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )
        Spacer(Modifier.height(48.dp))
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            EmptyState(
                icon = Icons.Outlined.Construction,
                title = "職場沙盒與競賽組隊",
                description = "MVP 階段先聚焦在履歷與面試,這個區塊會在下個版本上線",
            )
        }
    }
}
