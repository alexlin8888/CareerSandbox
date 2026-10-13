package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.careersandbox.app.ui.components.pressScale
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding

private data class DockItem(val id: Int, val label: String, val hasDot: Boolean = false)

private val dock = listOf(
    DockItem(R.drawable.ic_sb_msg, "訊息", hasDot = true),
    DockItem(R.drawable.ic_sb_data, "資料"),
    DockItem(R.drawable.ic_sb_ana, "分析"),
    DockItem(R.drawable.ic_sb_out, "產出"),
)

/** Workstation main screen: pixel desk background with overlaid UI. */
@Composable
fun SandboxDeskScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize().background(Color(0xFFB8B8BE))) {
        Image(
            painter = painterResource(R.drawable.sandbox_desk_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Sticky note (top-left)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .rotate(-2f)
                .background(Color(0xFFFFE98A))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                "收藏清單分析\n18:00 前交給小昀",
                color = Color(0xFF3A2E05), fontSize = 12.sp, lineHeight = 18.sp,
            )
        }

        // Clock (top-right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xE61F2937))
                .border(2.dp, Color(0xFF0D1320), RoundedCornerShape(6.dp))
                .padding(horizontal = 9.dp, vertical = 4.dp),
        ) {
            Text(
                "09:12", color = Color.White, fontSize = 13.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
            )
        }

        // Left dock with four round icons
        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            dock.forEach { item ->
                DockIcon(item = item, onClick = { /* TODO: open the window for this icon */ })
            }
        }

        // Bottom hint
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

@Composable
private fun DockIcon(item: DockItem, onClick: () -> Unit) {
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
            if (item.hasDot) {
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