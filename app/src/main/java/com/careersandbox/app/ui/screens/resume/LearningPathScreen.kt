package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

/* =====================================================================
   學習路徑 —— 重做
   兩種模式:
   1) 表單(手動建立):告訴 AI 想學什麼 → 生成
   2) 每日計畫:把目標拆成每天可執行的 Day 卡;今日卡可展開,
      AI 一步步教「從哪裡開始」(4 步 + 學習資源 + 今日練習 + 開始按鈕)
   也可由「職涯探索」的建議路徑一鍵生成(stage 2 接線)
   ===================================================================== */

private enum class LpMode { FORM, PLAN }
private enum class DayState { DONE, TODAY, FUTURE }

private data class GuideStep(val title: String, val desc: String)

private data class DayTask(
    val day: Int,
    val title: String,
    val sub: String,
    val state: DayState,
    val focus: String = "",                         // 展開時的一句重點(非今日)
    val guideSteps: List<GuideStep> = emptyList(),  // 今日:從哪開始
    val resources: List<String> = emptyList(),
    val practice: String = "",
)

/* ---------- 示範資料:資料分析入門 14 天 ---------- */
private const val PLAN_TITLE = "資料分析入門"
private const val PLAN_SOURCE = "由「給 UX 設計師的學習路徑」自動生成 · 也可手動建立"
private const val PLAN_DESC = "把資料庫管理、GDA 證照、BI 實習這條路,拆成 14 天循序漸進的每日任務。"
private const val PLAN_DONE = 6
private const val PLAN_TOTAL = 14

private val demoDays = listOf(
    DayTask(1, "認識資料分析流程", "先看懂資料分析在做什麼", DayState.DONE),
    DayTask(2, "Excel 資料整理", "把雜亂資料變乾淨的表", DayState.DONE),
    DayTask(3, "資料清理與去重", "處理空值、重複、格式不一", DayState.DONE),
    DayTask(4, "常用函數", "VLOOKUP、IF、SUMIF 上手", DayState.DONE),
    DayTask(5, "排序與篩選", "快速找出你要的那一群", DayState.DONE),
    DayTask(6, "樞紐分析表", "Excel 樞紐表快速拆報表", DayState.DONE),
    DayTask(
        7, "資料視覺化", "用圖表把分析說成故事", DayState.TODAY,
        guideSteps = listOf(
            GuideStep("先搞懂「哪種圖配哪種資料」", "花 5 分鐘看長條／折線／圓餅各適合什麼,別一開始就糾結美感。"),
            GuideStep("打開今天的資料集,挑一個問題", "例如:哪個月業績最高?先有問題,圖才有方向。"),
            GuideStep("做出第一張圖,先求有不求美", "用 Excel 或 Tableau 拉一張長條圖出來就好。"),
            GuideStep("加標題＋一句結論", "讓圖自己會說話,這是「資料說故事」的核心。"),
        ),
        resources = listOf("Tableau Public 入門教學(免費)", "資料視覺化 10 大原則"),
        practice = "用提供的銷售資料,做一張能一眼看出趨勢的圖,並寫下一句你看到的結論。",
    ),
    DayTask(8, "敘事與簡報", "把結論講給沒背景的人聽懂", DayState.FUTURE,
        focus = "練習把一張圖配一句話,串成 3 頁的小故事。"),
    DayTask(9, "基礎統計概念", "看懂平均、分布與顯著性", DayState.FUTURE,
        focus = "重點是「看懂」而不是會算,面試常被問到。"),
    DayTask(10, "A/B 測試入門", "追蹤活動成效的基本功", DayState.FUTURE,
        focus = "搞懂對照組與實驗組的差別就先夠用。"),
    DayTask(11, "GA4 數據", "從網站行為看使用者", DayState.FUTURE),
    DayTask(12, "SQL 撈資料", "自己撈,不用每次求人", DayState.FUTURE),
    DayTask(13, "綜合小專案", "把這 12 天的技能串起來", DayState.FUTURE),
    DayTask(14, "成果發表", "把專案整理成可放作品集的報告", DayState.FUTURE),
)

private data class ProjectIdea(val title: String, val sub: String, val tag: String)

private val demoProjects = listOf(
    ProjectIdea("銷售儀表板", "用一份電商資料做出可互動的儀表板", "Excel / Tableau"),
    ProjectIdea("使用者行為分析", "從 GA4 找出流失最多的頁面並提出假設", "GA4"),
    ProjectIdea("A/B 測試報告", "設計一個小實驗並寫出結論報告", "統計"),
)

/* ===================================================================== */

