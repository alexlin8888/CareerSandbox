package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.AiApiService
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.ReinforceRequest
import kotlinx.coroutines.CancellationException

data class ReinforceResource(
    val id: String,
    val type: String,         // "course" | "competition"
    val title: String,
    val provider: String,
    val url: String,
    val deadline: String,
    val note: String,
)

/** One missing skill and what can fix it. [resources] may be empty. */
data class ReinforceGroup(
    val skill: String,
    val resources: List<ReinforceResource>,
)

interface ReinforceRepository {
    suspend fun reinforce(missingSkills: List<String>): Result<List<ReinforceGroup>>
}

class RemoteReinforceRepository(
    private val api: AiApiService = ApiClient.aiApi
) : ReinforceRepository {

    override suspend fun reinforce(missingSkills: List<String>): Result<List<ReinforceGroup>> = try {
        val res = api.reinforceSkills(ReinforceRequest(missingSkills))
        when {
            res.isSuccessful && res.body() != null -> {
                val groups = (res.body()!!.items ?: emptyList()).mapIndexed { i, item ->
                    ReinforceGroup(
                        // items are one-to-one with the request, so fall back to the same index
                        skill = item.skill ?: missingSkills.getOrElse(i) { "" },
                        resources = (item.resources ?: emptyList()).mapIndexed { j, r ->
                            ReinforceResource(
                                id = r.resourceId?.takeIf { it.isNotBlank() } ?: "$i-$j",
                                type = r.type.orEmpty(),
                                title = r.title.orEmpty(),
                                provider = r.provider.orEmpty(),
                                url = r.url.orEmpty(),
                                deadline = r.deadline.orEmpty(),
                                note = r.note.orEmpty(),
                            )
                        },
                    )
                }
                Result.success(groups)
            }
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            res.code() in listOf(502, 504) -> Result.failure(Exception("AI 服務暫時無法回應，請稍後再試"))
            else -> Result.failure(Exception("載入補強建議失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
}