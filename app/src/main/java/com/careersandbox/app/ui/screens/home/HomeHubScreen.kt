package com.careersandbox.app.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Article
import com.careersandbox.app.data.model.ArticleCategory
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun HomeHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            HeroSection(navController)
            Spacer(Modifier.height(20.dp))
            ArticleSection(navController)
            Spacer(Modifier.height(24.dp))
            QuickActionsBorderless(navController)
            Spacer(Modifier.height(28.dp))
            ModuleSection(navController)
            Spacer(Modifier.height(28.dp))
            CompetitionSection(navController)
            Spacer(Modifier.height(32.dp))

            // 頁尾品牌大使(招手)
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_wave),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    "今天也一起加油!",
                    color = InkGray500,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** Hero 區:wave 漸層 + 大字 + 插畫 + **僅在此區的線稿裝飾** */
@Composable
private fun HeroSection(navController: NavHostController) {
    val stat = MockData.homeStat
    Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
        // 1. wave 漸層背景(最底層)
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(800f, 600f),
            ),
            heightDp = 360,
        )

        // 2. 線稿裝飾(只在 hero 區內)
        ScatteredDecorations(
            modifier = Modifier.fillMaxSize().alpha(0.6f)
        )

        // 3. 文字內容(避開右下插畫位置)
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
        ) {
            // 頂部問候
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("下午好,", color = PaperWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.weight(1f))
                AnimatedBell(
                    unreadCount = MockData.notifications.count { !it.read },
                    onClick = {
                        navController.navigate(Routes.NOTIFICATIONS_ALL)
                    },
                )
            }
            Spacer(Modifier.height(4.dp))
            // 姓名 + 年級
            Row(verticalAlignment = Alignment.Bottom) {
                Box {
                    Text(MockData.currentUser.name,
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 40.sp,
                        lineHeight = 44.sp)
                    HandDrawnUnderline(
                        width = 84.dp,
                        color = BrandYellow,
                        strokeWidth = 4f,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 2.dp)
                            .offset(y = 6.dp)
                            .height(10.dp)
                            .width(84.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("大三",
                    color = BrandYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp))
            }

            Spacer(Modifier.height(20.dp))

            // 64% + chips 並排
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                // 左:64% 大數字
                Column(modifier = Modifier.fillMaxWidth(0.55f)) {
                    Text(
                        "這週你完成了",
                        color = PaperWhite.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${stat.resumeCompletion}",
                            color = PaperWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 56.sp,
                            lineHeight = 60.sp,
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "%",
                            color = PaperWhite.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(bottom = 10.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "履歷",
                            color = PaperWhite.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 14.dp),
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                // 右:streak / 進步 chips(放頂部對齊文字)
                Column(
                    modifier = Modifier.padding(bottom = 60.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StreakChip(
                        leadingIcon = Icons.Outlined.LocalFireDepartment,
                        text = "連續",
                        value = "5",
                        suffix = "天",
                    )
                    StreakChip(
                        leadingIcon = Icons.Outlined.TrendingUp,
                        text = "比上週",
                        value = "+12",
                        suffix = "%",
                    )
                }
            }
        }

        // 4. 插畫破框(右下,但縮小避免擠到文字)
        Image(
            painter = painterResource(R.drawable.undraw_online_information_hhp2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 24.dp)
                .size(140.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

/** Hero 右上 streak / 進步 chip — 白底實色,跟 hero 對比強 */
@Composable
private fun StreakChip(
    leadingIcon: ImageVector,
    text: String,
    value: String,
    suffix: String,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(PaperWhite)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            leadingIcon,
            contentDescription = null,
            tint = BrandDeepOrange,
            modifier = Modifier.size(13.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text,
            color = InkGray500,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(3.dp))
        Text(
            value,
            color = InkBlack,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
        )
        Text(
            suffix,
            color = InkGray500,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** 快速入口 — 無框 icon + 文字 */
@Composable
private fun QuickActionsBorderless(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BorderlessIconAction("加經驗", Icons.Outlined.AddCircle, BrandOrange) {
            navController.navigate(Routes.EXPERIENCE_EDIT)
        }
        BorderlessIconAction("練面試", Icons.Outlined.Mic, BrandDeepOrange) {
            navController.navigate(Routes.INTERVIEW_HUB)
        }
        BorderlessIconAction("我的履歷", Icons.Outlined.Description, GlowPurple) {
            navController.navigate(Routes.RESUME_HUB)
        }
        BorderlessIconAction("找競賽", Icons.Outlined.EmojiEvents, AccentGreen) {
            navController.navigate(Routes.COMPETITION_LIST)
        }
    }
}

@Composable
private fun BorderlessIconAction(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .pressScale(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label,
                tint = accent, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label,
            color = InkBlack,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ModuleSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = buildAnnotatedString {
                append("你可以")
                withStyle(SpanStyle(color = BrandOrange)) { append("做") }
                append("的事")
            },
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text("4 個方向,從哪開始都可以",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        BorderlessModuleRow(
            number = "01",
            title = "AI 面試模擬",
            subtitle = "個人 / 團體,真實情境演練",
            tag = "MVP",
            tagColor = BrandYellow,
            accent = BrandOrange,
            icon = Icons.Outlined.Mic,
            onClick = { navController.navigate(Routes.INTERVIEW_HUB) },
        )
        SectionDivider(modifier = Modifier.padding(vertical = 18.dp))

        BorderlessModuleRow(
            number = "02",
            title = "經驗轉譯",
            subtitle = "整理你做過的事,寫成履歷",
            tag = null,
            tagColor = Color.Transparent,
            accent = BrandDeepOrange,
            icon = Icons.Outlined.Description,
            onClick = { navController.navigate(Routes.RESUME_HUB) },
        )
        SectionDivider(modifier = Modifier.padding(vertical = 18.dp))

        BorderlessModuleRow(
            number = "03",
            title = "職場沙盒",
            subtitle = "預習你未來的工作場景",
            tag = null,
            tagColor = Color.Transparent,
            accent = GlowPurple,
            icon = Icons.Outlined.Work,
            onClick = { navController.navigate(Routes.WORKPLACE_SANDBOX) },
        )
        SectionDivider(modifier = Modifier.padding(vertical = 18.dp))

        BorderlessModuleRow(
            number = "04",
            title = "競賽組隊媒合",
            subtitle = "找一起參賽的夥伴",
            tag = null,
            tagColor = Color.Transparent,
            accent = AccentGreen,
            icon = Icons.Outlined.Groups,
            onClick = { navController.navigate(Routes.COMPETITION_LIST) },
        )
    }
}

@Composable
private fun BorderlessModuleRow(
    number: String,
    title: String,
    subtitle: String,
    tag: String?,
    tagColor: Color,
    accent: Color,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(number,
            color = accent.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            modifier = Modifier.width(48.dp))

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title,
                    color = InkBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp)
                if (tag != null) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(tagColor)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(tag,
                            color = InkCharcoal,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = InkGray500,
                style = MaterialTheme.typography.bodyMedium)
        }

        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null,
                tint = accent, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun ArticleSection(navController: NavHostController) {
    val articles = MockData.articles
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        pageCount = { articles.size }
    )

    // 每 5 秒自動往下一篇 fling
    androidx.compose.runtime.LaunchedEffect(pagerState) {
        while (true) {
            kotlinx.coroutines.delay(5000)
            val next = (pagerState.currentPage + 1) % articles.size
            pagerState.animateScrollToPage(
                page = next,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Column {
        // 標題
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("精選")
                    withStyle(SpanStyle(color = BrandOrange)) { append("文章") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f),
            )
            Text("查看全部",
                color = BrandDeepOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "履歷、面試、職涯探索",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(14.dp))

        // 狹長 banner pager
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val article = articles[page]
            ArticleBanner(article) {
                navController.navigate(Routes.articleDetail(article.id))
            }
        }

        Spacer(Modifier.height(12.dp))

        // Dots indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(articles.size) { idx ->
                val isCurrent = pagerState.currentPage == idx
                val width by androidx.compose.animation.core.animateDpAsState(
                    targetValue = if (isCurrent) 18.dp else 6.dp,
                    animationSpec = tween(durationMillis = 400),
                    label = "dot-w"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (isCurrent) BrandDeepOrange
                            else InkGray300
                        )
                )
            }
        }
    }
}

/**
 * Ocard 風狹長 banner:圖滿版,文字疊在底部漸層遮罩
 */
@Composable
private fun ArticleBanner(
    article: Article,
    onClick: () -> Unit,
) {
    val accentColor = when (article.category) {
        ArticleCategory.RESUME -> BrandOrange
        ArticleCategory.INTERVIEW -> BrandDeepOrange
        ArticleCategory.CAREER_EXPLORATION -> GlowPurple
        ArticleCategory.WORKPLACE -> AccentGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.18f))
            .pressScale(onClick = onClick),
    ) {
        // 底圖
        if (article.coverImageUrl.isNotEmpty()) {
            coil.compose.AsyncImage(
                model = article.coverImageUrl,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 底部漸層遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.Transparent,
                            androidx.compose.ui.graphics.Color.Transparent,
                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                            androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.85f),
                        ),
                        startY = 0f,
                    )
                )
        )

        // 上方:分類膠囊 + 閱讀時間
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(14.dp),
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(accentColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(article.category.label,
                    color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(PaperWhite.copy(alpha = 0.92f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${article.readMinutes} min",
                    color = InkBlack,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        }

        // 下方:標題 + 摘要 + 來源
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(article.title,
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(article.source,
                    color = PaperWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                Text("·",
                    color = PaperWhite.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(6.dp))
                Text(article.publishedDate,
                    color = PaperWhite.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * 鈴鐺元件:有未讀時 → 紅點 + 每 8 秒搖晃一次
 */
@Composable
private fun AnimatedBell(
    unreadCount: Int,
    onClick: () -> Unit,
) {
    val hasUnread = unreadCount > 0

    // 搖晃動畫:每 8 秒一次,持續 0.8 秒,左右各晃 1 次
    val infiniteTransition = rememberInfiniteTransition(label = "bell-swing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 8000
                0f at 0
                0f at 100
                -15f at 200
                12f at 350
                -10f at 500
                7f at 650
                -4f at 800
                0f at 950
                0f at 8000
            }
        ),
        label = "rotation"
    )

    Box(
        Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0x33FFFFFF))
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = PaperWhite,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = if (hasUnread) rotation else 0f
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.2f)
                },
        )
        // 紅點(未讀時顯示)
        if (hasUnread) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = 10.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AccentRed)
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFFA63E1F),
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun CompetitionSection(navController: NavHostController) {
    Column {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("推薦")
                    withStyle(SpanStyle(color = AccentGreen)) { append("競賽") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "從興趣 / 系所推薦,組隊找夥伴",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(12.dp))

        val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
        val flingBehavior = androidx.compose.foundation.gestures.snapping
            .rememberSnapFlingBehavior(lazyListState = lazyListState)
        LazyRow(
            state = lazyListState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(MockData.competitions) { competition ->
                CompetitionCard(competition, navController)
            }
        }
    }
}

