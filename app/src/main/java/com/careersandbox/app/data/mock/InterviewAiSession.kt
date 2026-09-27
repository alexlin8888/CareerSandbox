package com.careersandbox.app.data.mock

import com.careersandbox.app.data.remote.PersonaDto

/* =====================================================================
   存放這次面試從模型組 AI 服務拿到的真實資料（sessionId、開場題目等），
   讓 Setup 畫面呼叫 /interviews 拿到結果後，Live 畫面之後可以讀取。
   跟 InterviewConfig（使用者的設定選項）、InterviewSession（逐字稿記錄）
   是三個不同用途、各自獨立的物件。
   ===================================================================== */
object InterviewAiSession {
    var sessionId: String? = null
    var openingQuestion: String? = null
    var openingSpeaker: String = ""
    var openingTopic: String = ""
    var personas: List<PersonaDto> = emptyList()
    var fallbackProbes: List<String> = emptyList()

    fun reset() {
        sessionId = null
        openingQuestion = null
        openingSpeaker = ""
        openingTopic = ""
        personas = emptyList()
        fallbackProbes = emptyList()
    }
}