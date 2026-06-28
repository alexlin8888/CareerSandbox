package com.careersandbox.app.data.mock

import kotlinx.coroutines.delay

/* =====================================================================
   模型驅動沙盒 —— 前端 ↔ 後端 接縫(混合架構的「表層」)
   骨架(五天結構/角色立場/必觸發戲劇拍/計量定義)仍由腳本掌握;
   這一層負責「玩家自由回覆 → NPC 即時回應 + 計量變化」,由後端 LLM 生成。

   前端只做:把玩家輸入 + 當前狀態(天/三計量/旗標/歷史)送進 engine,
   渲染回傳的 NPC 回應、套用 meterDeltas、收束時進下一步。

   現在用 MockSandboxChatEngine(關鍵字粗判,純為了讓前端迴圈能跑能 demo)。
   後端就緒後:把 SandboxChatEngineProvider.engine 換成呼叫 FastAPI 的
   RemoteSandboxChatEngine 即可,本檔的 data class 就是 API 的 JSON 介面。
   ===================================================================== */

/** 送出:玩家這一回合 + 當前狀態 */
data class SandboxTurnRequest(
    val sessionId: String,
    val day: Int,
    val npcId: String,                 // "ken" / "zhe" / "vivian"
    val playerMessage: String,
    val managerTrust: Int,
    val peerBond: Int,
    val proImage: Int,
    val flags: List<String>,
    val history: List<SandboxLine>,    // 對話歷史(讓語氣/上下文連貫)
)

/** 一則對話(歷史用) */
data class SandboxLine(
    val fromPlayer: Boolean,
    val text: String,
)

/** 一筆計量變化(對應 WorkplaceState.apply) */
data class MeterDelta(
    val meter: String,   // 主管信任 / 同事情誼 / 專業形象
    val delta: Int,
    val reason: String,
)

/** 回傳:NPC 回應 + 狀態變化 */
data class SandboxTurnResponse(
    val npcMessage: String,
    val meterDeltas: List<MeterDelta>,
    val newFlags: List<String>,
    val concluded: Boolean,              // 這段對話是否該收束(進下一步/結束當天)
    val suggestedReplies: List<String>,  // 可選:給玩家的回覆提示(空 = 純自由打字)
)

/** 引擎介面:換後端只換這個的實作,前端不動 */
interface SandboxChatEngine {
    suspend fun reply(req: SandboxTurnRequest): SandboxTurnResponse
}

/**
 * 取得引擎的單一入口。
 * 後端就緒後改成:val engine: SandboxChatEngine = RemoteSandboxChatEngine(BuildConfig.SANDBOX_API)
 */
object SandboxChatEngineProvider {
    val engine: SandboxChatEngine = MockSandboxChatEngine
}

/**
 * 假後端(純前端開發用)。
 * 用關鍵字把語氣粗分幾桶 → 給計量 delta + 罐頭回應。
 * 真正的理解與生成在後端 LLM;這裡只是讓前端的「打字→回應→計量動」迴圈能完整跑起來。
 */
object MockSandboxChatEngine : SandboxChatEngine {

    private val collaborativeWords = listOf("一起", "我們", "幫", "團隊", "理解", "支援", "謝謝", "辛苦")
    private val dataWords = listOf("數據", "資料", "風險", "評估", "範圍", "時程", "依據", "影響")
    private val pushyWords = listOf("一定", "必須", "馬上", "立刻", "上線", "沒問題", "答應", "保證")
    private val dismissiveWords = listOf("不關我", "你自己", "隨便", "不想", "懶得", "不管")

    override suspend fun reply(req: SandboxTurnRequest): SandboxTurnResponse {
        delay(900)   // 模擬「對方正在輸入…」的延遲(真後端是 LLM 回應時間)

        val msg = req.playerMessage
        val collaborative = collaborativeWords.any { it in msg }
        val datadriven = dataWords.any { it in msg }
        val pushy = pushyWords.any { it in msg }
        val dismissive = dismissiveWords.any { it in msg }

        val deltas = mutableListOf<MeterDelta>()
        if (collaborative) deltas.add(MeterDelta("同事情誼", +1, "你把對方放進來一起想"))
        if (datadriven) deltas.add(MeterDelta("專業形象", +1, "你用依據說話,不是憑感覺"))
        if (datadriven) deltas.add(MeterDelta("主管信任", +1, "讓人覺得交給你會評估清楚"))
        if (pushy && !datadriven) deltas.add(MeterDelta("專業形象", -1, "答應得太快,沒先確認可行性"))
        if (dismissive) deltas.add(MeterDelta("同事情誼", -1, "對方感覺被推開了"))
        if (deltas.isEmpty()) deltas.add(MeterDelta("主管信任", 0, "中性回應,沒有明顯加減"))

        val npcLine = npcReply(req.npcId, collaborative, datadriven, pushy, dismissive)
        val playerTurns = req.history.count { it.fromPlayer } + 1

        return SandboxTurnResponse(
            npcMessage = npcLine,
            meterDeltas = deltas,
            newFlags = emptyList(),
            concluded = playerTurns >= 3,    // demo:聊滿 3 回合就收束(真後端由 director 決定)
            suggestedReplies = emptyList(),
        )
    }

    private fun npcReply(
        npcId: String,
        collaborative: Boolean,
        datadriven: Boolean,
        pushy: Boolean,
        dismissive: Boolean,
    ): String = when (npcId) {
        "ken" -> when {
            datadriven -> "嗯,你有想過風險點,這很好。那你評估下來,我們守得住哪一塊?"
            pushy -> "我欣賞你想扛,但別急著答應——客戶聽到的承諾,之後要由你兌現。"
            dismissive -> "這件事最後還是會回到你身上。我需要你正面接它。"
            else -> "我想聽你的判斷,不是標準答案。你會怎麼處理?"
        }
        "zhe" -> when {
            collaborative -> "謝啦,有人這樣講我好過一點。那排程我們一起跟 Ken 講?"
            pushy -> "你說上線就上線喔?程式碼上掛名的是我,我得說清楚風險。"
            dismissive -> "……那算了,我自己想辦法。"
            else -> "我不是不想做,是兩週內真的有風險。你站哪邊?"
        }
        "vivian" -> when {
            datadriven -> "你講範圍跟時程我懂,但客戶那邊的時間我也不能跳票啊。"
            collaborative -> "好,那我們一起想個兩邊都活得下去的版本。"
            pushy -> "你也覺得能上?太好了,那我去回客戶了喔——你可別反悔。"
            else -> "客戶下週要 demo,這個我真的需要你幫忙頂一下。"
        }
        else -> "嗯,你繼續說。"
    }
}
