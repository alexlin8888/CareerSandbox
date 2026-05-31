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
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.HandDrawnUnderline
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

private data class CareerRec(
    val id: String,
    val title: String,
    val subtitleEn: String,
    val shortSubtitle: String,
    val salary: String,
    val openings: String,
    val matchScore: Int,
    val missingSkills: List<String>,
    val category: String,
    val icon: ImageVector,
    val isAcademic: Boolean = false,
    val academicNote: String = "",
)

private data class LearningStep(
    val stepNum: Int,
    val term: String,
    val title: String,
    val subtitle: String,
    val tier: StepTier,
)
private enum class StepTier { Primary, Secondary, Tertiary }

private val allCareerRecs = listOf(
    CareerRec(
        id = "data_analyst", title = "資料分析師", subtitleEn = "Data Analyst",
        shortSubtitle = "數據 · 45-65k", salary = "45-65k", openings = "1,240",
        matchScore = 92, missingSkills = listOf("SQL 進階", "A/B test", "用戶研究"),
        category = "數據", icon = Icons.Outlined.Analytics,
    ),
    CareerRec(
        id = "pm", title = "產品經理", subtitleEn = "Product Manager",
        shortSubtitle = "PM · 50-80k", salary = "50-80k", openings = "890",
        matchScore = 78, missingSkills = listOf("PRD 撰寫", "user story", "SaaS 邏輯"),
        category = "產品", icon = Icons.Outlined.WorkOutline,
    ),
    CareerRec(
        id = "growth", title = "行銷策略", subtitleEn = "Growth Marketing",
        shortSubtitle = "Growth · 42-58k", salary = "42-58k", openings = "1,050",
        matchScore = 71, missingSkills = listOf("成長駭客", "SEO", "數據追蹤"),
        category = "產品", icon = Icons.Outlined.TrendingUp,
    ),
    CareerRec(
        id = "ux", title = "UX 設計師", subtitleEn = "UX Designer",
        shortSubtitle = "Design · 48-70k", salary = "48-70k", openings = "620",
        matchScore = 68, missingSkills = listOf("Figma", "使用者訪談", "原型測試"),
        category = "設計", icon = Icons.Outlined.Edit,
    ),
    CareerRec(
        id = "researcher", title = "研究員", subtitleEn = "Researcher",
        shortSubtitle = "學術 · 依機構", salary = "依機構", openings = "380",
        matchScore = 65, missingSkills = listOf("論文發表", "研究方法", "碩博學歷"),
        category = "學術", icon = Icons.Outlined.Lightbulb,
        isAcademic = true,
        academicNote = "想走學術路線?先看看研究員的一天,以及碩博升學的時程規劃。",
    ),
)

private val careerFilters = listOf("全部", "數據", "產品", "設計", "學術")

