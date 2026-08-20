package com.careersandbox.app.data.mock

/* =====================================================================
   面試逐字記錄(現場 live 寫入、報告讀取)
   一場面試進行中,各 live 頁把「題目 + 你的回答」逐輪記進來;
   報告頁的逐題回顧改讀這裡 → 顯示你「真正講的」,而非寫死範例。
   之後由後端 Interview_Turns 取代,介面不變。
   進入任何 live 頁時先 reset(),避免跨場殘留。
   ===================================================================== */
object InterviewSession {
    data class Turn(val question: String, val answer: String)

    /** 團體面試完整逐字稿:依實際發生順序記錄每一句話跟說話者(使用者或 AI 隊友)。*/
    data class GroupUtterance(
        val speaker: String,
        val content: String,
        val isUser: Boolean,
        val segments: List<String> = emptyList(),
        val segmentStartsMs: List<Long> = emptyList(),
    )

    val turns = mutableListOf<Turn>()

    @Deprecated("只存使用者發言,已不足以支援評分需求,改用 groupTranscript")
    val groupSays = mutableListOf<String>()

    val groupTranscript = mutableListOf<GroupUtterance>()

    fun reset() {
        turns.clear()
        groupSays.clear()
        groupTranscript.clear()
    }

    fun record(question: String, answer: String) {
        if (answer.isBlank()) return
        turns.add(Turn(question.trim(), answer.trim()))
    }

    fun recordGroupSay(text: String) {
        if (text.isBlank()) return
        groupSays.add(text.trim())
    }

    /** 記錄團體面試裡任何一句發言(使用者或 AI),依呼叫順序即代表實際發生順序。*/
    fun recordGroupUtterance(
        speaker: String,
        content: String,
        isUser: Boolean,
        segments: List<String> = emptyList(),
        segmentStartsMs: List<Long> = emptyList(),
    ) {
        if (content.isBlank()) return
        groupTranscript.add(GroupUtterance(speaker.trim(), content.trim(), isUser, segments, segmentStartsMs))
    }
}
