package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.theme.*

@Composable
fun WorkplaceSandboxScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperOff)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "職場沙盒",
            style = MaterialTheme.typography.headlineLarge,
            color = InkBlack,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )

        // Hero 卡 — 解釋這個功能在做什麼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(24.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Apartment,
                        contentDescription = null,
                        tint = BrandDeepOrange,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "提前打預防針",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "不是教你成功的職場 — 是讓你在踏進去之前,先感覺真實上班的樣子。主管的壓力、同事的氛圍、處理不完的 email、開不完的會。",
                    color = InkGray700,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp,
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // 未來會推出的場景列表
        Text(
            "即將推出的場景",
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(14.dp))

        SandboxPreview(
            icon = Icons.Outlined.SupervisorAccount,
            title = "和主管 1on1",
            description = "嚴厲、嘮叨、放手、微管理 — 不同個性主管的模擬對話",
        )
        SandboxPreview(
            icon = Icons.Outlined.Groups,
            title = "跨部門開會",
            description = "PM、工程、設計、行銷各自有立場,你怎麼推動進度",
        )
        SandboxPreview(
            icon = Icons.Outlined.Email,
            title = "Email 風暴日",
            description = "一天收 50 封信,有的緊急有的廢話,你決定先處理哪些",
        )
        SandboxPreview(
            icon = Icons.Outlined.Coffee,
            title = "同事午餐閒聊",
            description = "聽起來是閒聊,實際在探消息 — 練習職場社交分寸",
        )

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SandboxPreview(
    icon: ImageVector,
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandOrange.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = BrandDeepOrange,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
            )
        }
    }
}
