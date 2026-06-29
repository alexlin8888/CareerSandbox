package com.careersandbox.app.data.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/* =====================================================================
   RemoteSandboxChatEngine — 接真後端(FastAPI)的 client
   ---------------------------------------------------------------------
   給 model 組對接用:把沙盒對話的 HTTP 合約寫死在這。純標準庫
   (HttpURLConnection + org.json,皆 Android SDK 內建),不需新增任何 build.gradle 依賴。

   啟用方式(後端就緒時,在 App 啟動或設定處):
       SandboxChatEngineProvider.useRemote = true
       SandboxChatEngineProvider.baseUrl   = "https://your-fastapi-host"

   端點約定:
       POST {baseUrl}/sandbox/chat
       Content-Type: application/json

   Request body(對齊 SandboxTurnRequest):
   {
     "sessionId": "...", "day": 1, "npcId": "ken",
     "playerMessage": "玩家選的那句(round 0 為空字串)",
     "managerTrust": 3, "peerBond": 3, "proImage": 3,
     "flags": ["intel_d2_mail", ...],
     "history": [ { "fromPlayer": false, "text": "..." }, { "fromPlayer": true, "text": "..." } ]
   }

   Response body(對齊 SandboxTurnResponse):
   {
     "npcMessage": "NPC 這一輪回應(round 0 可空)",
     "meterDeltas": [ { "meter": "主管信任", "delta": 1, "reason": "..." } ],
     "newFlags": ["..."],
     "concluded": false,
     "choices": ["選項A", "選項B", "選項C"]
   }

   穩健性:baseUrl 未設、連線失敗、非 2xx、JSON 解析失敗 → 一律退回 MockSandboxChatEngine,
   確保 demo / 離線時對話永遠能跑。
   ===================================================================== */
object RemoteSandboxChatEngine : SandboxChatEngine {

    override suspend fun reply(req: SandboxTurnRequest): SandboxTurnResponse = withContext(Dispatchers.IO) {
        val base = SandboxChatEngineProvider.baseUrl.trim().trimEnd('/')
        if (base.isBlank()) return@withContext MockSandboxChatEngine.reply(req)
        try {
            val conn = (URL("$base/sandbox/chat").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 8000
                readTimeout = 20000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }
            conn.outputStream.use { it.write(encodeRequest(req).toString().toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
            conn.disconnect()
            if (code !in 200..299 || text.isBlank()) {
                MockSandboxChatEngine.reply(req)
            } else {
                decodeResponse(JSONObject(text))
            }
        } catch (e: Exception) {
            // 連線/逾時/解析任何失敗 → 退回 mock,對話不中斷
            MockSandboxChatEngine.reply(req)
        }
    }

    private fun encodeRequest(req: SandboxTurnRequest): JSONObject {
        val history = JSONArray()
        req.history.forEach { line ->
            history.put(JSONObject().put("fromPlayer", line.fromPlayer).put("text", line.text))
        }
        val flags = JSONArray()
        req.flags.forEach { flags.put(it) }
        return JSONObject()
            .put("sessionId", req.sessionId)
            .put("day", req.day)
            .put("npcId", req.npcId)
            .put("playerMessage", req.playerMessage)
            .put("managerTrust", req.managerTrust)
            .put("peerBond", req.peerBond)
            .put("proImage", req.proImage)
            .put("flags", flags)
            .put("history", history)
    }

    private fun decodeResponse(o: JSONObject): SandboxTurnResponse {
        val deltas = mutableListOf<MeterDelta>()
        o.optJSONArray("meterDeltas")?.let { arr ->
            for (i in 0 until arr.length()) {
                val d = arr.getJSONObject(i)
                deltas.add(MeterDelta(d.getString("meter"), d.getInt("delta"), d.optString("reason", "")))
            }
        }
        val flags = mutableListOf<String>()
        o.optJSONArray("newFlags")?.let { arr -> for (i in 0 until arr.length()) flags.add(arr.getString(i)) }
        val choices = mutableListOf<String>()
        o.optJSONArray("choices")?.let { arr -> for (i in 0 until arr.length()) choices.add(arr.getString(i)) }
        return SandboxTurnResponse(
            npcMessage = o.optString("npcMessage", ""),
            meterDeltas = deltas,
            newFlags = flags,
            concluded = o.optBoolean("concluded", false),
            choices = choices,
        )
    }
}
