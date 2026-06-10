package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class WeekDay(
    val dayNo: Int,
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val route: String?,   // null = 規劃中
)

@Composable
fun WorkplaceSandboxScreen(navController: NavHostController) {
    var industry by remember { mutableStateOf("科技 / 網路") }

    val week = listOf(
        WeekDay(1, "和主管 1on1", "功能延期了,門關上了 — 你怎麼接", Icons.Outlined.SupervisorAccount, Routes.WORKPLACE_CHAT),
        WeekDay(2, "Email 風暴日", "90 秒,12 封未讀 — 拆不完,只能選", Icons.Outlined.Email, Routes.WORKPLACE_EMAIL),
        WeekDay(3, "跨部門會議", "各部門各有立場,你要推的是進度", Icons.Outlined.Groups, null),
        WeekDay(4, "同事午餐", "聽起來是閒聊,其實在探消息", Icons.Outlined.Coffee, null),
        WeekDay(5, "週五回顧", "這一週的隱形分數,週五才揭曉", Icons.Outlined.Insights, null),
    )

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

        // Hero — 一句講完這裡在幹嘛
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BrandPeach.copy(alpha = 0.5f))
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(R.drawable.beaver_point),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "提前打預防針",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "不是教你成功的職場 — 是讓你在踏進去之前,先感覺真實上班的樣子。",
                        color = InkGray700,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandDeepOrange.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text(
                            "在這裡踩雷,總比上班才踩好。",
                            color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        IndustrySelector(selected = industry, onSelect = { industry = it })

        Spacer(Modifier.height(28.dp))

        // === 入職第一週(Day 路徑)===
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("入職第一週",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(InkBlack)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text("第 1 週 ・ 試用期",
                    color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            if (industry == "科技 / 網路") "五天,五個場景。從 Day 1 開始。"
            else "「$industry」的一週規劃中 — 先走通用版。",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(18.dp))

        week.forEachIndexed { idx, day ->
            StaggeredAppear(delayMillis = idx * 70) {
                DayPathNode(
                    day = day,
                    isLast = idx == week.lastIndex,
                    isCurrent = idx == 0,
                    onClick = day.route?.let { r -> { navController.navigate(r) } },
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

/* ===================== Day 路徑節點(左側時間軸)===================== */

@Composable
private fun DayPathNode(
    day: WeekDay,
    isLast: Boolean,
    isCurrent: Boolean,
    onClick: (() -> Unit)?,
) {
    val ready = onClick != null
    val pulse = rememberInfiniteTransition(label = "day-${day.dayNo}")
    val nodeScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent && ready) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // 左:節點 + 連接線
        Column(
            modifier = Modifier.width(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .scale(nodeScale)
                    .clip(CircleShape)
                    .background(if (ready) BrandDeepOrange else InkGray200)
                    .then(
                        if (onClick != null) Modifier.pressScale(onClick = onClick)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(day.icon, contentDescription = null,
                    tint = if (ready) PaperWhite else InkGray400,
                    modifier = Modifier.size(24.dp))
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(InkGray200),
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        // 右:文字
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp, bottom = if (isLast) 0.dp else 14.dp)
                .then(
                    if (onClick != null) Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .pressScale(onClick = onClick)
                    else Modifier
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Day ${day.dayNo}",
                    color = if (ready) BrandDeepOrange else InkGray400,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (ready) AccentGreen else InkGray200)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        if (ready) "可以玩了" else "規劃中",
                        color = if (ready) PaperWhite else InkGray500,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(day.title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp)
            Spacer(Modifier.height(2.dp))
            Text(day.desc,
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp)
        }
    }
}

/* ===================== 產業選擇(可擴充)===================== */

@Composable
private fun IndustrySelector(selected: String, onSelect: (String) -> Unit) {
    val scroll = rememberScrollState()
    val industries = listOf(
        "科技 / 網路" to true,
        "金融" to false,
        "行銷 / 廣告" to false,
        "製造 / 工程" to false,
        "醫療" to false,
        "公部門" to false,
    )
    Column {
        Text(
            "選一個產業",
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            industries.forEach { (name, ready) ->
                val isSel = name == selected
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSel) BrandDeepOrange else MaterialTheme.colorScheme.surface)
                        .pressScale { onSelect(name) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        name,
                        color = if (isSel) PaperWhite else InkGray700,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!ready) {
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "規劃中",
                            color = if (isSel) PaperWhite.copy(alpha = 0.8f) else InkGray400,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
