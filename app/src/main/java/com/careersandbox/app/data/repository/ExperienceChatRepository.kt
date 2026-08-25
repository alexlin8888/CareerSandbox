package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.ChatExtractedFields
import com.careersandbox.app.data.remote.ChatHistoryTurn
import com.careersandbox.app.data.remote.ChatTurnRequest
import com.careersandbox.app.data.remote.ChatTurnResponse
import com.careersandbox.app.data.remote.ExperienceChatApiService

interface ExperienceChatRepository {
    suspend fun sendTurn(history: List<ChatHistoryTurn>, answer: String): Result<ChatTurnResponse>
}

class RemoteExperienceChatRepository(
    private val api: ExperienceChatApiService = ApiClient.experienceChatApi
) : ExperienceChatRepository {

    override suspend fun sendTurn(
        history: List<ChatHistoryTurn>,
        answer: String,
    ): Result<ChatTurnResponse> = try {
        val res = api.turn(ChatTurnRequest(history, answer))
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("AI 服務暫時無法使用（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("網路連線異常，請稍後再試"))
    }
}