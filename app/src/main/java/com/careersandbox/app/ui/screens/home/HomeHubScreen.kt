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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        // 內容區捲動,Hero 在最上方
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            HeroSection()
            Spacer(Modifier.height(8.dp))
            QuickActionsBorderless(navController)
            Spacer(Modifier.height(36.dp))
            ModuleSection(navController)
            Spacer(Modifier.height(36.dp))
            ArticleSection(navController)
            Spacer(Modifier.height(36.dp))
            NotificationsBorderless(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

/** Hero 區:wave 漸層 + 大字 + 插畫 + **僅在此區的線稿裝飾** */
@Composable
private fun HeroSection() {
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
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .pressScale {},
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.NotificationsNone, contentDescription = null,
                        tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(4.dp))
            // 姓名 + 年級
            Row(verticalAlignment = Alignment.Bottom) {
                Text(MockData.currentUser.name,
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 40.sp,
                    lineHeight = 44.sp)
                Spacer(Modifier.width(8.dp))
                Text("大三",
                    color = BrandYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp))
            }

            Spacer(Modifier.height(20.dp))

            // 修 bug 1:拆成兩個 Text,各自獨立 line height
            // 縮窄寬度,避開右下插畫位置
            Column(modifier = Modifier.fillMaxWidth(0.62f)) {
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
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "履歷",
                        color = PaperWhite.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 14.dp),
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
        BorderlessIconAction("寫履歷", Icons.Outlined.Description, GlowPurple) {
            navController.navigate(Routes.RESUME_EDITOR)
        }
        BorderlessIconAction("找職缺", Icons.Outlined.Search, AccentGreen) {
            navController.navigate(Routes.EXPLORE_HUB)
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
            onClick = { navController.navigate(Routes.EXPLORE_HUB) },
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
            onClick = { navController.navigate(Routes.EXPLORE_HUB) },
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

@Composable
private fun ArticleSection(navController: NavHostController) {
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
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "履歷、面試、職涯探索 — 邊看邊學",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(16.dp))

        // 橫向滾動文章卡
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(MockData.articles) { article ->
                ArticleCard(article) {
                    navController.navigate(Routes.articleDetail(article.id))
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(
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
            .width(260.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .pressScale(onClick = onClick)
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 分類標
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(accentColor)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(article.category.label,
                        color = PaperWhite,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.weight(1f))
                Text("${article.readMinutes} min",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            // 標題
            Text(article.title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            // 摘要
            Text(article.excerpt,
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp)
            Spacer(Modifier.weight(1f))
            // 來源
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(accentColor))
                Spacer(Modifier.width(6.dp))
                Text(article.source,
                    color = InkBlack,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Text("·",
                    color = InkGray400,
                    style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(6.dp))
                Text(article.publishedDate,
                    color = InkGray500,
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun NotificationsBorderless(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("最近的提醒",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f))
            Text("全部",
                color = BrandOrange,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.pressScale {
                    navController.navigate(Routes.NOTIFICATIONS_ALL)
                })
        }
        Spacer(Modifier.height(16.dp))
        MockData.notifications.take(2).forEachIndexed { idx, n ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(8.dp).clip(CircleShape).background(BrandOrange)
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(n.title,
                        color = InkBlack,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold)
                    Text(n.body,
                        color = InkGray500,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1)
                }
                Text(n.time, color = InkGray400,
                    style = MaterialTheme.typography.labelSmall)
            }
            if (idx < 1) {
                SectionDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
                                                                                                                                                                                                                                                                                          