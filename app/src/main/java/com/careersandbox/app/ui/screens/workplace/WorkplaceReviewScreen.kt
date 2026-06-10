package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.components.rememberProgressFill
import com.careersandbox.app.ui.theme.*

private data class HiddenStat(
    val label: String,
    val value: Int,        // 0-100
    val delta: Int,
    val note: String,
    val color: Color,
)

private data class MomentCard(
    val title: String,
    val body: String,
    val good: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkplaceReviewScreen(navController: NavHostController) {
    val stats = listOf(
        HiddenStat("主管信任", 62, +8, "誠實回報那次加的", AccentGreen),
        HiddenStat("同事關係", 55, +3, "回了林經理那封信", AccentBlue),
        HiddenStat("你的電量", 41, -19, "第一週,正常", BrandAmber),
    )
    val moments = listOf(
        MomentCard(
            "沒找藉口的那次",
            "週一的 1on1,Ken 問你為什麼他最後一個知道。你沒有繞。他在筆記上寫了一行字——主管信任的 +8,是這樣來的。",
            good = true,
        ),
        MomentCard(
            "漏掉的那封信",
            "週三的信箱風暴,你在 90 秒裡拆了 9 封。漏掉的其中一封,週四自己找上門。有些代價會遲到,但不會缺席。",
            good = false,
        ),
        MomentCard(
            "你說了 3 次「好」",
            "其中 1 次,你其實想說不。下週,試著把那個「不」說出來一次就好。",
            good = false,
        ),
    )

    Scaffold(
        containerColor = InkCharcoal,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkCharcoal),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StaggeredAppear(delayMillis = 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("週五 18:05",
                        color = PaperWhite.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("第一週結束",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("你撐過來了。來看這週留下的痕跡。",
                        color = PaperWhite.copy(alpha = 0.7f),
                        fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(26.dp))

            // === 隱性數值揭露 ===
            StaggeredAppear(delayMillis = 250) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(PaperWhite.copy(alpha = 0.06f))
                        .padding(18.dp),
                ) {
                    Text("這週,有三個數字一直在動。你看不到,但它們都記得。",
                        color = PaperWhite.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    stats.forEachIndexed { i, st ->
                        HiddenStatRow(st)
                        if (i != stats.lastIndex) Spacer(Modifier.height(14.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // === 時刻卡:哪句話造成的 ===
            moments.forEachIndexed { i, m ->
                StaggeredAppear(delayMillis = 550 + i * 250) {
                    ReviewMomentCard(m)
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(10.dp))

            // === 雙輸誠實收尾 ===
            StaggeredAppear(delayMillis = 1400) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("有些題沒有好答案。你選了比較不爛的那個——這就是上班。",
                        color = PaperWhite.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        lineHeight = 19.sp)
                    Spacer(Modifier.height(18.dp))
                    Image(
                        painter = painterResource(R.drawable.beaver_sleep),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(104.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("電量 41%。週末拿去充。",
                        color = PaperWhite.copy(alpha = 0.55f),
                        fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            StaggeredAppear(delayMillis = 1650) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandOrange)
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("下週一見",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(36.dp))
        }
    }
}

@Composable
private fun HiddenStatRow(st: HiddenStat) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(st.label,
                color = PaperWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f))
            Text("${st.value}",
                color = st.color,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp)
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(st.color.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(if (st.delta >= 0) "+${st.delta}" else "${st.delta}",
                    color = st.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(PaperWhite.copy(alpha = 0.12f)),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(rememberProgressFill(st.value / 100f))
                    .clip(RoundedCornerShape(50))
                    .background(st.color),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(st.note,
            color = PaperWhite.copy(alpha = 0.45f),
            fontSize = 11.sp)
    }
}

@Composable
private fun ReviewMomentCard(m: MomentCard) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PaperWhite.copy(alpha = 0.07f))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(8.dp).clip(CircleShape)
                    .background(if (m.good) AccentGreen else BrandAmber),
            )
            Spacer(Modifier.width(8.dp))
            Text(m.title,
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(m.body,
            color = PaperWhite.copy(alpha = 0.85f),
            fontSize = 13.sp,
            lineHeight = 20.sp)
    }
}
