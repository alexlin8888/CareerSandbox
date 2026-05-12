package com.careersandbox.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun HomeHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperOff)) {
        // 底部粉紫黃光暈
        Box(
            Modifier.fillMaxSize().drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPink.copy(alpha = 0.32f),
                            GlowPink.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.95f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.1f, size.height * 0.95f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPurple.copy(alpha = 0.28f),
                            GlowPurple.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.7f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.9f, size.height * 0.7f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandYellow.copy(alpha = 0.35f),
                            BrandYellow.copy(alpha = 0f),
                        ),
                        center = Offset(size.width * 0.5f, size.height * 1.05f),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.5f, size.height * 1.05f),
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            TopGreeting()
            Spacer(Modifier.height(8.dp))
            HeroProgressCard()
            Spacer(Modifier.height(28.dp))
            QuickActions(navController)
            Spacer(Modifier.height(24.dp))
            ModuleCards(navController)
            Spacer(Modifier.height(24.dp))
            RecentNotifications()
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TopGreeting() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("下午好,", style = MaterialTheme.typography.titleSmall, color = InkGray500)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(MockData.currentUser.name,
                    fontWeight = FontWeight.Black,
                    color = InkBlack,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandYellow)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("大三", style = MaterialTheme.typography.labelSmall,
                        color = InkCharcoal, fontWeight = FontWeight.Bold)
                }
            }
        }
        Box(
            Modifier
                .size(46.dp)
                .shadow(8.dp, CircleShape, spotColor = InkBlack.copy(alpha = 0.15f))
                .clip(CircleShape)
                .background(PaperWhite)
                .pressScale { },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.NotificationsNone, contentDescription = "通知",
                tint = InkBlack, modifier = Modifier.size(22.dp))
            Box(
                Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(BrandOrange)
                    .align(Alignment.TopEnd)
                    .offset(x = (-11).dp, y = 11.dp)
            )
        }
    }
}

@Composable
private fun HeroProgressCard() {
    val stat = MockData.homeStat
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(32.dp),
                spotColor = InkBlack.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(32.dp))
            .background(InkCharcoal)
            .drawBehind {
                // 卡內光暈
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandOrange.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.2f),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPurple.copy(alpha = 0.4f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.9f),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.1f, size.height * 0.9f),
                )
            }
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("THIS WEEK", color = PaperWhite,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("你的職涯進度",
                        color = InkGray400,
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(2.dp))
                    Text("看起來不錯",
                        color = PaperWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp)
                }
                ProgressRingLarge(progress = stat.resumeCompletion / 100f,
                    valueText = "${stat.resumeCompletion}")
            }

            Spacer(Modifier.height(28.dp))

            // 玻璃感小卡分隔三組數據
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat(value = "${stat.resumeCompletion}%",
                    label = "履歷完成",
                    accent = BrandYellow,
                    modifier = Modifier.weight(1f))
                MiniStat(value = "${stat.weeklyInterviews}",
                    label = "本週面試",
                    accent = GlowPink,
                    modifier = Modifier.weight(1f))
                MiniStat(value = "${stat.recommendedJobs}",
                    label = "推薦職位",
                    accent = GlowPurple,
                    modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProgressRingLarge(progress: Float, valueText: String) {
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "ring",
    )
    Box(Modifier.size(84.dp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            drawArc(
                color = Color(0xFF2A2F3A),
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(size.width - stroke, size.height - stroke),
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(BrandOrange, BrandAmber, BrandYellow, BrandOrange)),
                startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(size.width - stroke, size.height - stroke),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valueText, color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp, letterSpacing = (-1).sp)
            Text("%", color = InkGray400, fontSize = 10.sp,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x14FFFFFF))
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(6.dp))
            Text(label, color = InkGray400,
                style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, color = PaperWhite,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp, letterSpacing = (-0.5).sp)
    }
}

@Composable
private fun QuickActions(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickAction("加經驗", Icons.Outlined.AddCircle, BrandPeach, BrandDeepOrange,
            Modifier.weight(1f)) {
            navController.navigate(Routes.EXPERIENCE_EDIT)
        }
        QuickAction("練面試", Icons.Outlined.Mic, GlowPink.copy(alpha = 0.4f), BrandDeepOrange,
            Modifier.weight(1f)) {
            navController.navigate(Routes.INTERVIEW_HUB)
        }
        QuickAction("寫履歷", Icons.Outlined.Description, GlowPurple.copy(alpha = 0.4f), InkBlack,
            Modifier.weight(1f)) {
            navController.navigate(Routes.RESUME_EDITOR)
        }
        QuickAction("找職缺", Icons.Outlined.Search, BrandYellow.copy(alpha = 0.7f), InkBlack,
            Modifier.weight(1f)) {
            navController.navigate(Routes.EXPLORE_HUB)
        }
    }
}

