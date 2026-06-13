package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   學習路徑 —— 看板流(想學 / 進行中 / 已掌握)
   刻意不用 Duolingo 蜿蜒路徑(沙盒已用),改成自己推動的看板:
   把缺口技能一張一張從「想學」推到「進行中」,補完推到「已掌握」
   ===================================================================== */

private enum class LearnStage { TODO, DOING, DONE }

private data class LearnSkill(
    val id: String,
    val name: String,
    val why: String,           // 為什麼這職缺要它
    val resource: String,      // 學習資源建議
    val estimate: String,      // 預估時間
    val stage: LearnStage,
)

// mock:接「技能差距」常見缺口
private val seedSkills = listOf(
    LearnSkill("sql", "SQL", "幾乎每個資料職缺都要你自己撈資料", "Mode SQL Tutorial · 互動式", "2-3 週", LearnStage.DONE),
    LearnSkill("excel", "Excel 樞紐分析", "面試常要你當場拆一份報表", "Microsoft 官方教學", "1 週", LearnStage.DONE),
    LearnSkill("python", "Python 資料清理", "JD 寫「需自行清理資料」", "Kaggle Pandas · 免費", "3-4 週", LearnStage.DOING),
    LearnSkill("viz", "資料視覺化", "把分析做成看得懂的圖", "Tableau Public · 免費", "2 週", LearnStage.DOING),
    LearnSkill("abtest", "A/B 測試", "JD 提到「追蹤活動成效」", "Udacity A/B Testing", "2 週", LearnStage.TODO),
    LearnSkill("ga4", "GA4", "電商職缺幾乎都要", "Google Skillshop · 免費", "1-2 週", LearnStage.TODO),
    LearnSkill("stats", "基礎統計", "看懂顯著性,面試會問", "Khan Academy · 免費", "3 週", LearnStage.TODO),
    LearnSkill("storytell", "數據說故事", "讓報告有人看得進去", "Storytelling with Data", "持續", LearnStage.TODO),
)

@Composable
fun LearningPathScreen(navController: NavHostController) {
    val skills = remember { mutableStateListOf(*seedSkills.toTypedArray()) }

    val total = skills.size
    val done = skills.count { it.stage == LearnStage.DONE }

    fun advance(id: String) {
        val i = skills.indexOfFirst { it.id == id }
        if (i < 0) return
        val s = skills[i]
        val next = when (s.stage) {
            LearnStage.TODO -> LearnStage.DOING
            LearnStage.DOING -> LearnStage.DONE
            LearnStage.DONE -> LearnStage.DOING   // 點完成的可退回進行中
        }
        skills[i] = s.copy(stage = next)
    }

    Column(
        Modifier.fillMaxSize().background(PaperWarm).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(38.dp).clip(CircleShape).background(InkGray100)
                    .pressScale { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack, modifier = Modifier.size(18.dp)) }
            Spacer(Modifier.width(12.dp))
            Text("學習路徑", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }
        Spacer(Modifier.height(16.dp))

        // 進度頭
        ProgressHeader(done = done, total = total)
        Spacer(Modifier.height(8.dp))
        Text(
            "把還缺的技能,從「想學」推到「進行中」,補完推到「已掌握」。一步一步來。",
            color = InkGray500, fontSize = 12.sp, lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(18.dp))

        // 三欄
        StageColumn("進行中", skills.filter { it.stage == LearnStage.DOING }, BrandOrange, ::advance)
        Spacer(Modifier.height(14.dp))
        StageColumn("想學", skills.filter { it.stage == LearnStage.TODO }, InkGray400, ::advance)
        Spacer(Modifier.height(14.dp))
        StageColumn("已掌握", skills.filter { it.stage == LearnStage.DONE }, AccentGreen, ::advance)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProgressHeader(done: Int, total: Int) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val animDone by animateIntAsState(
        targetValue = if (appear) done else 0,
        animationSpec = tween(900, easing = FastOutSlowInEasing), label = "ldDone",
    )
    val prog by animateFloatAsState(
        targetValue = if (appear && total > 0) done.toFloat() / total else 0f,
        animationSpec = tween(1000, delayMillis = 120, easing = FastOutSlowInEasing), label = "ldProg",
    )
    Column(
        Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)).background(InkBlack).padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("已掌握", color = PaperWhite.copy(alpha = 0.55f), fontSize = 11.sp,
                fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 5.dp))
            Spacer(Modifier.width(8.dp))
            Text("$animDone", color = BrandAmber, fontWeight = FontWeight.Black, fontSize = 36.sp, lineHeight = 36.sp)
            Text(" / $total 項技能", color = PaperWhite.copy(alpha = 0.5f), fontSize = 14.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50))
                .background(PaperWhite.copy(alpha = 0.12f)),
        ) {
            Box(
                Modifier.fillMaxWidth(prog.coerceAtLeast(0.02f)).fillMaxHeight()
                    .clip(RoundedCornerShape(50)).background(BrandOrange),
            )
        }
    }
}

@Composable
private fun StageColumn(
    title: String,
    items: List<LearnSkill>,
    accent: Color,
    onAdvance: (String) -> Unit,
) {
    Column(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(8.dp))
            Text(title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 15.sp)
            Spacer(Modifier.width(6.dp))
            Text("${items.size}", color = InkGray400, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        if (items.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(InkGray100.copy(alpha = 0.5f)).padding(vertical = 18.dp),
                contentAlignment = Alignment.Center,
            ) { Text("這欄還是空的", color = InkGray400, fontSize = 12.sp) }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { SkillCard(it, accent, onAdvance) }
            }
        }
    }
}

@Composable
private fun SkillCard(skill: LearnSkill, accent: Color, onAdvance: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val isDone = skill.stage == LearnStage.DONE
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(PaperWhite)
            .pressScale { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize(tween(220)),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(34.dp).clip(CircleShape).background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                if (isDone) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                } else {
                    Text(skill.name.first().toString(), color = accent, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    skill.name,
                    color = if (isDone) InkGray400 else InkBlack,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                )
                Text(skill.estimate, color = InkGray400, fontSize = 11.sp)
            }
        }
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Text("為什麼要學", color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(skill.why, color = InkGray700, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(10.dp))
            Text("學習資源", color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(skill.resource, color = InkGray700, fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            // 推進按鈕
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(if (isDone) InkGray100 else InkBlack)
                    .pressScale { onAdvance(skill.id) }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isDone) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null,
                            tint = PaperWhite, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        when (skill.stage) {
                            LearnStage.TODO -> "開始學這個"
                            LearnStage.DOING -> "標記為已掌握"
                            LearnStage.DONE -> "移回進行中"
                        },
                        color = if (isDone) InkGray700 else PaperWhite,
                        fontWeight = FontWeight.Black, fontSize = 13.sp,
                    )
                }
            }
        }
    }
}
