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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun InterviewReportScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    val onShare: () -> Unit = {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, "我在 CareerSandbox 完成了一場模擬面試,整體 74 分,還在持續練習中。")
        }
        ctx.startActivity(android.content.Intent.createChooser(intent, "分享面試報告"))
    }
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
                        .pressScale(onClick = onShare),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "分享報告",
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
                            Text("${rememberCountUp(74, durationMillis = 1200)}",
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

                // === #3 三大面向 dashboard(內容 / 結構 / 表達)===
                SectionTitleDark("三大面向")
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FaceCard(
                        letter = "內", name = "內容", score = 71,
                        verdict = "有講到重點,但缺乏具體數字與亮點。",
                        points = listOf(
                            "自我介紹加 1-2 個量化成就(追蹤數、效率提升倍數)",
                            "回答時多舉一個具體例子,少用空泛形容詞",
                        ),
                    )
                    FaceCard(
                        letter = "構", name = "結構", score = 82,
                        verdict = "邏輯清楚,但講失敗經歷時 STAR 的 Result 段常缺。",
                        points = listOf(
                            "講經歷一律用 STAR:情境 → 任務 → 行動 → 結果",
                            "結尾補一句「我從中學到什麼」",
                        ),
                    )
                    FaceCard(
                        letter = "達", name = "表達", score = 70,
                        verdict = "整體流暢,但語速偏快、語調起伏不足。",
                        points = listOf(
                            "放慢語速,重點句講完停半秒",
                            "用語調強調關鍵字,不要從頭平到尾",
                        ),
                        prosody = listOf(
                            "語速" to "220 字/分 · 偏快",
                            "停頓" to "偏少 · 句子間幾乎不停",
                            "語調" to "起伏不足 · 偏平",
                            "填充詞" to "「嗯 / 那個」8 次",
                        ),
                    )
                }

                Spacer(Modifier.height(32.dp))

                // 細項分數
                SectionTitleDark("細項分數")
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
                        a = "你好,我是中山資管系大三的 Alex,過去主要做過社團行銷和資料分析實習,想往產品經理發展。",
                        comment = "有清楚交代背景,但缺乏亮點。可以加 1-2 個具體成就。",
                        better = "我是中山資管大三的 Alex,把社團 IG 從 0 經營到 1200 追蹤,實習用 SQL 把週報效率提升 4 倍,接下來想把這些經驗帶到產品端。",
                    )
                    QuestionReview(
                        q = "可以講一個你覺得做得不太好的決定嗎?",
                        a = "我們曾經辦過一場聯名活動,前期沒有先測試小規模就直接全推,結果觸及只有預期的三成。",
                        comment = "誠實面對失誤是好的,但只講事實沒有反思。STAR 結構缺了 Result 的學習段落。",
                        better = "觸及只有預期三成,我覆盤後發現缺了「先小規模測試」這一步。下次再辦時我先用兩個小貼文測流量,結果觸及達標。",
                    )
                }

                Spacer(Modifier.height(32.dp))

                // === #5 該提、但沒提到 ===
                SectionTitleDark("該提、但你沒提到")
                Spacer(Modifier.height(12.dp))
                OmissionSection()

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

            // 收尾鼓勵(誠實語氣,呼應 74 分「有基礎但還能更好」)
            StaggeredAppear {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x14FFFFFF))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.beaver_thumbsup),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(84.dp),
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "有基礎了,別停在這",
                            color = PaperWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "把語速放慢一點、再來一次時把漏掉的經歷補上,分數會更穩。",
                            color = InkGray300,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

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
                        .pressScale {
                            android.widget.Toast.makeText(
                                ctx, "報告已儲存到「面試紀錄」(示範)", android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        },
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
            Text("${rememberCountUp(score)}",
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
                    .fillMaxWidth(rememberProgressFill(score / 100f))
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

/* ===================== #3 / #4 三大面向(含語音語調)===================== */

@Composable
private fun FaceCard(
    letter: String,
    name: String,
    score: Int,
    verdict: String,
    points: List<String>,
    prosody: List<Pair<String, String>>? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val scoreColor = when {
        score >= 80 -> AccentGreen
        score >= 65 -> BrandOrange
        else -> AccentRed
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x14FFFFFF))
            .pressScale { expanded = !expanded }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(BrandYellow),
                contentAlignment = Alignment.Center,
            ) {
                Text(letter, color = InkCharcoal, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(name, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Text("${rememberCountUp(score)}", color = scoreColor, fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = (-0.5).sp)
            Spacer(Modifier.width(6.dp))
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray400, modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Color(0x22FFFFFF)),
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(rememberProgressFill(score / 100f)).clip(RoundedCornerShape(50)).background(SolidColor(scoreColor)),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(verdict, color = PaperWhite.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)

        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Text("可以怎麼做", color = BrandYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            points.forEach { p ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("·", color = BrandYellow, fontSize = 13.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(8.dp))
                    Text(p, color = PaperWhite.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
            if (prosody != null) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Mic, contentDescription = null, tint = BrandYellow, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("語音語調分析", color = BrandYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 6.dp, vertical = 1.dp),
                        ) {
                            Text("beta", color = InkGray400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    prosody.forEach { (k, v) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(k, color = InkGray400, fontSize = 12.sp)
                            Text(v, color = PaperWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "語音分析需開啟錄音,接上後會用你的實際音檔分析。",
                        color = InkGray400, fontSize = 10.sp, lineHeight = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun OmissionSection() {
    data class Miss(val point: String, val why: String)
    val misses = listOf(
        Miss("SQL 把週報效率提升 4 倍", "這份 JD 很重視數據能力,但你整場沒提到這段量化成果。"),
        Miss("社團 IG 從 0 經營到 1200 追蹤", "能證明你的成長行銷實作,面試官通常很買單。"),
        Miss("帶 5 人團隊完成聯名專案", "JD 要求協作與領導,這段沒帶到很可惜。"),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x14FFFFFF))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "根據這份 JD 和你的履歷,這些是你有、卻整場沒講到的加分點。",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(Color(0x33FFFFFF)).padding(horizontal = 7.dp, vertical = 2.dp),
            ) {
                Text("依 JD×履歷", color = InkGray400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(14.dp))
        misses.forEachIndexed { idx, m ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Box(
                    Modifier.size(22.dp).clip(CircleShape).background(BrandOrange.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("+", color = BrandYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(m.point, color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(m.why, color = InkGray400, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
            if (idx < misses.size - 1) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x14FFFFFF)))
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x22FFFFFF)))
        Spacer(Modifier.height(10.dp))
        Text(
            "面試教練的重點:不只看你講了什麼,更看你「該講卻沒講」的。",
            color = BrandYellow, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp,
        )
    }
}
