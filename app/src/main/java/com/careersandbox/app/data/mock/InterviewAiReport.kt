package com.careersandbox.app.data.mock

import com.careersandbox.app.data.remote.ReportResponse

/* =====================================================================
   存放這場面試從模型組 AI 服務拿到的真實報告結果。
   跟 InterviewAiSession（開場資料）是同一種角色，只是這次存的是報告。
   ===================================================================== */
object InterviewAiReport {
    var response: ReportResponse? = null

    fun reset() {
        response = null
    }
}