package com.careersandbox.app.data.mock

import kotlinx.coroutines.delay

/* =====================================================================
   模型驅動沙盒 —— 前端 ↔ 後端 接縫(混合架構的「表層」)
   機制:模型生成「NPC 一句話 + 三個情境選項」,玩家選一個,模型評分後回應 + 給下一輪三選項。
   仍是三選一,但選項不是寫死的預設,而是依劇情/狀態/前面選擇即時產生。

   前端只做:把玩家「選的那個選項」+ 當前狀態送進 engine,渲染回傳的 NPC 回應與三個新選項、
   套用 meterDeltas、收束時進下一步。

   現在用 MockSandboxChatEngine(關鍵字粗判,純為了讓前端迴圈能跑能 demo)。
   後端就緒後把 SandboxChatEngineProvider.engine 換成呼叫 FastAPI 的 RemoteSandboxChatEngine;
   本檔 data class 就是 API 合約,後端照這個 schema 回(choices 由 LLM 生成)。
   ===================================================================== */

/** 送出:玩家剛選的選項(round 0 給空字串=要開場選項)+ 當前狀態 */
data class SandboxTurnRequest(
    val sessionId: String,
    val day: Int,
    val npcId: String,                 // "ken" / "zhe" / "vivian" / "fang"
    val playerMessage: String,         // 玩家選的那個選項文字(round 0 = "")
    val managerTrust: Int,
    val peerBond: Int,
    val proImage: Int,
    val flags: List<String>,
    val history: List<SandboxLine>,
)

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

/** 回傳:NPC 回應 + 對剛選那個的評分 + 下一輪三個選項 */
data class SandboxTurnResponse(
    val npcMessage: String,            // NPC 這一句(round 0 可空,用各天 opening)
    val meterDeltas: List<MeterDelta>, // 對「剛選的選項」的評分(round 0 為空)
    val newFlags: List<String>,
    val concluded: Boolean,            // 對話是否收束(進下一步)
    val choices: List<String>,         // 下一輪要給玩家選的三個選項(收束時空)
)

interface SandboxChatEngine {
    suspend fun reply(req: SandboxTurnRequest): SandboxTurnResponse
}

object SandboxChatEngineProvider {
    val engine: SandboxChatEngine = MockSandboxChatEngine
}

/**
 * 假後端(純前端開發用)。
 * 依 npc 給「情境化的三選項」+ 用關鍵字粗分玩家選的立場(合作/數據/強勢/迴避)→ 計量 delta + 回應。
 * 真正的選項生成與理解在後端 LLM;這裡只讓前端「三選一 → 評分 → 下一輪」整條跑起來。
 */
object MockSandboxChatEngine : SandboxChatEngine {

    private val collaborative = listOf("一起", "我們", "幫", "聊過", "謝", "大家", "支援", "聽")
    private val dataWords = listOf("查", "清單", "範圍", "時程", "了解", "風險", "確認", "依據", "看一下", "摸清楚")
    private val assertive = listOf("沒問題", "保證", "答應", "扛", "壓", "最快", "上線", "一定")
    private val avoidant = listOf("自己", "不麻煩", "撐", "再說", "隨便", "不想")

    override suspend fun reply(req: SandboxTurnRequest): SandboxTurnResponse {
        delay(700)   // 模擬 LLM 回應延遲
        val picks = req.history.count { it.fromPlayer }

        // round 0:還沒選過 → 只給開場三選項(不評分,NPC 開場白用各天 opening)
        if (req.playerMessage.isBlank()) {
            return SandboxTurnResponse("", emptyList(), emptyList(), false, choicesFor(req.npcId, 0))
        }

        // 評「剛選的那個選項」
        val msg = req.playerMessage
        val isCollab = collaborative.any { it in msg }
        val isData = dataWords.any { it in msg }
        val isPush = assertive.any { it in msg }
        val isAvoid = avoidant.any { it in msg }
        val deltas = mutableListOf<MeterDelta>()
        if (isData) { deltas.add(MeterDelta("專業形象", +1, "你用依據說話")); deltas.add(MeterDelta("主管信任", +1, "讓人放心交給你")) }
        if (isCollab) deltas.add(MeterDelta("同事情誼", +1, "你把對方放進來一起想"))
        if (isPush && !isData) deltas.add(MeterDelta("專業形象", -1, "答應得太快,沒先確認"))
        if (isAvoid) deltas.add(MeterDelta("同事情誼", -1, "對方感覺被推開"))
        if (deltas.isEmpty()) deltas.add(MeterDelta("主管信任", 0, "中性回應"))

        val reaction = reactTo(req.npcId, isCollab, isData, isPush, isAvoid)
        val concluded = picks >= 3                    // 選滿 3 次收束(真後端由 director 決定)
        val nextChoices = if (concluded) emptyList() else choicesFor(req.npcId, picks)
        return SandboxTurnResponse(reaction, deltas, emptyList(), concluded, nextChoices)
    }

