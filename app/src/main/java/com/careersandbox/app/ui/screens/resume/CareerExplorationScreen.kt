package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class CareerRec(
    val title: String,
    val subtitleEn: String,
    val salary: String,
    val openings: String,
    val matchScore: Int,
    val missingSkills: List<String>,
)

private data class LearningStep(
    val stepNum: Int,
    val term: String,
    val title: String,
    val subtitle: String,
    val tier: StepTier,
)
private enum class StepTier { Primary, Secondary, Tertiary }

@Composable
fun CareerExplorationScreen(navController: NavHostController) {
    val topMatch = remember {
        CareerRec(
            title = "資料分析師",
            subtitleEn = "Data Analyst",
            salary = "45-65k",
            openings = "1,240",
            matchScore = 92,
            missingSkills = listOf("SQL 進階", "A/B test", "用戶研究"),
        )
    }
    val secondaryRecs = remember {
        listOf(
            "產品經理" to 78,
            "行銷策略" to 71,
        )
    }
    val steps = remember {
        listOf(
            LearningStep(1, "本學期", "資料庫管理", "商管院 · 3 學分", StepTier.Primary),
            LearningStep(2, "下學期", "GDA 證照", "Coursera · 6 月", StepTier.Secondary),
            LearningStep(3, "暑期", "外商 BI 實習", "補用戶研究短板", StepTier.Tertiary),
        )
    }
    val chipScroll = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            CareerHeroSection(onBack = { navController.popBackStack() })

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp)) {
                AnimatedSection(visible = visible, delayMs = 0) {
                    SearchBar()
                }
                Spacer(Modifier.height(12.dp))

                AnimatedSection(visible = visible, delayMs = 80) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(chipScroll),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        FilterPill("全部", active = true)
                        FilterPill("應屆", active = false)
                        FilterPill("數據", active = false)
                        FilterPill("產品", active = false)
                        FilterPill("設計", active = false)
                    }
                }
                Spacer(Modifier.height(18.dp))

                AnimatedSection(visible = visible, delayMs = 160) {
                    TopMatchCard(rec = topMatch)
                }
                Spacer(Modifier.height(12.dp))

                AnimatedSection(visible = visible, delayMs = 240) {
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        SecondaryRecCard(
                            title = secondaryRecs[0].first,
                            subtitle = "PM · 50-80k",
                            score = secondaryRecs[0].second,
                            icon = Icons.Outlined.WorkOutline,
                            modifier = Modifier.weight(1f),
                        )
                        SecondaryRecCard(
                            title = secondaryRecs[1].first,
                            subtitle = "Growth · 42-58k",
                            score = secondaryRecs[1].second,
                            icon = Icons.Outlined.TrendingUp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))

                AnimatedSection(visible = visible, delayMs = 320) {
                    Text(
                        "給「${topMatch.title}」的學習路徑",
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                    )
                }
                Spacer(Modifier.height(12.dp))

                AnimatedSection(visible = visible, delayMs = 400) {
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        steps.forEach { step ->
                            LearningStepCard(step)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                AnimatedSection(visible = visible, delayMs = 520) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InkBlack)
                            .pressScale { /* TODO: start learning path */ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("開始學習路徑", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedSection(visible: Boolean, delayMs: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(550, delayMillis = delayMs, easing = FastOutSlowInEasing),
            ),
    ) {
        content()
    }
}

@Composable
private fun CareerHeroSection(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandYellow, BrandAmber, BrandOrange),
            ),
            heightDp = 240,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Box(
            Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.2f))
                .pressScale(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp)
                .fillMaxWidth(0.65f),
        ) {
            Text("CAREER PATHS",
                color = Color(0xFF993C1D),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(10.dp))
            Text("職涯探索",
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                lineHeight = 36.sp)
            Spacer(Modifier.height(6.dp))
            Text("3 條最適合你的路徑",
                color = PaperWhite,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PaperWhite)
                    .padding(horizontal = 11.dp, vertical = 4.dp),
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(5.dp))
                Text("基於 4 段經歷 · 42 門修課",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp)
            }
        }

        Image(
            painter = painterResource(R.drawable.undraw_feedback_ebmx),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = 8.dp)
                .size(150.dp)
                .alpha(0.92f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = InkGray400, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text("搜尋其他職位...", color = InkGray400, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.Tune, contentDescription = null, tint = InkGray500, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun FilterPill(text: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (active) InkBlack else InkGray100)
            .padding(horizontal = 13.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = if (active) PaperWhite else InkGray700,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun TopMatchCard(rec: CareerRec) {
    val animScore by animateIntAsState(
        targetValue = rec.matchScore,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "top_match",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange))),
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(PaperWhite.copy(alpha = 0.08f)),
        )

        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PaperWhite)
                        .padding(horizontal = 11.dp, vertical = 4.dp),
                ) {
                    Text("TOP MATCH",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp)
                }
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = PaperWhite.copy(alpha = 0.85f), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text(rec.title, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 24.sp, lineHeight = 28.sp)
            Spacer(Modifier.height(2.dp))
            Text(rec.subtitleEn, color = PaperWhite.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("月薪", color = PaperWhite.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(rec.salary, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
                Column {
                    Text("職缺", color = PaperWhite.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(rec.openings, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 17.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("FIT", color = PaperWhite.copy(alpha = 0.7f), fontSize = 10.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$animScore", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 44.sp, lineHeight = 44.sp)
                        Text("%", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.padding(bottom = 5.dp))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                rec.missingSkills.forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PaperWhite.copy(alpha = 0.2f))
                            .padding(horizontal = 11.dp, vertical = 4.dp),
                    ) {
                        Text(skill, color = PaperWhite, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaperWhite)
                    .pressScale { /* navigate to detail */ },
                contentAlignment = Alignment.Center,
            ) {
                Text("查看完整路徑", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SecondaryRecCard(
    title: String,
    subtitle: String,
    score: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val (scoreColor, barColor, iconBg, iconTint) = when {
        score >= 75 -> arrayOf(BrandDeepOrange, BrandDeepOrange, BrandPeach.copy(alpha = 0.6f), BrandDeepOrange)
        else -> arrayOf(Color(0xFFBA7517), BrandAmber, BrandYellow.copy(alpha = 0.3f), Color(0xFFBA7517))
    }
    val animScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "$title-score",
    )
    val animBar by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1100, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "$title-bar",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale {}
            .padding(13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(iconBg as Color),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint as Color, modifier = Modifier.size(16.dp))
            }
            Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = InkGray300, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(title, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text("FIT", color = InkGray400, fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$animScore", color = scoreColor as Color, fontWeight = FontWeight.Black, fontSize = 22.sp, lineHeight = 22.sp)
                Text("%", color = scoreColor, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(4.dp)) {
            drawRoundRect(
                color = InkGray200,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
            val w = size.width * animBar.coerceIn(0f, 1f)
            if (w > 0f) {
                drawRoundRect(
                    color = barColor as Color,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, size.height),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun LearningStepCard(step: LearningStep) {
    val numBg: Color = when (step.tier) {
        StepTier.Primary -> BrandDeepOrange
        StepTier.Secondary -> BrandAmber
        StepTier.Tertiary -> InkGray200
    }
    val numTextColor: Color = when (step.tier) {
        StepTier.Tertiary -> InkGray500
        else -> PaperWhite
    }
    val chipBg: Color = when (step.tier) {
        StepTier.Primary -> BrandPeach
        StepTier.Secondary -> BrandPeach.copy(alpha = 0.6f)
        StepTier.Tertiary -> InkGray100
    }
    val chipTextColor: Color = when (step.tier) {
        StepTier.Tertiary -> InkGray500
        else -> BrandDeepOrange
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .pressScale {}
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)).background(numBg),
            contentAlignment = Alignment.Center,
        ) {
            Text("${step.stepNum}", color = numTextColor, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(chipBg)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text(step.term, color = chipTextColor, fontWeight = FontWeight.Black, fontSize = 9.sp, letterSpacing = 0.5.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(step.title, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(1.dp))
            Text(step.subtitle, color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400, modifier = Modifier.size(18.dp))
    }
}
