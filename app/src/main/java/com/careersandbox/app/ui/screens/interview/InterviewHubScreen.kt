package com.careersandbox.app.ui.screens.interview

import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
            StaggeredAppear(delayMillis = 0) { HeroSection() }

            Spacer(Modifier.height(20.dp))

            // === #6 Avatar 成長卡 ===
            StaggeredAppear(delayMillis = 90) { AvatarGrowthCard() }

            Spacer(Modifier.height(20.dp))

            // === #1 快速練習(低門檻入口,與正式 mock 區分)===
            StaggeredAppear(delayMillis = 170) { QuickPracticeCard(navController) }

            Spacer(Modifier.height(20.dp))

            // === 本週練習(連續天數)===
            StaggeredAppear(delayMillis = 200) { WeeklyStreak() }

            Spacer(Modifier.height(20.dp))

            // === 能力輪廓(收合,點開詳細)===
            StaggeredAppear(delayMillis = 225) { AbilityProfileEntry() }

            Spacer(Modifier.height(32.dp))

            // === 兩個方案卡(都帶插畫)===
            StaggeredAppear(delayMillis = 250) { PlanCards(navController) }

            Spacer(Modifier.height(24.dp))

            // === 三對一 panel 入口(已上線)===

            Spacer(Modifier.height(32.dp))

            // === 歷史紀錄(無框列表)===
            StaggeredAppear(delayMillis = 410) { HistorySection(navController) }

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
        // 品牌大使(打氣,右下角)
        Image(
            painter = painterResource(R.drawable.beaver_celebrate),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 10.dp, bottom = 4.dp)
                .size(118.dp),
            contentScale = ContentScale.Fit,
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
                Text("已完成 4 次 · 平均 74 分",
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
            cardBg = SolidColor(BrandDeepOrange),
            illustrationRes = R.drawable.undraw_interview_yz52,
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_INDIVIDUAL) },
        )

        // 團體面試卡
        PlanCard(
            number = "02",
            title = "團體面試",
            eyebrow = "3-5 人小組",
            description = "AI 扮演其他應徵者,\n真實小組討論演練",
            tagText = "AI 同儕同場",
            tagBg = BrandYellow,
            tagFg = InkCharcoal,
            cardBg = SolidColor(InkCharcoal),
            illustrationRes = R.drawable.undraw_group_video_k4jx,
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_GROUP) },
        )

        // 影像面試卡(新,創新性)
        PlanCard(
            number = "03",
            title = "影像面試",
            eyebrow = "鏡頭 + 即時覺察",
            description = "開鏡頭對著河狸面試官練,\n看自己的眼神與表現",
            tagText = "練習工具",
            tagBg = AccentGreen,
            tagFg = PaperWhite,
            cardBg = SolidColor(BrandOrange),
            illustrationRes = R.drawable.undraw_video_call_i5de,
            onClick = { navController.navigate(Routes.INTERVIEW_VIDEO) },
        )
        // 面試頁改版預覽(WIP)
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

/* ===================== #6 Avatar 成長卡 ===================== */

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
/* 面試六維能力(暫為示範資料,日後接後端) */
private val INTERVIEW_ABILITIES = listOf(
    Triple("內容深度", 78, 4),
    Triple("邏輯清晰", 82, 2),
    Triple("表達流暢", 71, 6),
    Triple("互動", 68, 3),
    Triple("應變", 64, -1),
    Triple("自信", 80, 5),
)
private const val INTERVIEW_POWER = 74

@Composable
private fun AvatarGrowthCard() {
    val power = INTERVIEW_POWER
    val rank = rankOf(power)
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BrandPeach.copy(alpha = 0.4f))
            .padding(18.dp),
    ) {
        // 英雄列：段位徽章 + 段位河狸 + 面試力
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShieldBadge(rank.title)
            Spacer(Modifier.width(10.dp))
            Image(
                painter = painterResource(rank.beaver),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp),
            )
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("面試力", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("${rememberCountUp(power)}", color = InkBlack, fontSize = 44.sp, fontWeight = FontWeight.Black, lineHeight = 46.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        // 段位進度條（自帶「再 X 分晉升 Y」）
        RankProgressBar(power)
    }
}

/* ===================== 本週練習(連續天數)===================== */