@Composable
fun LearningPathScreen(navController: NavHostController) {
    var mode by remember { mutableStateOf(LpMode.FORM) }
    when (mode) {
        LpMode.FORM -> LpForm(navController, onGenerate = { mode = LpMode.PLAN })
        LpMode.PLAN -> LpPlan(navController, onEditForm = { mode = LpMode.FORM })
    }
}

/* ============================ 表單模式 ============================ */

@Composable
private fun LpForm(navController: NavHostController, onGenerate: () -> Unit) {
    var topic by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(1) }      // 0:7天 1:14天 2:30天
    var dailyTime by remember { mutableStateOf(1) }     // 0:15 1:30 2:60
    var level by remember { mutableStateOf(0) }         // 0 初學 1 中階 2 進階
    var styles by remember { mutableStateOf(setOf(0)) } // 多選
    var goal by remember { mutableStateOf("") }
    var includeProjects by remember { mutableStateOf(false) }
    var includeExercises by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().background(PaperWarm).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        TopBar("建立學習路徑") { navController.popBackStack() }
        Spacer(Modifier.height(18.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            Text("生成你的專屬學習路徑", color = InkBlack, fontWeight = FontWeight.Black,
                fontSize = 22.sp, lineHeight = 28.sp)
            Spacer(Modifier.height(6.dp))
            Text("告訴我你想學什麼,AI 會幫你排出每天可執行的學習計畫。",
                color = InkGray500, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(20.dp))

            FieldLabel("想學什麼?")
            InputBox(topic, { topic = it }, "例如:資料分析、UI 設計、商用英文")
            Spacer(Modifier.height(16.dp))

            FieldLabel("學習期間")
            ChoiceRow(listOf("7 天", "14 天", "30 天"), duration) { duration = it }
            Spacer(Modifier.height(16.dp))

            FieldLabel("每天時間")
            ChoiceRow(listOf("15 分鐘", "30 分鐘", "60 分鐘"), dailyTime) { dailyTime = it }
            Spacer(Modifier.height(16.dp))

            FieldLabel("你的程度")
            ChoiceRow(listOf("初學者", "中階", "進階"), level) { level = it }
            Spacer(Modifier.height(16.dp))

            FieldLabel("偏好的學習方式")
            MultiChoiceRow(listOf("視覺", "聽覺", "實作", "讀寫"), styles) { idx ->
                styles = if (styles.contains(idx)) styles - idx else styles + idx
            }
            Spacer(Modifier.height(16.dp))

            FieldLabel("想達成什麼?")
            InputBox(goal, { goal = it }, "例如:做出一個作品、考到證照、開始接案", minHeight = 76.dp, singleLine = false)
            Spacer(Modifier.height(16.dp))

            FieldLabel("額外選項")
            CheckRow("包含實作專案建議", "給你可以動手做的專案點子", includeProjects) { includeProjects = !includeProjects }
            Spacer(Modifier.height(8.dp))
            CheckRow("包含每日練習", "每天給一個小練習鞏固", includeExercises) { includeExercises = !includeExercises }
            Spacer(Modifier.height(16.dp))

            FieldLabel("補充說明(選填)")
            InputBox(notes, { notes = it }, "任何特別的需求、偏好或限制…", minHeight = 70.dp, singleLine = false)
            Spacer(Modifier.height(22.dp))

            // 生成按鈕
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange)))
                    .pressScale { onGenerate() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("✦  生成學習路徑", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Spacer(Modifier.height(36.dp))
        }
    }
}

/* ============================ 每日計畫模式 ============================ */

