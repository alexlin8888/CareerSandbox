package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.InterviewRecord
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun InterviewHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            // === Hero 區 ===
            HeroSection()

            Spacer(Modifier.height(24.dp))

            // === 兩個方案卡(都帶插畫)===
            PlanCards(navController)

            Spacer(Modifier.height(32.dp))

            // === 歷史紀錄(無框列表)===
            HistorySection(navController)

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeroSection() {
    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 220,
        )
        ScatteredDecorations(
            modifier = Modifier.fillMaxSize().alpha(0.6f)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
        ) {
            Text("INTERVIEW PRACTICE",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("面試")
                    withStyle(SpanStyle(color = BrandYellow)) { append("不再") }
                    append("\n緊張到失常。")
                },
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                lineHeight = 42.sp,
            )
            Spacer(Modifier.height(12.dp))
            // 統計
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(BrandYellow))
                Spacer(Modifier.width(6.dp))
                Text("已完成 4 次 · 平均 71 分",
                    color = PaperWhite.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PlanCards(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 大標
        Text(
            text = buildAnnotatedString {
                append("選一種")
                withStyle(SpanStyle(color = BrandOrange)) { append("開始") }
            },
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            modifier = Modifier.padding(start = 4.dp),
        )

        // 個人面試卡
        PlanCard(
            number = "01",
            title = "個人面試",
            eyebrow = "1 對 1",
            description = "AI 扮演面試官,\n從履歷出題、即時追問",
            tagText = "入門推薦",
            tagBg = BrandYellow,
            tagFg = InkCharcoal,
            cardBg = Brush.linearGradient(listOf(BrandDeepOrange, BrandOrange)),
            illustrationRes = R.drawable.undraw_video_call_i5de,
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_INDIVIDUAL) },
        )

        // 團體面試卡(MVP 差異化)
        PlanCard(
            number = "02",
            title = "團體面試",
            eyebrow = "3-5 人小組",
            description = "AI 扮演其他應徵者,\n真實小組討論演練",
            tagText = "市面少見 · MVP",
            tagBg = BrandYellow,
            tagFg = InkCharcoal,
            cardBg = Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF374151))),
            illustrationRes = R.drawable.undraw_group_video_k4jx,
            onClick = { navController.navigate(Routes.INTERVIEW_LIVE_GROUP) },
        )
    }
}

@Composable
private fun PlanCard(
    number: String,
    title: String,
    eyebrow: String,
    description: String,
    tagText: String,
    tagBg: Color,
    tagFg: Color,
    cardBg: Brush,
    illustrationRes: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(20.dp, RoundedCornerShape(28.dp),
                spotColor = BrandOrange.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(28.dp))
            .background(cardBg)
            .pressScale(onClick = onClick),
    ) {
        // 卡內裝飾線稿
        ScatteredDecorations(
            modifier = Modifier.fillMaxSize().alpha(0.3f)
        )
        // 文字內容(左側)
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.6f),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(number,
                        color = PaperWhite.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(eyebrow,
                        color = PaperWhite.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(6.dp))
                Text(title,
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 36.sp)
                Spacer(Modifier.height(8.dp))
                Text(description,
                    color = PaperWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp)
            }
            // 底部 tag + 箭頭
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(tagBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(tagText,
                        color = tagFg,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = PaperWhite)
            }
        }
        // 插畫破框(右下)
        Image(
            painter = painterResource(illustrationRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 0.dp)
                .size(150.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun HistorySection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("歷史紀錄",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f))
            Text("全部",
                color = BrandOrange,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.INTERVIEW_HISTORY)
                })
        }
        Spacer(Modifier.height(12.dp))
        MockData.interviewHistory.forEachIndexed { idx, r ->
            HistoryRow(r) { navController.navigate(Routes.INTERVIEW_REPORT) }
            if (idx < MockData.interviewHistory.size - 1) {
                SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun HistoryRow(r: InterviewRecord, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandYellow.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(r.type.label,
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(r.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray400)
            }
            Spacer(Modifier.height(4.dp))
            Text(r.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${r.score}",
                color = BrandOrange,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp)
            Spacer(Modifier.width(2.dp))
            Text("分",
                style = MaterialTheme.typography.labelSmall,
                color = InkGray500,
                modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}
