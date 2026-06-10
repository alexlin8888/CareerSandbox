package com.careersandbox.app.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Article
import com.careersandbox.app.data.model.ArticleBlock
import com.careersandbox.app.data.model.ArticleCategory
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(navController: NavHostController, articleId: String) {
    val article = MockData.articles.firstOrNull { it.id == articleId } ?: return
    val context = LocalContext.current
    val accent = when (article.category) {
        ArticleCategory.RESUME -> BrandOrange
        ArticleCategory.INTERVIEW -> BrandDeepOrange
        ArticleCategory.CAREER_EXPLORATION -> GlowPurple
        ArticleCategory.WORKPLACE -> AccentGreen
    }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = article.category.label,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        fontSize = 14.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // 封面區:大漸層 + 標題壓在底
            CoverBanner(article, accent)

            // 中段內容
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(28.dp))

                // 來源 + 日期 + 閱讀時間
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        article.source,
                        color = InkBlack,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("·", color = InkGray400)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        article.publishedDate,
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("·", color = InkGray400)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${article.readMinutes} 分鐘",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(Modifier.height(28.dp))

                // 摘要 quote
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(accent.copy(alpha = 0.06f))
                        .padding(20.dp),
                ) {
                    Text(
                        article.excerpt,
                        color = InkGray700,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 內容區塊
                article.bodyContent.forEach { block ->
                    Spacer(Modifier.height(20.dp))
                    when (block) {
                        is ArticleBlock.Heading -> {
                            Text(
                                block.text,
                                color = InkBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                lineHeight = 28.sp,
                            )
                        }
                        is ArticleBlock.Paragraph -> {
                            Text(
                                block.text,
                                color = InkBlack,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 28.sp,
                            )
                        }
                        is ArticleBlock.BulletList -> {
                            Column {
                                block.items.forEach { item ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Box(
                                            Modifier
                                                .padding(top = 10.dp)
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(accent)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            item,
                                            color = InkBlack,
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 26.sp,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }
                        }
                        is ArticleBlock.Quote -> {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(
                                    Modifier
                                        .width(3.dp)
                                        .height(64.dp)
                                        .background(accent)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = "\u201C${block.text}\u201D",
                                    color = InkBlack,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                // 閱讀原文按鈕
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accent)
                        .pressScale {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "閱讀原文",
                            color = PaperWhite,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.OpenInNew,
                            contentDescription = null,
                            tint = PaperWhite,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "本文為摘要與重點整理,完整版請看原文",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun CoverBanner(article: Article, accent: Color) {
    val illustRes = when (article.category) {
        ArticleCategory.RESUME -> com.careersandbox.app.R.drawable.beaver_resume
        ArticleCategory.INTERVIEW -> com.careersandbox.app.R.drawable.beaver_present
        ArticleCategory.CAREER_EXPLORATION -> com.careersandbox.app.R.drawable.beaver_search
        ArticleCategory.WORKPLACE -> com.careersandbox.app.R.drawable.beaver_point
    }

    // 圖片封面 — 真實照片 + 漸層遮罩,文字浮在上面
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(accent),
    ) {
        // 真實照片(Coil)— 載入中或失敗時顯示底色 + unDraw
        if (article.coverImageUrl.isNotEmpty()) {
            coil.compose.AsyncImage(
                model = article.coverImageUrl,
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // 備援:unDraw 插畫
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = illustRes),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                alpha = 0.85f,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .size(140.dp),
            )
        }

        // 漸層遮罩 — 上半透明,下半深色讓文字好讀
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.75f),
                        )
                    )
                )
        )

        // 分類膠囊
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .clip(CircleShape)
                .background(accent)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                article.category.label,
                color = PaperWhite,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
            )
        }

        // 主標
        Text(
            article.title,
            color = PaperWhite,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        )
    }
}
