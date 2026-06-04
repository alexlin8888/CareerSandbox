package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/**
 * #7 multi-agent 面試模擬 — 設計預覽(開發中)。
 * 對話為示範腳本;真實版本由後端多個 AI agent 即時依回答提問。
 */
@Composable
fun MultiAgentPreviewScreen(navController: NavHostController) {
    val interviewers = listOf(
        Triple("HR 主管", "人格特質 · 團隊適配", BrandYellow),
        Triple("技術主管", "專業深度 · 解決問題", AccentBlue),
        Triple("用人主管", "實戰經驗 · 成果", AccentGreen),
    )
    // (speaker, line, isYou)
    val transcript = listOf(
        Triple("HR 主管", "你遇過最棘手的團隊衝突,後來怎麼收的?", false),
        Triple("你", "上個專案我和設計師對需求認知不同,我先約了一次一對一,把彼此的目標講清楚……", true),
        Triple("技術主管", "你說「把目標講清楚」——具體你用什麼方式對齊?有留下文件嗎?", false),
        Triple("你", "我把討論結論寫成一頁 PRD,標出每個決定的理由,後續有爭議就回去看那份。", true),
        Triple("用人主管", "如果這塊交給你,前三個月你會先動哪裡?", false),
    )

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // === 深色 Hero ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF1F2937), Color(0xFF374151))))
                    .padding(20.dp),
            ) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PaperWhite.copy(alpha = 0.15f))
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.padding(top = 56.dp)) {
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(BrandYellow).padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text("開發中 · 設計預覽", color = InkCharcoal, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("多人面試官 Panel", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "多位 AI 面試官同時面你,從不同角度輪流追問 — 比單一面試官更接近真實的關主面試。",
                        color = PaperWhite.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp,
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                // === 三位面試官 ===
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.beaver_point),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("這場有三位面試官", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                Spacer(Modifier.height(14.dp))
                interviewers.forEachIndexed { i, (role, focus, accent) ->
                    StaggeredAppear(delayMillis = i * 90) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PaperWhite)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier.size(40.dp).clip(CircleShape).background(accent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("${i + 1}", color = accent, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(role, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.height(2.dp))
                                Text(focus, color = InkGray500, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // === 預覽對話 ===
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("預覽:一段 panel 對話", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(50)).background(InkGray100).padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text("示範腳本", color = InkGray500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(14.dp))
                transcript.forEach { (speaker, line, isYou) ->
                    if (isYou) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BrandPeach.copy(alpha = 0.5f))
                                    .padding(12.dp),
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("你", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    Spacer(Modifier.height(3.dp))
                                    Text(line, color = InkBlack, fontSize = 13.sp, lineHeight = 18.sp)
                                }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PaperWhite)
                                    .padding(12.dp),
                            ) {
                                Column {
                                    Text(speaker, color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    Spacer(Modifier.height(3.dp))
                                    Text(line, color = InkBlack, fontSize = 13.sp, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandYellow.copy(alpha = 0.16f))
                        .padding(14.dp),
                ) {
                    Column {
                        Text("注意第 3 句", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "技術主管接著 HR 的話題追問 — 多位面試官會互相接話、針對你的回答深挖,這是單一面試官做不到的。",
                            color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // === 為什麼這樣設計 ===
                Text("為什麼這樣設計", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.height(12.dp))
                listOf(
                    "不同角色不同盲點" to "HR、技術、用人主管在意的點不同,逼你全面準備,而不是只討好一個人。",
                    "會互相接話追問" to "面試官根據你前一段回答即時深挖,壓力與真實 panel 面試一致。",
                    "回饋更立體" to "面試後能看到每位面試官各自的評價,而不是單一視角的分數。",
                ).forEach { (title, body) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Box(Modifier.padding(top = 6.dp).size(7.dp).clip(CircleShape).background(BrandOrange))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(title, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(body, color = InkGray500, fontSize = 12.sp, lineHeight = 17.sp)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(InkGray100)
                        .padding(14.dp),
                ) {
                    Text(
                        "這是設計預覽,對話為示範腳本。實際上線後,面試官會由多個 AI agent 即時依你的回答提問與追問。",
                        color = InkGray500, fontSize = 12.sp, lineHeight = 18.sp,
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