@Composable
private fun QuickAction(
    label: String, icon: ImageVector,
    iconBg: Color, iconFg: Color,
    modifier: Modifier, onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(20.dp),
                spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = iconFg,
                modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = InkBlack, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ModuleCards(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("你可以做的事",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = InkBlack)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(InkBlack)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("4", color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(16.dp))

        // 第一張:錯位大卡(橘色漸層)
        StaggeredAppear(delayMillis = 0) {
            BigModuleCard(
                title = "AI 面試模擬",
                subtitle = "個人 / 團體面試,真實情境演練",
                tag = "MVP",
                gradient = HeroGradient,
                textColor = PaperWhite,
                icon = Icons.Outlined.Mic,
                onClick = { navController.navigate(Routes.INTERVIEW_HUB) }
            )
        }

        Spacer(Modifier.height(12.dp))

        // 第二排:雙卡並排,左寬右窄
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallModuleCard(
                title = "經驗轉譯",
                subtitle = "整理你做過的事",
                icon = Icons.Outlined.Description,
                iconBg = BrandPeach,
                iconFg = BrandDeepOrange,
                modifier = Modifier.weight(1.4f),
                onClick = { navController.navigate(Routes.RESUME_HUB) },
            )
            SmallModuleCard(
                title = "沙盒",
                subtitle = "預習職場",
                icon = Icons.Outlined.Work,
                iconBg = GlowPurple.copy(alpha = 0.4f),
                iconFg = InkBlack,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Routes.EXPLORE_HUB) },
            )
        }

        Spacer(Modifier.height(12.dp))

        StaggeredAppear(delayMillis = 240) {
            CompactModuleCard(
                title = "競賽組隊媒合",
                subtitle = "找一起參賽的夥伴",
                icon = Icons.Outlined.Groups,
                onClick = { navController.navigate(Routes.EXPLORE_HUB) },
            )
        }
    }
}

@Composable
private fun BigModuleCard(
    title: String, subtitle: String,
    tag: String?, gradient: Brush, textColor: Color,
    icon: ImageVector, onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(28.dp),
                spotColor = BrandOrange.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .pressScale(onClick = onClick)
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (tag != null) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(InkBlack)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(tag, color = BrandYellow,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = textColor,
                        modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(title, color = textColor,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp, letterSpacing = (-0.5).sp)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = textColor.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(textColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("立刻開始", color = BrandDeepOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = textColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun SmallModuleCard(
    title: String, subtitle: String,
    icon: ImageVector, iconBg: Color, iconFg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = 140.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp),
                spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(22.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconFg,
                    modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, letterSpacing = (-0.3).sp)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = InkGray500,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CompactModuleCard(
    title: String, subtitle: String,
    icon: ImageVector, onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp),
                spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(22.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(GlowAmber.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = BrandDeepOrange,
                modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Text(subtitle, color = InkGray500,
                style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null,
            tint = InkGray400)
    }
}

@Composable
private fun RecentNotifications() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("最近的提醒",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = InkBlack,
                modifier = Modifier.weight(1f))
            Text("全部",
                style = MaterialTheme.typography.labelLarge,
                color = BrandDeepOrange,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.pressScale { })
        }
        Spacer(Modifier.height(12.dp))
        MockData.notifications.take(2).forEachIndexed { idx, n ->
            StaggeredAppear(delayMillis = idx * 80) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PaperWhite)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape)
                            .background(BrandPeach),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = BrandDeepOrange,
                            modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(n.title, style = MaterialTheme.typography.titleSmall,
                            color = InkBlack, fontWeight = FontWeight.SemiBold)
                        Text(n.body, style = MaterialTheme.typography.bodySmall,
                            color = InkGray500, maxLines = 1)
                    }
                    Text(n.time, style = MaterialTheme.typography.labelSmall,
                        color = InkGray400)
                }
            }
            if (idx < 1) Spacer(Modifier.height(10.dp))
        }
    }
}
