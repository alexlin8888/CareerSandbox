package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.InterviewRecord
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun InterviewHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(InkCharcoal)) {
        // 光暈背景
        Box(
            Modifier.fillMaxSize().drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandOrange.copy(alpha = 0.5f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.15f),
                        radius = size.width * 0.9f,
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(size.width * 0.2f, size.height * 0.15f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPurple.copy(alpha = 0.35f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.95f, size.height * 0.5f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.95f, size.height * 0.5f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandYellow.copy(alpha = 0.3f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.95f),
                        radius = size.width * 0.6f,
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.1f, size.height * 0.95f),
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            // 頂部
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("面試模擬",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Interview Practice",
                        color = InkGray400,
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp)
                }
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                        .pressScale {},
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null,
                        tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
            }

            // 統計區:三個玻璃感小卡
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GlassStatChip("4", "已完成", BrandYellow, Modifier.weight(1f))
                GlassStatChip("71", "平均分", GlowPink, Modifier.weight(1f))
                GlassStatChip("2", "本週", GlowPurple, Modifier.weight(1f))
            }

            Spacer(Modifier.height(28.dp))

            // 選一種開始
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("選一種開始",
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(6.dp).clip(CircleShape).background(BrandYellow)
                )
            }
            Spacer(Modifier.height(16.dp))

            // 兩張大型面試類型卡
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                BigInterviewCard(
                    title = "個人面試",
                    eyebrow = "1 對 1 ・ 入門推薦",
                    description = "AI 扮演面試官,從履歷出題,從你的回答追問",
                    bgGradient = Brush.linearGradient(
                        listOf(BrandDeepOrange, BrandOrange)
                    ),
                    icon = Icons.Outlined.Person,
                    tag = null,
                    onClick = { navController.navigate(Routes.INTERVIEW_SETUP_INDIVIDUAL) },
                )
                BigInterviewCard(
                    title = "團體面試",
                    eyebrow = "3-5 人小組 ・ MVP",
                    description = "AI 扮演其他應徵者,真實討論場景演練",
                    bgGradient = Brush.linearGradient(
                        listOf(Color(0xFF1F2937), Color(0xFF374151))
                    ),
                    icon = Icons.Outlined.Groups,
                    tag = "市面少見",
                    onClick = { navController.navigate(Routes.INTERVIEW_LIVE_GROUP) },
                )
            }

            Spacer(Modifier.height(28.dp))

            // 歷史紀錄
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("歷史紀錄",
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f))
                Text("全部",
                    color = BrandYellow,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.pressScale {})
            }
            Spacer(Modifier.height(14.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MockData.interviewHistory.forEach { r ->
                    HistoryRow(r) { navController.navigate(Routes.INTERVIEW_REPORT) }
                }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun GlassStatChip(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x14FFFFFF))
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(6.dp))
            Text(label, color = InkGray400,
                style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, color = PaperWhite,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp, letterSpacing = (-1).sp)
    }
}

@Composable
private fun BigInterviewCard(
    title: String, eyebrow: String, description: String,
    bgGradient: Brush, icon: ImageVector, tag: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, RoundedCornerShape(28.dp),
                spotColor = BrandOrange.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(28.dp))
            .background(bgGradient)
            .pressScale(onClick = onClick)
            .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(eyebrow, color = PaperWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.weight(1f))
                if (tag != null) {
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(BrandYellow)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(tag, color = InkCharcoal,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 36.sp, letterSpacing = (-1).sp,
                    modifier = Modifier.weight(1f))
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null,
                        tint = PaperWhite,
                        modifier = Modifier.size(26.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(description,
                color = PaperWhite.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(PaperWhite)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("開始模擬", color = InkCharcoal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.weight(1f))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = PaperWhite)
            }
        }
    }
}

@Composable
private fun HistoryRow(r: InterviewRecord, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x14FFFFFF))
            .pressScale(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandYellow)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(r.type.label,
                        color = InkCharcoal,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(r.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray400)
            }
            Spacer(Modifier.height(6.dp))
            Text(r.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                color = PaperWhite, fontWeight = FontWeight.SemiBold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${r.score}",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp, letterSpacing = (-1).sp)
            Text("分",
                style = MaterialTheme.typography.labelSmall,
                color = InkGray400)
        }
    }
}