@Composable
private fun WeeklyStreak() {
    val days = listOf("一", "二", "三", "四", "五", "六", "日")
    val doneCount = 4   // 一~四 已完成
    val todayIdx = 4    // 五 = 今天
    Column(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Text("本週練習", color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            days.forEachIndexed { i, d ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val state = when {
                        i < doneCount -> 0   // 已完成
                        i == todayIdx -> 1   // 今天
                        else -> 2            // 未來
                    }
                    // 完成/今天=點燃火焰, 今天額外加一圈橘色光環, 未來=熄滅火焰(半透明)
                    Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        if (state == 1) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape)
                                    .border(2.dp, BrandOrange, CircleShape),
                            )
                        }
                        Image(
                            painter = painterResource(
                                if (state == 2) R.drawable.flame_unlit else R.drawable.flame_lit,
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(if (state == 1) 32.dp else 30.dp)
                                .alpha(if (state == 2) 0.4f else 1f),
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        d,
                        color = if (i == todayIdx) BrandDeepOrange else InkGray500,
                        fontSize = 11.sp,
                        fontWeight = if (i == todayIdx) FontWeight.Black else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/* ===================== 能力輪廓(收合,點開展開詳細)===================== */

@Composable
private fun AbilityProfileEntry() {
    val abilities = INTERVIEW_ABILITIES
    val power = INTERVIEW_POWER
    val strongest = abilities.maxByOrNull { it.second } ?: abilities.first()
    val weakest = abilities.minByOrNull { it.second } ?: abilities.first()
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        // 收合列
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(InkGray100)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("能力輪廓", color = InkBlack, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(3.dp))
                Text(
                    "最強 ${strongest.first} ${strongest.second} ・ 最弱 ${weakest.first} ${weakest.second}",
                    color = InkGray500, fontSize = 12.sp,
                )
            }
            Text(
                if (expanded) "收合" else "查看詳細 ›",
                color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            )
        }
        // 點開：河狸對話框 + 能力排行長條 + 下一階解鎖
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandPeach.copy(alpha = 0.4f))
                    .padding(16.dp),
            ) {
                val sorted = abilities.sortedByDescending { it.second }
                val mostImproved = abilities.maxByOrNull { it.third } ?: abilities.first()
                // 河狸講話 + 對話框
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(PaperWhite)
                            .padding(horizontal = 13.dp, vertical = 11.dp),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = BrandDeepOrange, fontWeight = FontWeight.Black)) { append(mostImproved.first) }
                                append(" +${mostImproved.third} 進步最多!就差 ")
                                withStyle(SpanStyle(color = BrandDeepOrange, fontWeight = FontWeight.Black)) { append(weakest.first) }
                                append(",下一階一起補。")
                            },
                            color = InkGray700, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Image(
                        painter = painterResource(rankOf(power).beaver),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(64.dp),
                    )
                }
                Spacer(Modifier.height(14.dp))
                // 能力排行長條(強→弱)
                sorted.forEachIndexed { idx, (label, value, delta) ->
                    AbilityBar(label = label, value = value, delta = delta, isWeak = label == weakest.first)
                    if (idx < sorted.size - 1) Spacer(Modifier.height(11.dp))
                }
                Spacer(Modifier.height(14.dp))
                NextUnlockCallout(power)
            }
        }
    }
}

/* ===================== 小元件:能力長條 ===================== */

@Composable
private fun AbilityBar(label: String, value: Int, delta: Int, isWeak: Boolean) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val fill by animateFloatAsState(
        targetValue = if (appear) value / 100f else 0f,
        animationSpec = tween(800),
        label = "abFill",
    )
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text(
                label,
                color = if (isWeak) BrandDeepOrange else InkBlack,
                fontSize = 13.sp, fontWeight = FontWeight.Bold,
            )
            if (isWeak) {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(4.dp)).background(BrandDeepOrange)
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text("要補", color = PaperWhite, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.weight(1f))
            Text("${rememberCountUp(value)}", color = InkBlack, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(3.dp))
            Text(
                if (delta >= 0) "+$delta" else "$delta",
                color = if (delta >= 0) AccentGreen else AccentRed,
                fontSize = 11.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 1.dp),
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(50)).background(PaperWhite)) {
            Box(
                Modifier.fillMaxWidth(fill).fillMaxHeight().clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            if (isWeak) listOf(Color(0xFFFCA98A), BrandDeepOrange)
                            else listOf(BrandAmber, BrandOrange),
                        ),
                    ),
            )
        }
    }
}

/* 段位門檻：新手 0-59 ・ 新星 60-74 ・ 好手 75-84 ・ 大師 85-100 */
private val RANK_TIERS = listOf(0, 60, 75, 85)
private val RANK_NAMES = listOf("新手", "新星", "好手", "大師")
private fun rankIndexOf(power: Int): Int = RANK_TIERS.indexOfLast { power >= it }.coerceIn(0, 3)