@Composable
private fun CompetitionCard(competition: com.careersandbox.app.data.model.Competition, navController: NavHostController) {
    val accentColor = when (competition.coverColor) {
        "orange" -> BrandOrange
        "green" -> AccentGreen
        "purple" -> GlowPurple
        "pink" -> Color(0xFFE26B8C)
        "teal" -> Color(0xFF1D9E75)
        else -> BrandOrange
    }
    Column(
        modifier = Modifier
            .width(200.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pressScale { navController.navigate(Routes.competitionDetail(competition.id)) },
    ) {
        // 上半:類別 + 截止日 chip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(accentColor.copy(alpha = 0.14f)),
        ) {
            // 真實圖片(Coil 載入)
            if (competition.coverImageUrl.isNotEmpty()) {
                coil.compose.AsyncImage(
                    model = competition.coverImageUrl,
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // 漸層遮罩
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.4f),
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(competition.category.label,
                    color = PaperWhite,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(PaperWhite.copy(alpha = 0.92f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text("截止 ${competition.deadline.takeLast(5)}",
                    color = InkBlack,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        }

        // 下半:標題 + 主辦 + 獎金 + 隊伍規模
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
        ) {
            Text(competition.title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(competition.organizer,
                color = InkGray500,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(accentColor))
                Spacer(Modifier.width(6.dp))
                Text(competition.prize,
                    color = InkBlack,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(2.dp))
            Text(competition.teamSize,
                color = InkGray500,
                style = MaterialTheme.typography.labelSmall)
        }
    }
}