@Composable
private fun LpPlan(navController: NavHostController, onEditForm: () -> Unit) {
    var tab by remember { mutableStateOf(0) } // 0 學習計畫 1 實作專案

    Column(
        Modifier.fillMaxSize().background(PaperWarm).verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        // 頂列(含右側重新編輯)
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
            Text(PLAN_TITLE, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.clip(RoundedCornerShape(10.dp)).background(InkGray100)
                    .pressScale { onEditForm() }.padding(horizontal = 12.dp, vertical = 7.dp),
            ) { Text("重新設定", color = InkGray700, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(16.dp))

        Column(Modifier.padding(horizontal = 20.dp)) {
            // 來源標籤
            Row(
                Modifier.clip(RoundedCornerShape(8.dp)).background(InkGray100)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Text("由「", color = InkGray500, fontSize = 11.sp)
                Text("給 UX 設計師的學習路徑", color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("」自動生成 · 也可手動建立", color = InkGray500, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(PLAN_DESC, color = InkGray700, fontSize = 13.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(16.dp))

            // 進度
            ProgressBlock(done = PLAN_DONE, total = PLAN_TOTAL)
            Spacer(Modifier.height(18.dp))

            // Tabs
            Row(Modifier.fillMaxWidth()) {
                TabItem("學習計畫", tab == 0, Modifier.weight(1f)) { tab = 0 }
                TabItem("實作專案", tab == 1, Modifier.weight(1f)) { tab = 1 }
            }
            Box(Modifier.fillMaxWidth().height(1.5.dp).background(InkGray100))
            Spacer(Modifier.height(16.dp))
        }

        if (tab == 0) {
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                demoDays.forEach { DayCard(it) }
            }
        } else {
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                demoProjects.forEach { ProjectCard(it) }
            }
        }
        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun ProgressBlock(done: Int, total: Int) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appear = true }
    val frac = if (total > 0) done.toFloat() / total else 0f
    val prog by animateFloatAsState(
        targetValue = if (appear) frac else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "lpProg",
    )
    val pct = (frac * 100).toInt()
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("進度", color = InkGray500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.clip(RoundedCornerShape(999.dp)).background(BrandPeach)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) { Text("進行中", color = BrandDeepOrange, fontSize = 10.5.sp, fontWeight = FontWeight.Black) }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(InkGray100),
        ) {
            Box(
                Modifier.fillMaxWidth(prog.coerceAtLeast(0.02f)).fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(Brush.horizontalGradient(listOf(BrandAmber, BrandOrange))),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("$pct% 完成 · $done/$total 個任務", color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TabItem(label: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier.pressScale { onClick() }.padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, color = if (on) BrandDeepOrange else InkGray400,
            fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier.fillMaxWidth().height(2.5.dp)
                .background(if (on) BrandOrange else Color.Transparent),
        )
    }
}

/* ---------- Day 卡(可展開 → AI 教學) ---------- */

@Composable
private fun DayCard(task: DayTask) {
    val isDone = task.state == DayState.DONE
    val isToday = task.state == DayState.TODAY
    var expanded by remember { mutableStateOf(isToday) } // 今日預設展開

    val borderMod = if (isToday) Modifier.border(2.dp, BrandOrange, RoundedCornerShape(14.dp))
    else Modifier.border(1.5.dp, InkGray200, RoundedCornerShape(14.dp))

    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PaperWhite)
            .then(borderMod)
            .pressScale { expanded = !expanded }
            .padding(13.dp)
            .animateContentSize(tween(220)),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // 狀態圖示
            Box(Modifier.size(25.dp).padding(top = 1.dp), contentAlignment = Alignment.Center) {
                when (task.state) {
                    DayState.DONE -> Box(
                        Modifier.size(25.dp).clip(CircleShape).background(AccentGreen),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Outlined.Check, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(15.dp)) }
                    DayState.TODAY -> Box(
                        Modifier.size(25.dp).clip(CircleShape).background(BrandOrange),
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(14.dp)) }
                    DayState.FUTURE -> Box(
                        Modifier.size(22.dp).clip(CircleShape).border(2.dp, InkGray200, CircleShape),
                    )
                }
            }
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Day ${task.day}", color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    if (isToday) {
                        Spacer(Modifier.width(7.dp))
                        Box(
                            Modifier.clip(RoundedCornerShape(5.dp)).background(BrandOrange)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) { Text("今日", color = PaperWhite, fontSize = 9.sp, fontWeight = FontWeight.Black) }
                    }
                }
                Text(task.title, color = if (isDone) InkGray400 else InkBlack,
                    fontSize = 15.sp, fontWeight = FontWeight.Black,
                    textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    modifier = Modifier.padding(top = 2.dp))
                Text(task.sub, color = InkGray500, fontSize = 11.sp,
                    textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                    modifier = Modifier.padding(top = 1.dp))
            }
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null, tint = InkGray400,
                modifier = Modifier.size(20.dp).padding(top = 2.dp),
            )
        }

        if (expanded) {
            if (isToday && task.guideSteps.isNotEmpty()) {
                TodayGuide(task)
            } else {
                Spacer(Modifier.height(11.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray100))
                Spacer(Modifier.height(11.dp))
                if (isDone) {
                    Text("✓ 你已完成這天的任務", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    val tip = task.focus.ifEmpty { "這天的內容,AI 會在你開始時一步步帶你走。" }
                    Text("🧭 重點", color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(5.dp))
                    Text(tip, color = InkGray700, fontSize = 12.5.sp, lineHeight = 19.sp)
                }
            }
        }
    }
}

