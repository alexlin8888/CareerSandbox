package com.careersandbox.app.data.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/* =====================================================================
   RemoteInterviewEngine — 面試接後端(FastAPI)的 client(給 model 組對接用)
   純標準庫(HttpURLConnection + org.json),不需新增依賴。任何失敗自動退回 MockInterviewEngine。

   啟用:InterviewEngineProvider.useRemote = true; InterviewEngineProvider.baseUrl = "https://host"

   端點:
     POST {baseUrl}/interview/question
       req: { sessionId, seatIndex, role, name, askedSoFar }
       res: { question, concluded }
     POST {baseUrl}/interview/score
       req: { sessionId, seatIndex, role, question, answer }
       res: { reactionDelta(-1/0/1), reactionText, comment }
   ===================================================================== */
object RemoteInterviewEngine : InterviewEngine {

    override suspend fun nextQuestion(req: InterviewQuestionRequest): InterviewQuestionResponse =
        withContext(Dispatchers.IO) {
            val base = InterviewEngineProvider.baseUrl.trim().trimEnd('/')
            if (base.isBlank()) return@withContext MockInterviewEngine.nextQuestion(req)
            try {
                val body = JSONObject()
                    .put("sessionId", req.sessionId)
                    .put("seatIndex", req.seatIndex)
                    .put("role", req.role)
                    .put("name", req.name)
                    .put("askedSoFar", req.askedSoFar)
                val o = post("$base/interview/question", body)
                    ?: return@withContext MockInterviewEngine.nextQuestion(req)
                InterviewQuestionResponse(
                    question = o.optString("question", ""),
                    concluded = o.optBoolean("concluded", false),
                )
            } catch (e: Exception) {
                MockInterviewEngine.nextQuestion(req)
            }
        }

    override suspend fun scoreAnswer(req: InterviewScoreRequest): InterviewScoreResponse =
        withContext(Dispatchers.IO) {
            val base = InterviewEngineProvider.baseUrl.trim().trimEnd('/')
            if (base.isBlank()) return@withContext MockInterviewEngine.scoreAnswer(req)
            try {
                val body = JSONObject()
                    .put("sessionId", req.sessionId)
                    .put("seatIndex", req.seatIndex)
                    .put("role", req.role)
                    .put("question", req.question)
                    .put("answer", req.answer)
                val o = post("$base/interview/score", body)
                    ?: return@withContext MockInterviewEngine.scoreAnswer(req)
                InterviewScoreResponse(
                    reactionDelta = o.optInt("reactionDelta", 0),
                    reactionText = o.optString("reactionText", ""),
                    comment = o.optString("comment", ""),
                )
            } catch (e: Exception) {
                MockInterviewEngine.scoreAnswer(req)
            }
        }

    // 共用 POST:成功且 2xx 回 JSONObject,否則 null(呼叫端退回 mock)
    private fun post(urlStr: String, body: JSONObject): JSONObject? {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 8000
            readTimeout = 20000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
        }
        conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
        conn.disconnect()
        return if (code in 200..299 && text.isNotBlank()) JSONObject(text) else null
    }
}
