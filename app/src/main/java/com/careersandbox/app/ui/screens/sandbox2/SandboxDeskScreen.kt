package com.careersandbox.app.ui.screens.sandbox2

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandDeepOrange

private data class DockItem(val id: Int, val label: String, val key: String)

private val dock = listOf(
    DockItem(R.drawable.ic_sb_msg, "訊息", "msg"),
    DockItem(R.drawable.ic_sb_data, "資料", "data"),
    DockItem(R.drawable.ic_sb_ana, "分析", "ana"),
    DockItem(R.drawable.ic_sb_out, "產出", "out"),
)

/** Workstation main screen: pixel desk background with overlaid UI. */
@Composable
fun SandboxDeskScreen(navController: NavHostController) {
    var showExitDialog by remember { mutableStateOf(false) }

    // Sends the opening messages once per day
    LaunchedEffect(Unit) { SandboxV2State.beginDay() }

    // The system back gesture asks for confirmation instead of leaving at once
    BackHandler { showExitDialog = true }

    Box(Modifier.fillMaxSize().background(Color(0xFFB8B8BE))) {
        Image(
            painter = painterResource(R.drawable.sandbox_desk_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Top-left: exit button above the sticky note
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xE61F2937))
                    .pressScale { showExitDialog = true }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text("離開", color = Color.White, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .rotate(-2f)
                    .background(Color(0xFFFFE98A))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Text(
                    "收藏清單分析\n18:00 前交給小昀",
                    color = Color(0xFF3A2E05), fontSize = 12.sp, lineHeight = 18.sp,
                )
            }
        }

        // Clock (top-right); follows the current station
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xE61F2937))
                .pressScale { navController.navigate(Routes.SANDBOX_V2_RESULT) } // TODO: dev shortcut, remove later
                .border(2.dp, Color(0xFF0D1320), RoundedCornerShape(6.dp))
                .padding(horizontal = 9.dp, vertical = 4.dp),
        ) {
            Text(
                SandboxV2State.clockText, color = Color.White, fontSize = 13.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
            )
        }

        // Left dock with four round icons
        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            dock.forEach { item ->
                DockIcon(
                    item = item,
                    hasDot = item.key == "msg" && SandboxV2State.hasUnread,
                    onClick = {
                        when (item.key) {
                            "msg" -> navController.navigate(Routes.SANDBOX_V2_MSG)
                            else -> { /* TODO: data / analysis / output windows */ }
                        }
                    },
                )
            }
        }

        // Bottom hint, only while there are unread messages
        if (SandboxV2State.hasUnread) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE61F2937))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text("小昀傳了訊息給你", color = Color.White, fontSize = 12.sp)
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("離開這一天") },
            text = { Text("離開後，這次的紀錄不會保存，下次要從頭開始。") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    // Back to the occupation select screen (the sandbox tab route)
                    navController.popBackStack(Routes.WORKPLACE_SANDBOX, false)
                }) { Text("離開", color = BrandDeepOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("繼續") }
            },
        )
    }
}

@Composable
private fun DockIcon(item: DockItem, hasDot: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.pressScale { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(
                painter = painterResource(item.id),
                contentDescription = item.label,
                modifier = Modifier.size(56.dp),
            )
            if (hasDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE02424))
                        .border(2.dp, Color.White, CircleShape),
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xD11F2937))
                .padding(horizontal = 6.dp, vertical = 1.dp),
        ) {
            Text(item.label, color = Color.White, fontSize = 11.sp)
        }
    }
}