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

    val turns = mutableListOf<Turn>()

    fun reset() {
        turns.clear()
    }

    fun record(question: String, answer: String) {
        if (answer.isBlank()) return
        turns.add(Turn(question.trim(), answer.trim()))
    }
}