@Composable
private fun TodayGuide(task: DayTask) {
    Spacer(Modifier.height(13.dp))
    Box(Modifier.fillMaxWidth().height(1.dp).background(InkGray200))
    Spacer(Modifier.height(13.dp))

    GuideSectionTitle("🧭 從哪裡開始(AI 帶你走)")
    task.guideSteps.forEachIndexed { i, g ->
        Row(Modifier.fillMaxWidth().padding(bottom = 11.dp)) {
            Box(
                Modifier.size(20.dp).clip(CircleShape).background(BrandPeach),
                contentAlignment = Alignment.Center,
            ) { Text("${i + 1}", color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Black) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(g.title, color = InkBlack, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                Text(g.desc, color = InkGray500, fontSize = 11.sp, lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 1.dp))
            }
        }
    }

    if (task.resources.isNotEmpty()) {
        Spacer(Modifier.height(2.dp))
        GuideSectionTitle("📺 學習資源")
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(PaperWarm)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            task.resources.forEach {
                Row(Modifier.padding(vertical = 3.dp)) {
                    Text("▸ ", color = BrandOrange, fontSize = 11.5.sp)
                    Text(it, color = InkGray700, fontSize = 11.5.sp, lineHeight = 17.sp)
                }
            }
        }
    }

    if (task.practice.isNotEmpty()) {
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF6EE))
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(BrandOrange))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("✏️ 今日練習", color = BrandDeepOrange, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(3.dp))
                Text(task.practice, color = InkGray700, fontSize = 11.5.sp, lineHeight = 18.sp)
            }
        }
    }

    Spacer(Modifier.height(13.dp))
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(11.dp))
            .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange)))
            .pressScale { /* stage 2:開始今日任務 */ }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("開始今日任務", color = PaperWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun GuideSectionTitle(text: String) {
    Text(text, color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Black,
        letterSpacing = 0.5.sp, modifier = Modifier.padding(bottom = 9.dp))
}

@Composable
private fun ProjectCard(p: ProjectIdea) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PaperWhite)
            .border(1.5.dp, InkGray200, RoundedCornerShape(14.dp)).padding(14.dp),
    ) {
        Box(
            Modifier.clip(RoundedCornerShape(6.dp)).background(BrandPeach)
                .padding(horizontal = 7.dp, vertical = 3.dp),
        ) { Text(p.tag, color = BrandDeepOrange, fontSize = 9.5.sp, fontWeight = FontWeight.Black) }
        Spacer(Modifier.height(8.dp))
        Text(p.title, color = InkBlack, fontSize = 15.sp, fontWeight = FontWeight.Black)
        Text(p.sub, color = InkGray500, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

/* ============================ 共用小元件 ============================ */

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(38.dp).clip(CircleShape).background(InkGray100).pressScale { onBack() },
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Text(title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = InkBlack, fontSize = 13.sp, fontWeight = FontWeight.Black,
        modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun InputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minHeight: Dp = 46.dp,
    singleLine: Boolean = true,
) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PaperWhite)
            .border(1.5.dp, InkGray200, RoundedCornerShape(12.dp))
            .heightIn(min = minHeight)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = InkBlack, fontSize = 14.sp, lineHeight = 20.sp),
            singleLine = singleLine,
            cursorBrush = SolidColor(BrandOrange),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(placeholder, color = InkGray400, fontSize = 14.sp, lineHeight = 20.sp)
                }
                inner()
            },
        )
    }
}

@Composable
private fun ChoiceRow(options: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { i, label ->
            Chip(label, i == selected) { onSelect(i) }
        }
    }
}

@Composable
private fun MultiChoiceRow(options: List<String>, selected: Set<Int>, onToggle: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEachIndexed { i, label ->
            Chip(label, selected.contains(i)) { onToggle(i) }
        }
    }
}

@Composable
private fun Chip(label: String, on: Boolean, onClick: () -> Unit) {
    val mod = if (on) Modifier.background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange)))
    else Modifier.background(PaperWhite).border(1.5.dp, InkGray200, RoundedCornerShape(999.dp))
    Box(
        Modifier.clip(RoundedCornerShape(999.dp)).then(mod).pressScale { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(label, color = if (on) PaperWhite else InkGray700,
            fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CheckRow(title: String, sub: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().pressScale { onToggle() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val boxMod = if (checked) Modifier.background(BrandOrange)
        else Modifier.background(PaperWhite).border(2.dp, InkGray200, RoundedCornerShape(6.dp))
        Box(
            Modifier.size(22.dp).clip(RoundedCornerShape(6.dp)).then(boxMod),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) Icon(Icons.Outlined.Check, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = InkBlack, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = InkGray400, fontSize = 11.sp)
        }
    }
}
