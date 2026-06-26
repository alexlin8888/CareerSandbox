package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/* =====================================================================
   NovaBackFrame —— 給 Nova 畫面疊一個浮動返回鍵
   不動各畫面內部結構,只在外層加左上角浮動返回(半透明深圓,淺底深底都看得到)。
   在 NavHost 包住「缺返回鍵」的 Nova 路由即可。
   ===================================================================== */
@Composable
fun NovaBackFrame(navController: NavHostController, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        content()
        Box(
            Modifier.align(Alignment.TopStart)
                .padding(top = 10.dp, start = 10.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x66000000))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
