package com.careersandbox.app.ui.screens.workplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.careersandbox.app.data.mock.WorkplaceState
import com.careersandbox.app.navigation.Routes
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
    fun netFor(meter: String): Int = WorkplaceState.log.filter { it.meter == meter }.sumOf { it.delta }
    fun reasonFor(meter: String): String =
        WorkplaceState.log.lastOrNull { it.meter == meter }?.reason ?: "這週沒什麼波動"
    val stats = listOf(
        HiddenStat("主管信任", WorkplaceState.managerTrust.value * 10, netFor("主管信任"), reasonFor("主管信任"), AccentGreen),
        HiddenStat("同事情誼", WorkplaceState.peerBond.value * 10, netFor("同事情誼"), reasonFor("同事情誼"), AccentBlue),
        HiddenStat("專業形象", WorkplaceState.proImage.value * 10, netFor("專業形象"), reasonFor("專業形象"), BrandAmber),
    )
    val mtV = WorkplaceState.managerTrust.value
    val pbV = WorkplaceState.peerBond.value
    val prV = WorkplaceState.proImage.value
    fun cap(v: Float): Int = (v * 10f).toInt().coerceIn(0, 100)
    val radarAxes = listOf(
        RadarAxis("向上溝通", cap(mtV.toFloat()), 78),
        RadarAxis("跨部門協作", cap(pbV.toFloat()), 75),
        RadarAxis("專業判斷", cap(prV.toFloat()), 82),
        RadarAxis("抗壓應變", cap((mtV + pbV + prV) / 3f), 70),
        RadarAxis("主動當責", cap(prV * 0.6f + mtV * 0.4f), 80),
        RadarAxis("自我覺察", cap(prV * 0.7f + pbV * 0.3f), 68),
    )
    val radarTopGap = radarAxes.maxByOrNull { it.target - it.current }
    val moments = listOf(
        MomentCard(
            "沒找藉口的那次",
            "週一的 1on1,Ken 問你為什麼他最後一個知道。你沒有繞。他在筆記上寫了一行字。主管信任 +8,是這樣來的。",
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
                    Spacer(Modifier.height(14.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(BrandOrange.copy(alpha = 0.18f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text("這週的你:" + WorkplaceState.persona(),
                            color = BrandAmber, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
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

            // === 能力輪廓雷達(差集:現有 vs 角色目標)===
            StaggeredAppear(delayMillis = 400) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(PaperWhite.copy(alpha = 0.06f))
                        .padding(18.dp),
                ) {
                    Text("這週,也悄悄畫出了你的能力輪廓。",
                        color = PaperWhite.copy(alpha = 0.85f),
                        fontSize = 13.sp, lineHeight = 20.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("橘色是現在的你,虛線是這個位子期待的樣子。凹進去的,是下週可以長的地方。",
                        color = PaperWhite.copy(alpha = 0.5f),
                        fontSize = 11.sp, lineHeight = 17.sp)
                    WeekCapabilityRadar(axes = radarAxes, animate = true)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(BrandDeepOrange))
                        Spacer(Modifier.width(6.dp))
                        Text("現在的你", color = PaperWhite.copy(alpha = 0.7f), fontSize = 11.sp)
                        Spacer(Modifier.width(16.dp))
                        Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(PaperWhite.copy(alpha = 0.45f)))
                        Spacer(Modifier.width(6.dp))
                        Text("位子的期待", color = PaperWhite.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    radarTopGap?.let { g ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandAmber.copy(alpha = 0.14f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("↑", color = BrandAmber, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.width(10.dp))
                            Text("下週最值得長的:" + g.label + "(離期待還差 " + (g.target - g.current) + ")",
                                color = PaperWhite.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // === 後果回收:午餐接住阿凱 → 週五的回禮(只在當天接住才出現)===
            if (WorkplaceState.hasFlag("lunch_bonded_akai")) {
                StaggeredAppear(delayMillis = 480) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(BrandPeach.copy(alpha = 0.14f))
                            .padding(18.dp),
                    ) {
                        Text("後續", color = BrandDeepOrange, fontSize = 11.sp,
                            fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "午餐那天你接住了阿凱的話。週五下班前,他晃到你桌邊,丟下一句:下週有個小案子,算你一個。",
                            color = PaperWhite.copy(alpha = 0.85f), fontSize = 13.sp, lineHeight = 20.sp,
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

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
                    Text("有些題沒有好答案。你選了比較不爛的那個。這就是上班。",
                        color = PaperWhite.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        lineHeight = 19.sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Ken:" + kenVerdict(
                            WorkplaceState.managerTrust.value,
                            WorkplaceState.peerBond.value,
                            WorkplaceState.proImage.value,
                        ),
                        color = PaperWhite.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                    )
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
                        .pressScale { navController.navigate(Routes.NIGHT_INTERLUDE_5) },
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

private data class RadarAxis(val label: String, val current: Int, val target: Int)

@Composable
private fun WeekCapabilityRadar(axes: List<RadarAxis>, animate: Boolean) {
    val n = axes.size
    val anim by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "weekradar",
    )
    Box(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.minDimension / 2f * 0.74f
            fun axisAngle(i: Int): Double = -Math.PI / 2 + 2 * Math.PI * i / n
            fun point(i: Int, r: Float): Offset {
                val a = axisAngle(i)
                return Offset(cx + (r * kotlin.math.cos(a)).toFloat(), cy + (r * kotlin.math.sin(a)).toFloat())
            }
            for (ring in 1..4) {
                val rr = radius * ring / 4f
                val path = androidx.compose.ui.graphics.Path()
                for (i in 0 until n) {
                    val p = point(i, rr)
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                path.close()
                drawPath(path, color = PaperWhite.copy(alpha = 0.10f), style = Stroke(width = 1f))
            }
            for (i in 0 until n) {
                drawLine(PaperWhite.copy(alpha = 0.10f), start = Offset(cx, cy), end = point(i, radius), strokeWidth = 1f)
            }
            val targetPath = androidx.compose.ui.graphics.Path()
            for (i in 0 until n) {
                val p = point(i, radius * (axes[i].target / 100f))
                if (i == 0) targetPath.moveTo(p.x, p.y) else targetPath.lineTo(p.x, p.y)
            }
            targetPath.close()
            drawPath(targetPath, color = PaperWhite.copy(alpha = 0.45f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))))
            val curPath = androidx.compose.ui.graphics.Path()
            for (i in 0 until n) {
                val p = point(i, radius * (axes[i].current / 100f) * anim)
                if (i == 0) curPath.moveTo(p.x, p.y) else curPath.lineTo(p.x, p.y)
            }
            curPath.close()
            drawPath(curPath, color = BrandOrange.copy(alpha = 0.30f))
            drawPath(curPath, color = BrandDeepOrange, style = Stroke(width = 2f))
            for (i in 0 until n) {
                drawCircle(BrandDeepOrange, radius = 3f, center = point(i, radius * (axes[i].current / 100f) * anim))
            }
        }
        axes.forEachIndexed { i, ax ->
            val a = -Math.PI / 2 + 2 * Math.PI * i / n
            val dx = (kotlin.math.cos(a) * 112).toFloat()
            val dy = (kotlin.math.sin(a) * 112).toFloat()
            val gap = ax.target - ax.current
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(x = dx.dp, y = dy.dp),
            ) {
                Text(ax.label,
                    color = if (gap >= 25) BrandAmber else PaperWhite.copy(alpha = 0.85f),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("" + ax.current,
                    color = if (gap >= 25) BrandAmber else PaperWhite.copy(alpha = 0.5f),
                    fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

private fun kenVerdict(trust: Int, bond: Int, pro: Int): String = when {
    trust >= 6 && pro >= 6 -> "「這週你扛得住事,也敢做決定。下週給你帶個小東西試試。」"
    trust >= 6 -> "「我開始信得過你。把這份穩,帶到下週。」"
    bond >= 6 && trust <= 3 -> "「同事很挺你。但有些決定,還是得你自己拍板。」"
    pro <= 2 || trust <= 2 -> "「第一週本來就難。記得,事情是人一起做的,別自己硬扛。」"
    else -> "「穩穩的。下週,讓我看到你更主動一點。」"
}