    /** 依 npc + 第幾輪給三個情境選項(真後端由 LLM 生成;這裡是占位池) */
    private fun choicesFor(npcId: String, round: Int): List<String> = when (npcId) {
        "ken" -> if (round == 0) listOf(
            "我先把實際狀況摸清楚,今天去看工程的進度再回您。",
            "我會跟工程和業務都聊過,一起把方案對齊。",
            "沒問題,該上的我保證上,我來扛。",
        ) else listOf(
            "我把風險跟範圍列清楚給您看。",
            "我去問清楚各邊的版本,再給您一個判斷。",
            "就照最快的方式衝,我負責。",
        )
        "zhe" -> if (round == 0) listOf(
            "你的兩週我信,我們一起跟上面講清楚。",
            "可以給我看 bug 清單嗎?我想了解卡在哪。",
            "能不能想辦法壓到一週?上面一直在催。",
        ) else listOf(
            "週末你寫的那個工具,要不要一起看看怎麼用上?",
            "我幫你跟 Ken 說明技術上的風險。",
            "總之先求快,細節之後再補。",
        )
        "vivian" -> if (round == 0) listOf(
            "我們先一起確認 demo 真正要展示什麼。",
            "我查一下範圍跟時程,給你一個務實的版本。",
            "沒問題,我答應客戶這版能上。",
        ) else listOf(
            "基本版先給客戶看,風險也鎖得住。",
            "我去跟工程確認哪些是這次一定要的。",
            "客戶不能等,先答應再說。",
        )
        "fang" -> if (round == 0) listOf(
            "謝謝你提醒,這裡的人我還在摸,想多聽你說。",
            "我想了解大家平常都怎麼合作的。",
            "我自己先撐著就好,不太想麻煩別人。",
        ) else listOf(
            "下次有狀況我會找人一起討論。",
            "我會先把事情搞清楚再開口。",
            "反正撐過去就好,別想太多。",
        )
        else -> listOf("嗯,我想想。", "我先了解一下情況。", "就這樣吧。")
    }

    private fun reactTo(npcId: String, collab: Boolean, data: Boolean, push: Boolean, avoid: Boolean): String =
        when (npcId) {
            "ken" -> when {
                data -> "務實,我喜歡。那你評估下來,我們守得住哪一塊?"
                collab -> "願意把人拉在一起,是好事。別忘了最後要有人拍板。"
                push -> "我欣賞你想扛,但別急著答應——你說的,之後要你兌現。"
                avoid -> "這件事最後還是會回到你身上。我需要你正面接它。"
                else -> "嗯。說說你接下來打算怎麼做?"
            }
            "zhe" -> when {
                collab -> "謝啦,有人這樣講我好過一點。那我們一起跟 Ken 講?"
                data -> "好,我把 bug 清單給你,你看了就知道不是我在拖。"
                push -> "壓到一週?程式碼上掛名的是我,我得先說清楚風險。"
                avoid -> "……那算了,我自己想辦法。"
                else -> "你說了算,但風險我先講在前面。"
            }
            "vivian" -> when {
                data -> "你講範圍跟時程我懂,那我去跟客戶喬一個務實的版本。"
                collab -> "好,那我們一起想個兩邊都活得下去的方式。"
                push -> "你也覺得能上?那我去回客戶了喔——你可別反悔。"
                avoid -> "你這樣我很難跟客戶交代欸。"
                else -> "嗯,那這次 demo 我就先這樣跟客戶說了。"
            }
            "fang" -> when {
                collab -> "對嘛,別自己悶著。這裡其實沒那麼可怕啦。"
                data -> "你這種先搞清楚再動的個性,在這裡會吃得開。"
                avoid -> "唉,第一週都這樣。但有事真的可以找人,別硬撐。"
                else -> "嗯嗯,慢慢來,第一週能撐住就不錯了。"
            }
            else -> "嗯,你繼續。"
        }
}
