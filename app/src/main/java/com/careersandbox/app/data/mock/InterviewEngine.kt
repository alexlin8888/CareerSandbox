package com.careersandbox.app.data.mock

/* =====================================================================
   面試引擎(可切換 mock / remote)
   面試頁透過 InterviewEngineProvider.engine 取題與評分:
     - nextQuestion:給某位主考官出題(或 concluded=true 表示問完)。
     - scoreAnswer:評玩家的回答 → reactionDelta(-1/0/1,驅動主考官表情)+ 反應文字 + 評語。
   預設走 MockInterviewEngine;後端就緒時設 InterviewEngineProvider.useRemote/baseUrl 切到 Remote。
   ===================================================================== */

data class InterviewQuestionRequest(
    val sessionId: String,
    val seatIndex: Int,   // 第幾位主考官
    val role: String,     // 角色(用人主管/技術主管/業務代表…)
    val name: String,
    val askedSoFar: Int,  // 已問過幾題
)
data class InterviewQuestionResponse(
    val question: String,
    val concluded: Boolean,
)

data class InterviewScoreRequest(
    val sessionId: String,
    val seatIndex: Int,
    val role: String,
    val question: String,
    val answer: String,
)
data class InterviewScoreResponse(
    val reactionDelta: Int,    // -1 / 0 / 1 → 主考官表情
    val reactionText: String,  // 名牌顯示的反應
    val comment: String,       // 報告用評語(可空)
)

interface InterviewEngine {
    suspend fun nextQuestion(req: InterviewQuestionRequest): InterviewQuestionResponse
    suspend fun scoreAnswer(req: InterviewScoreRequest): InterviewScoreResponse
}

object MockInterviewEngine : InterviewEngine {
    // 各主考官的題目(mock);接後端後改由模型依履歷/職缺出題
    private val bank = mapOf(
        0 to "先自我介紹一下,聊聊你最近最有成就感的一個專案。",
        1 to "測試才跑六成、race condition 還沒解,你會怎麼跟客戶說月底這個時程?",
        2 to "如果讓你加入我們團隊,你覺得第一個月能帶來什麼?",
    )

    override suspend fun nextQuestion(req: InterviewQuestionRequest): InterviewQuestionResponse {
        val q = bank[req.seatIndex]
        return if (q == null) InterviewQuestionResponse("", true)
        else InterviewQuestionResponse(q, false)
    }

    override suspend fun scoreAnswer(req: InterviewScoreRequest): InterviewScoreResponse {
        // mock 啟發式:答案越完整反應越好;接後端後改用真評分
        val d = when {
            req.answer.isBlank() -> 0
            req.answer.length >= 24 -> 1
            req.answer.length >= 10 -> 0
            else -> -1
        }
        val t = when {
            d >= 1 -> "認可地點點頭"
            d <= -1 -> "等你再多說一點"
            else -> "若有所思地聽著"
        }
        val c = when {
            d >= 1 -> "回答完整,有具體例子。"
            d <= -1 -> "稍嫌簡略,建議補上情境與結果。"
            else -> "方向正確,可再深入一點。"
        }
        return InterviewScoreResponse(d, t, c)
    }
}

object InterviewEngineProvider {
    // 後端就緒:設 useRemote=true 並給 baseUrl → 切到 RemoteInterviewEngine(任何失敗自動退回 mock)
    var useRemote: Boolean = false
    var baseUrl: String = ""
    val engine: InterviewEngine
        get() = if (useRemote) RemoteInterviewEngine else MockInterviewEngine
}
