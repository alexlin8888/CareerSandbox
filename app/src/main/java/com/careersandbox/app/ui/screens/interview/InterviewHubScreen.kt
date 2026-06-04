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
            StaggeredAppear(delayMillis = 0) { HeroSection() }

            Spacer(Modifier.height(20.dp))

            // === #6 Avatar 成長卡 ===
            StaggeredAppear(delayMillis = 90) { AvatarGrowthCard() }

            Spacer(Modifier.height(20.dp))

            // === #1 快速練習(低門檻入口,與正式 mock 區分)===
            StaggeredAppear(delayMillis = 170) { QuickPracticeCard(navController) }

            Spacer(Modifier.height(32.dp))

            // === 兩個方案卡(都帶插畫)===
            StaggeredAppear(delayMillis = 250) { PlanCards(navController) }

            Spacer(Modifier.height(24.dp))

            // === #7 multi-agent 面試官 panel(殼 / 預告)===
            StaggeredAppear(delayMillis = 330) { MultiAgentTeaser() }

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
            onClick = { navController.navigate(Routes.INTERVIEW_SETUP_GROUP) },
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

/* ===================== #6 Avatar 成長卡 ===================== */

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun AvatarGrowthCard() {
    val abilities = listOf(
        Triple("內容深度", 78, 4),
        Triple("邏輯清晰", 82, 2),
        Triple("表達流暢", 71, 6),
        Triple("互動", 68, 3),
        Triple("應變", 64, 1),
        Triple("自信", 80, 5),
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(BrandPeach.copy(alpha = 0.55f), PaperWhite)))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(72.dp).clip(CircleShape).background(PaperWhite),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.beaver_flex),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(BrandDeepOrange).padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text("Lv.4", color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("面試新星", color = BrandDeepOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "面試力 ",
                        color = InkGray700, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    Text("${rememberCountUp(74)}", color = InkBlack, fontSize = 30.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Color(0x33D84315)),
                ) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(rememberProgressFill(0.74f)).clip(RoundedCornerShape(50)).background(BrandDeepOrange))
                }
                Spacer(Modifier.height(3.dp))
                Text("距 Lv.5 還差 26 XP", color = InkGray500, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x22D84315)))
        Spacer(Modifier.height(12.dp))
        Text("六項能力(每練一次會成長)", color = InkGray700, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            abilities.forEach { (label, value, delta) ->
                AbilityChip(label, value, delta)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "上次練習後 +6 分 — 再練一場會更高。",
            color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AbilityChip(label: String, value: Int, delta: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PaperWhite)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = InkGray700, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(5.dp))
        Text("${rememberCountUp(value)}", color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.Black)
        if (delta > 0) {
            Spacer(Modifier.width(4.dp))
            Text("↑$delta", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/* ===================== #1 快速練習(低門檻入口)===================== */

@Composable
private fun QuickPracticeCard(navController: NavHostController) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(BrandYellow.copy(alpha = 0.4f), BrandPeach.copy(alpha = 0.5f))))
            .pressScale {
                navController.navigate(Routes.INTERVIEW_LIVE_INDIVIDUAL) {
                    popUpTo(Routes.INTERVIEW_HUB)
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(PaperWhite),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Timer, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("快速練習", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(PaperWhite).padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text("約 5 分鐘", color = BrandDeepOrange, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                "1-3 題 · 不用設定 · 低壓力暖身,隨時來一場",
                color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp,
            )
        }
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = BrandDeepOrange)
    }
}

/* ===================== #7 multi-agent 面試官 panel(殼)===================== */

@Composable
private fun MultiAgentTeaser() {
    val panel = listOf(
        Triple("HR 主管", "人格特質 · 團隊適配", "你遇過最棘手的團隊衝突,後來怎麼收的?"),
        Triple("技術主管", "專業深度", "剛剛那個專案,為什麼選這個架構而不是別的?"),
        Triple("用人主管", "實戰與成果", "如果這塊交給你,前三個月你會先動哪裡?"),
    )
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF374151))))
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(BrandYellow).padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("即將推出", color = InkCharcoal, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(8.dp))
                Text("最貼近真實 panel 面試", color = PaperWhite.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            Text("多人面試官 panel", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "多位 AI 面試官同時在場,輪流從不同角度追問。實際會像這樣:",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 19.sp,
            )
            Spacer(Modifier.height(16.dp))
            panel.forEachIndexed { i, (role, angle, q) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        Modifier.size(28.dp).clip(CircleShape).background(BrandYellow),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${i + 1}", color = InkCharcoal, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(role, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(angle, color = PaperWhite.copy(alpha = 0.55f), fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PaperWhite.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Text(
                                "「$q」",
                                color = PaperWhite.copy(alpha = 0.92f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(BrandYellow.copy(alpha = 0.18f)).padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("開發中 — 這是團隊投入設計的差異化重點", color = BrandYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
