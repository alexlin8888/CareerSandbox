package com.careersandbox.app.data.repository

import com.careersandbox.app.data.mock.CustomizedItem
import com.careersandbox.app.data.remote.AiApiService
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.CustomizeResumeRequest

/** Everything the JD customization screen needs, in one call. */
data class CustomizeResult(
    val jdKeywords: List<String>,
    val coveredKeywords: List<String>,
    val items: List<CustomizedItem>,
)

interface AiRepository {
    suspend fun customizeResume(jobId: String): Result<CustomizeResult>
}

class RemoteAiRepository(
    private val api: AiApiService = ApiClient.aiApi
) : AiRepository {

    override suspend fun customizeResume(jobId: String): Result<CustomizeResult> = try {
        val res = api.customizeResume(CustomizeResumeRequest(jobId))
        when {
            res.isSuccessful && res.body() != null -> {
                val b = res.body()!!
                Result.success(
                    CustomizeResult(
                        jdKeywords = b.jdKeywords ?: emptyList(),
                        coveredKeywords = b.coveredKeywords ?: emptyList(),
                        items = (b.items ?: emptyList()).map {
                            CustomizedItem(
                                id = it.id,
                                text = it.text.orEmpty(),
                                matchedKeywords = it.matchedKeywords ?: emptyList(),
                                highlighted = it.highlighted ?: false,
                            )
                        },
                    )
                )
            }
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            res.code() == 404 -> Result.failure(Exception("找不到這個職缺"))
            res.code() in listOf(502, 504) -> Result.failure(Exception("AI 服務暫時無法回應，請稍後再試"))
            else -> Result.failure(Exception("客製化失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
}