@Composable
fun CareerExplorationScreen(navController: NavHostController) {
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

    // === 互動狀態 ===
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("全部") }
    var selectedId by remember { mutableStateOf("data_analyst") }
    val excludedIds = remember { mutableStateListOf<String>() }

    // === 衍生清單 ===
    val visibleRecs = allCareerRecs.filter { rec ->
        rec.id !in excludedIds &&
            (activeFilter == "全部" || rec.category == activeFilter) &&
            (searchQuery.isBlank() ||
                rec.title.contains(searchQuery, ignoreCase = true) ||
                rec.subtitleEn.contains(searchQuery, ignoreCase = true))
    }
    val focusedRec = visibleRecs.firstOrNull { it.id == selectedId }
        ?: visibleRecs.maxByOrNull { it.matchScore }
    val otherRecs = visibleRecs.filter { it.id != focusedRec?.id }
    val excludedRecs = allCareerRecs.filter { it.id in excludedIds }

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            CareerHeroSection(onBack = { navController.popBackStack() })

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp)) {
                AnimatedSection(visible = visible, delayMs = 0) {
                    SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                }
                Spacer(Modifier.height(12.dp))

                AnimatedSection(visible = visible, delayMs = 80) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(chipScroll),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        careerFilters.forEach { f ->
                            FilterPill(
                                text = f,
                                active = activeFilter == f,
                                onClick = { activeFilter = f },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                // === 學術路線 banner(只在學術 filter 出現)===
                if (activeFilter == "學術") {
                    val academicRec = allCareerRecs.first { it.isAcademic }
                    AcademicBanner(note = academicRec.academicNote)
                    Spacer(Modifier.height(14.dp))
                }

                if (focusedRec != null) {
                    AnimatedSection(visible = visible, delayMs = 160) {
                        TopMatchCard(rec = focusedRec, onViewPath = { navController.navigate(Routes.FIT_ANALYSIS) })
                    }
                    Spacer(Modifier.height(12.dp))
                } else {
                    // 全被排除 / 搜尋無結果
                    EmptyRecState(
                        hasExcluded = excludedRecs.isNotEmpty(),
                        onClearFilter = {
                            searchQuery = ""; activeFilter = "全部"
                        },
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // === 其他推薦(可點選聚焦 + 可排除)===
                if (otherRecs.isNotEmpty()) {
                    AnimatedSection(visible = visible, delayMs = 240) {
                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            otherRecs.chunked(2).forEach { rowItems ->
                                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                    rowItems.forEach { rec ->
                                        SecondaryRecCard(
                                            rec = rec,
                                            onClick = { selectedId = rec.id },
                                            onExclude = {
                                                if (rec.id !in excludedIds) excludedIds.add(rec.id)
                                                if (selectedId == rec.id) selectedId = ""
                                            },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }

                // === 已排除(減法)===
                if (excludedRecs.isNotEmpty()) {
                    ExcludedSection(
                        excluded = excludedRecs,
                        onRestore = { id -> excludedIds.remove(id) },
                    )
                    Spacer(Modifier.height(24.dp))
                }

                // === 學習路徑(跟著聚焦職位變)===
                if (focusedRec != null) {
                    AnimatedSection(visible = visible, delayMs = 320) {
                        Text(
                            "給「${focusedRec.title}」的學習路徑",
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
                                .pressScale { navController.navigate(Routes.FIT_ANALYSIS) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("看我跟這條路的差距", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
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

        // Back button
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

        // === undraw exploring illustration ===
        Image(
            painter = painterResource(R.drawable.undraw_exploring_fzmr),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = 8.dp)
                .size(150.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp)
                .fillMaxWidth(0.6f),
        ) {
            Text("CAREER PATHS",
                color = Color(0xFF993C1D),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            Box {
                Text("職涯探索",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 30.sp,
                    lineHeight = 30.sp)
                HandDrawnUnderline(
                    width = 84.dp,
                    color = BrandYellow,
                    strokeWidth = 3.5f,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(y = 5.dp)
                        .height(9.dp)
                        .width(84.dp),
                )
            }
            Spacer(Modifier.height(7.dp))
            Text("3 條最適合你的路徑",
                color = PaperWhite.copy(alpha = 0.95f),
                fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(PaperWhite)
                    .padding(horizontal = 11.dp, vertical = 5.dp),
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(6.dp))
                Text("基於 4 段經歷 · 42 門修課",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PaperWhite)
            .border(1.dp, InkGray100, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = InkGray400, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text("搜尋職位,例如 設計、PM...", color = InkGray400, fontSize = 13.sp)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = InkBlack, fontSize = 13.sp),
                cursorBrush = SolidColor(BrandDeepOrange),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = "清除",
                tint = InkGray400,
                modifier = Modifier.size(16.dp).pressScale { onQueryChange("") },
            )
        } else {
            Icon(Icons.Outlined.Tune, contentDescription = null, tint = InkGray500, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun FilterPill(text: String, active: Boolean, onClick: () -> Unit) {
    val base = if (active) {
        Modifier
            .clip(CircleShape)
            .background(InkBlack)
    } else {
        Modifier
            .clip(CircleShape)
            .background(PaperWhite)
            .border(1.dp, InkGray200, CircleShape)
    }
    Box(
        modifier = base
            .pressScale(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            color = if (active) PaperWhite else InkBlack,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

/** 學術路線 banner — 訪談洞察:受訪者想「看研究員生活、更早決定方向」 */
@Composable
private fun AcademicBanner(note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(InkDeepBlue, InkSlate)))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PaperWhite.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("學術路線", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(Modifier.height(3.dp))
            Text(note, color = PaperWhite.copy(alpha = 0.85f), fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

/** 全排除 / 搜尋無結果空狀態 */
@Composable
private fun EmptyRecState(hasExcluded: Boolean, onClearFilter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PaperWhite)
            .border(1.dp, InkGray100, RoundedCornerShape(20.dp))
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = InkGray300, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            if (hasExcluded) "這個條件下的職位都被你排除了" else "找不到符合的職位",
            color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "試試換個關鍵字或篩選",
            color = InkGray500, fontSize = 12.sp,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(InkGray100)
                .pressScale(onClick = onClearFilter)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text("清除條件", color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

/** 已排除區(減法)— 可還原 */
@Composable
private fun ExcludedSection(excluded: List<CareerRec>, onRestore: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InkGray100.copy(alpha = 0.5f))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().pressScale { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Tune, contentDescription = null, tint = InkGray500, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "已排除 ${excluded.size} 個職位",
                color = InkGray700, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            Text("讓推薦更聚焦", color = InkGray400, fontSize = 11.sp)
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.KeyboardArrowUp,
                contentDescription = null, tint = InkGray500,
                modifier = Modifier.size(18.dp).rotate(if (expanded) 0f else 180f),
            )
        }
        if (expanded) {
            Spacer(Modifier.height(10.dp))
            excluded.forEach { rec ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(rec.title, color = InkGray500, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandPeach)
                            .pressScale { onRestore(rec.id) }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text("拉回", color = BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopMatchCard(rec: CareerRec, onViewPath: () -> Unit = {}) {
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
                    .pressScale(onClick = onViewPath),
                contentAlignment = Alignment.Center,
            ) {
                Text("查看完整路徑", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SecondaryRecCard(
    rec: CareerRec,
    onClick: () -> Unit,
    onExclude: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val score = rec.matchScore
    val (scoreColor, barColor, iconBg, iconTint) = when {
        score >= 75 -> arrayOf(BrandDeepOrange, BrandDeepOrange, BrandPeach.copy(alpha = 0.6f), BrandDeepOrange)
        else -> arrayOf(Color(0xFFBA7517), BrandAmber, BrandYellow.copy(alpha = 0.3f), Color(0xFFBA7517))
    }
    val animScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "${rec.id}-score",
    )
    val animBar by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(1100, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "${rec.id}-bar",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick)
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
                Icon(rec.icon, contentDescription = null, tint = iconTint as Color, modifier = Modifier.size(16.dp))
            }
            // 排除按鈕(減法)
            Icon(
                Icons.Outlined.Close,
                contentDescription = "不想看這類",
                tint = InkGray400,
                modifier = Modifier.size(16.dp).pressScale(onClick = onExclude),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(rec.title, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(2.dp))
        Text(rec.shortSubtitle, color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
            .background(PaperWhite)
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