@Composable
private fun RankProgressBar(power: Int) {
    val idx = rankIndexOf(power)
    val nextThreshold = if (idx < 3) RANK_TIERS[idx + 1] else 100
    val toNext = (nextThreshold - power).coerceAtLeast(0)
    val nextName = if (idx < 3) RANK_NAMES[idx + 1] else "頂峰"

    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val fill by animateFloatAsState(
        targetValue = if (appear) power / 100f else 0f,
        animationSpec = tween(900),
        label = "rankFill",
    )

    Column(Modifier.fillMaxWidth()) {
        BoxWithConstraints(Modifier.fillMaxWidth().height(16.dp)) {
            val full = maxWidth
            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(PaperWhite.copy(alpha = 0.7f)))
            Box(
                Modifier.fillMaxHeight().width(full * fill).clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(BrandAmber, BrandOrange, BrandDeepOrange))),
            )
            // 段位分界
            listOf(0.60f, 0.75f, 0.85f).forEach { f ->
                Box(
                    Modifier.fillMaxHeight().width(2.dp).offset(x = full * f)
                        .background(InkCharcoal.copy(alpha = 0.22f)),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            RANK_NAMES.forEachIndexed { i, n ->
                Text(
                    n,
                    color = if (i == idx) BrandDeepOrange else InkGray500,
                    fontSize = 11.sp,
                    fontWeight = if (i == idx) FontWeight.Black else FontWeight.Medium,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (idx < 3) "再 $toNext 分晉升 $nextName" else "已達頂峰段位",
            color = InkCharcoal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun NextUnlockCallout(power: Int) {
    val idx = rankIndexOf(power)
    val reward = when (idx) {
        0 -> "面試官會開始追問你的回答細節"
        1 -> "解鎖群組面試動態 ・ 即戰力深度提問"
        2 -> "解鎖高壓情境題 ・ 跨部門協作評估"
        else -> "已達頂峰，挑戰刷新自己的最高分"
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(BrandAmber.copy(alpha = 0.30f)).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(4.dp).height(34.dp).clip(RoundedCornerShape(2.dp)).background(BrandDeepOrange))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                if (idx < 3) "下一階解鎖" else "頂峰",
                color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(reward, color = InkCharcoal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
        }
    }
}

private data class InterviewRank(val title: String, val beaver: Int)

private fun rankOf(score: Int): InterviewRank = when {
    score >= 85 -> InterviewRank("面試大師 I", R.drawable.beaver_rank_master)
    score >= 75 -> InterviewRank("面試好手 II", R.drawable.beaver_rank_expert)
    score >= 60 -> InterviewRank("面試新星 IV", R.drawable.beaver_rank_star)
    else -> InterviewRank("面試新手 V", R.drawable.beaver_rank_novice)
}

@Composable
private fun ShieldBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(CutCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(BrandAmber)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text, color = InkCharcoal, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
    }
}

@Composable
private fun StatCell(label: String, value: Int, delta: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${rememberCountUp(value)}", color = InkBlack, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.width(3.dp))
            Text(
                if (delta >= 0) "+$delta" else "$delta",
                color = if (delta >= 0) AccentGreen else AccentRed,
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun HexRadar(values: List<Int>, modifier: Modifier = Modifier) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val progress by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = tween(900),
        label = "radarGrow",
    )
    Canvas(modifier = modifier) {
        val n = values.size
        if (n < 3) return@Canvas
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rMax = size.minDimension / 2f * 0.92f
        fun point(i: Int, r: Float): Offset {
            val ang = Math.toRadians(-90.0 + i * 360.0 / n)
            return Offset(cx + (r * cos(ang)).toFloat(), cy + (r * sin(ang)).toFloat())
        }
        // 網格(兩圈)+ 軸線
        listOf(0.5f, 1f).forEach { ring ->
            val grid = Path()
            for (i in 0 until n) {
                val pt = point(i, rMax * ring)
                if (i == 0) grid.moveTo(pt.x, pt.y) else grid.lineTo(pt.x, pt.y)
            }
            grid.close()
            drawPath(grid, color = PaperWhite.copy(alpha = 0.9f), style = Stroke(width = 1.2.dp.toPx()))
        }
        for (i in 0 until n) {
            drawLine(
                color = PaperWhite.copy(alpha = 0.7f),
                start = Offset(cx, cy),
                end = point(i, rMax),
                strokeWidth = 1.dp.toPx(),
            )
        }
        // 能力形狀
        val shape = Path()
        for (i in 0 until n) {
            val r = rMax * (values[i] / 100f) * progress
            val pt = point(i, r)
            if (i == 0) shape.moveTo(pt.x, pt.y) else shape.lineTo(pt.x, pt.y)
        }
        shape.close()
        drawPath(shape, color = BrandDeepOrange.copy(alpha = 0.30f))
        drawPath(shape, color = BrandDeepOrange, style = Stroke(width = 2.dp.toPx()))
        for (i in 0 until n) {
            val r = rMax * (values[i] / 100f) * progress
            drawCircle(color = BrandDeepOrange, radius = 3.dp.toPx(), center = point(i, r))
        }
    }
}

/* ===================== #1 快速練習(低門檻入口)===================== */

@Composable
private fun QuickPracticeCard(navController: NavHostController) {
    val pulse = rememberInfiniteTransition(label = "quickPulse")
    val glow by pulse.animateFloat(
        initialValue = 0.85f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "quickGlow",
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkBlack)
            .pressScale {
                navController.navigate(Routes.INTERVIEW_QUICK)
            }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(50.dp).clip(CircleShape)
                .background(BrandOrange.copy(alpha = glow)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Bolt, contentDescription = null,
                tint = PaperWhite, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("快速面試", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandOrange)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("60 秒一題", color = PaperWhite, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                "免設定 · 抽一題就開始 · 答完馬上再來一題",
                color = PaperWhite.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 17.sp,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = PaperWhite.copy(alpha = 0.8f))
    }
}

