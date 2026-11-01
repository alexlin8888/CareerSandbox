package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.AiApiService
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.CareerRecommendRequest
import kotlinx.coroutines.CancellationException

/** One recommended career direction, already in display form. */
data class CareerRecommendation(
    val id: String,
    val title: String,
    val subtitleEn: String,
    val shortSubtitle: String,
    val salary: String,
    val openings: String,
    val matchScore: Int,
    val missingSkills: List<String>,
    val coveredSkills: List<String>,
    val category: String,        // kept for later use, not shown yet
    val isAcademic: Boolean,     // kept for later use, not shown yet
    val academicNote: String,    // kept for later use, not shown yet
)

interface CareerRecommendRepository {
    suspend fun recommend(query: String = ""): Result<List<CareerRecommendation>>
}

class RemoteCareerRecommendRepository(
    private val api: AiApiService = ApiClient.aiApi
) : CareerRecommendRepository {

    override suspend fun recommend(query: String): Result<List<CareerRecommendation>> = try {
        val res = api.recommendCareers(CareerRecommendRequest(query))
        when {
            res.isSuccessful && res.body() != null -> Result.success(
                (res.body()!!.recommendations ?: emptyList())
                    .filter { !it.id.isNullOrBlank() && !it.title.isNullOrBlank() }
                    .map {
                        CareerRecommendation(
                            id = it.id!!,
                            title = it.title!!,
                            subtitleEn = it.subtitleEn.orEmpty(),
                            shortSubtitle = it.shortSubtitle.orEmpty(),
                            salary = it.salary.orEmpty(),
                            openings = it.openings.orEmpty(),
                            matchScore = (it.matchScore ?: 0).coerceIn(0, 100),
                            missingSkills = it.missingSkills ?: emptyList(),
                            coveredSkills = it.coveredSkills ?: emptyList(),
                            category = it.category.orEmpty(),
                            isAcademic = it.isAcademic ?: false,
                            academicNote = it.academicNote.orEmpty(),
                        )
                    }
            )
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            res.code() in listOf(502, 504) -> Result.failure(Exception("AI 服務暫時無法回應，請稍後再試"))
            else -> Result.failure(Exception("載入推薦失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
}