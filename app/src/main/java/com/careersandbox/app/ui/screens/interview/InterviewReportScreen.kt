package com.careersandbox.app.ui.screens.interview

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun InterviewReportScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(InkCharcoal)) {
        // 光暈
        Box(
            Modifier.fillMaxSize().drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BrandOrange.copy(alpha = 0.5f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.5f, size.height * 0.15f),
                        radius = size.width * 0.9f,
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(size.width * 0.5f, size.height * 0.15f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlowPurple.copy(alpha = 0.3f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.05f, size.height * 0.5f),
                        radius = size.width * 0.7f,
                    ),
                    radius = size.width * 0.7f,
                    center = Offset(size.width * 0.05f, size.height * 0.5f),
                )
            }
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // TopBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                        .pressScale {
                            navController.popBackStack(Routes.INTERVIEW_HUB, false)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null,
                        tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("面試報告",
                    color = PaperWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f))
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                        .pressScale {},
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null,
                        tint = PaperWhite, modifier = Modifier.size(18.dp))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                // 總分 Hero
                StaggeredAppear {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("整體分數",
                                    color = PaperWhite,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp)
                            }
                            Spacer(Modifier.height(20.dp))
                            // 超大數字
                            Text("74",
                                color = PaperWhite,
                                fontSize = 128.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-6).sp,
                                lineHeight = 130.sp)
                            Text("/ 100",
                                color = InkGray400,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 2.sp)
                            Spacer(Modifier.height(20.dp))
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .background(BrandYellow)
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("有基礎,但還能更好",
                                    color = InkCharcoal,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.TrendingUp, contentDescription = null,
                                    tint = AccentGreen, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("比上次進步 6 分",
                                    color = AccentGreen,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // 六項能力
                SectionTitleDark("六項能力")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DimensionRow("內容深度", 78)
                    DimensionRow("邏輯清晰度", 82)
                    DimensionRow("表達流暢度", 71)
                    DimensionRow("互動能力", 68)
                    DimensionRow("應變能力", 64)
                    DimensionRow("自信程度", 80)
                }

                Spacer(Modifier.height(32.dp))

                // 逐題回顧
                SectionTitleDark("逐題回顧")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuestionReview(
                        q = "請你做一個簡短的自我介紹。",
                        a = "你好,我是政大資管系大三的 Alex,過去主要做過社團行銷和資料分析實習,想往產品經理發展。",
                        comment = "有清楚交代背景,但缺乏亮點。可以加 1-2 個具體成就。",
                        better = "我是政大資管大三的 Alex,把社團 IG 從 0 經營到 1200 追蹤,實習用 SQL 把週報效率提升 4 倍,接下來想把這些經驗帶到產品端。",
                    )
                    QuestionReview(
                        q = "可以講一個你覺得做得不太好的決定嗎?",
                        a = "我們曾經辦過一場聯名活動,前期沒有先測試小規模就直接全推,結果觸及只有預期的三成。",
                        comment = "誠實面對失誤是好的,但只講事實沒有反思。STAR 結構缺了 Result 的學習段落。",
                        better = "觸及只有預期三成,我覆盤後發現缺了「先小規模測試」這一步。下次再辦時我先用兩個小貼文測流量,結果觸及達標。",
                    )
                }

                Spacer(Modifier.height(32.dp))

                // 下次可以試試
                SectionTitleDark("下次可以試試")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ImprovementCard("回答前先重複問題一次,確認你聽對了")
                    ImprovementCard("講失敗時用 STAR 結構,結尾一定要有「我從中學到」")
                    ImprovementCard("互動性可以再強,主動問面試官「我這樣理解對嗎」")
                }

                Spacer(Modifier.height(32.dp))
            }

            // 底部按鈕
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f).height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x1AFFFFFF))
                        .pressScale {},
                    contentAlignment = Alignment.Center,
                ) {
                    Text("儲存報告",
                        color = PaperWhite,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium)
                }
                Box(
                    modifier = Modifier
                        .weight(1f).height(56.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp),
                            spotColor = BrandYellow.copy(alpha = 0.6f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandYellow)
                        .pressScale {
                            navController.navigate(Routes.INTERVIEW_HUB) {
                                popUpTo(Routes.INTERVIEW_HUB) { inclusive = false }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("再來一次",
                        color = InkCharcoal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SectionTitleDark(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text,
            color = PaperWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            letterSpacing = (-0.3).sp)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(6.dp).clip(CircleShape).background(BrandYellow))
    }
}

@Composable
private fun DimensionRow(name: String, score: Int) {
    val color = when {
        score >= 80 -> AccentGreen
        score >= 65 -> BrandOrange
        else -> AccentRed
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x14FFFFFF))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(name,
                color = PaperWhite,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f))
            Text("$score",
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                letterSpacing = (-0.5).sp)
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0x22FFFFFF)),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(score / 100f)
                    .clip(RoundedCornerShape(50))
                    .background(SolidColor(color))
            )
        }
    }
}

@Composable
private fun QuestionReview(q: String, a: String, comment: String, better: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x14FFFFFF))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(28.dp).clip(CircleShape)
                    .background(BrandYellow),
                contentAlignment = Alignment.Center,
            ) {
                Text("Q", color = InkCharcoal,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp)
            }
            Spacer(Modifier.width(10.dp))
            Text(q,
                color = PaperWhite,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Text("你的回答",
            color = InkGray400,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(a, color = PaperWhite, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(14.dp))
        // AI 點評
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BrandOrange.copy(alpha = 0.2f))
                .padding(12.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Comment, contentDescription = null,
                        tint = BrandYellow, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("AI 點評",
                        color = BrandYellow,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                Text(comment, color = PaperWhite,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(8.dp))
        // 改寫版
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x22FFFFFF))
                .padding(12.dp),
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoAwesome, contentDescription = null,
                        tint = PaperWhite, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("再強一點的版本",
                        color = PaperWhite,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(6.dp))
                Text(better, color = PaperWhite.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ImprovementCard(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x14FFFFFF))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape)
                .background(BrandYellow),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Lightbulb, contentDescription = null,
                tint = InkCharcoal, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(text,
            color = PaperWhite,
            style = MaterialTheme.typography.bodyMedium)
    }
}
