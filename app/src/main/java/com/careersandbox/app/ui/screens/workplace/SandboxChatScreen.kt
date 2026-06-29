package com.careersandbox.app.ui.screens.workplace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.RepChange
import com.careersandbox.app.data.mock.SandboxChatEngineProvider
import com.careersandbox.app.data.mock.SandboxLine
import com.careersandbox.app.data.mock.SandboxTurnRequest
import com.careersandbox.app.data.mock.WorkplaceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* =====================================================================
   模型驅動沙盒對話(場景式)
   復用 SandboxDecisionScene 的場景版面(背景圖 + 立繪 + 對話框 + 選項),內容由引擎生成:
   NPC 一句 + 模型生成的三個選項 → 玩家選一個 → 評分(計量動)+ 回應 + 下一輪三選項。
   立繪固定 220dp、對話框固定高度範圍可滑動 → 不再忽大忽小;不是線上聊天泡泡,而是場景對話。
   接後端只需把 SandboxChatEngineProvider.engine 換成 Remote 版,此頁不動。
   ===================================================================== */

private fun npcName(id: String) = when (id) {
    "ken" -> "Ken"
    "zhe" -> "阿哲"
    "vivian" -> "Vivian"
    "fang" -> "小芳"
    else -> "同事"
}

private fun npcOpening(id: String) = when (id) {
    "ken" -> "坐吧。第一週還順利嗎?分帳這案子,我想先聽聽你的想法——你會怎麼接?"
    "zhe" -> "欸,排程的事你怎麼看?兩週內上線我是覺得有風險啦。"
    "vivian" -> "客戶下週要 demo,時間壓得有點緊,想跟你討論一下。"
    "fang" -> "走啦吃飯!第一週撐下來啦,有什麼想問的儘管問。"
    else -> "嗨,聊一下?"
}

private fun bgFor(day: Int) = when (day) {
    1 -> R.drawable.bg_scene_1on1
    2 -> R.drawable.bg_scene_office
    3 -> R.drawable.bg_scene_meeting
    4 -> R.drawable.bg_scene_cafe
    else -> R.drawable.bg_scene_office
}

// 立繪隨剛剛的評分換表情;round 0(delta=0)用基礎表情
private fun portraitFor(id: String, delta: Int, mt: Int): Int = when (id) {
    "ken" -> if (delta != 0) faceKenReact(delta, mt) else faceKenBase(mt)
    "vivian" -> faceVivian(delta)
    "zhe" -> faceAkai(delta)
    "fang" -> faceFang(delta)
    else -> R.drawable.colleague_quiet
}

@Composable
fun SandboxConversation(
    navController: NavHostController,
    npcId: String,
    day: Int,
    opening: String? = null,
    onConcluded: () -> Unit,
) {
    val sessionId = remember { "sandbox-$npcId-$day" }
    val history = remember { mutableStateListOf<SandboxLine>() }
    var npcLine by remember { mutableStateOf(opening ?: npcOpening(npcId)) }
    var choices by remember { mutableStateOf<List<String>>(emptyList()) }
    var thinking by remember { mutableStateOf(false) }
    var concluded by remember { mutableStateOf(false) }
    var lastDelta by remember { mutableIntStateOf(0) }
    var repPop by remember { mutableStateOf<RepChange?>(null) }
    val scope = rememberCoroutineScope()

    fun req(pick: String) = SandboxTurnRequest(
        sessionId = sessionId,
        day = day,
        npcId = npcId,
        playerMessage = pick,
        managerTrust = WorkplaceState.managerTrust.value,
        peerBond = WorkplaceState.peerBond.value,
        proImage = WorkplaceState.proImage.value,
        flags = WorkplaceState.flags.toList(),
        history = history.toList(),
    )

    // 開場:放開場白 → 跟引擎要第一輪三選項
    LaunchedEffect(Unit) {
        history.add(SandboxLine(false, npcLine))
        thinking = true
        choices = SandboxChatEngineProvider.engine.reply(req("")).choices
        thinking = false
    }
    LaunchedEffect(repPop) { if (repPop != null) { delay(1900); repPop = null } }

    fun pick(text: String) {
        if (thinking || concluded) return
        history.add(SandboxLine(true, text))
        choices = emptyList()
        thinking = true
        scope.launch {
            val resp = SandboxChatEngineProvider.engine.reply(req(text))
            resp.meterDeltas.forEach { d ->
                if (d.delta != 0) {
                    repPop = WorkplaceState.apply(d.meter, d.delta, d.reason, day)
                    lastDelta = d.delta
                }
            }
            resp.newFlags.forEach { WorkplaceState.setFlag(it) }
            if (resp.npcMessage.isNotBlank()) {
                npcLine = resp.npcMessage
                history.add(SandboxLine(false, resp.npcMessage))
            }
            choices = resp.choices
            thinking = false
            if (resp.concluded) concluded = true
        }
    }

    val sceneChoices = when {
        concluded -> listOf(DecisionChoice("→", "結束這場對話，繼續"))
        thinking -> emptyList()
        else -> choices.mapIndexed { i, c -> DecisionChoice(('A' + i).toString(), c) }
    }

    SandboxDecisionScene(
        speaker = npcName(npcId),
        portrait = portraitFor(npcId, lastDelta, WorkplaceState.managerTrust.value),
        narration = if (thinking) "（……）" else npcLine,
        choices = sceneChoices,
        sceneLabel = "Day $day · 對話",
        bgRes = bgFor(day),
        repPop = repPop,
        onBack = { navController.popBackStack() },
        onChoose = { c -> if (concluded) onConcluded() else pick(c.label) },
    )
}

/** 獨立路由版(demo / 直接進入):聊完返回上一頁 */
@Composable
fun SandboxChatScreen(
    navController: NavHostController,
    npcId: String = "zhe",
    day: Int = 3,
) {
    SandboxConversation(
        navController = navController,
        npcId = npcId,
        day = day,
        onConcluded = { navController.popBackStack() },
    )
}
