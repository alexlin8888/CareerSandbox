package com.careersandbox.app.data.repository

import com.careersandbox.app.data.model.Competition
import com.careersandbox.app.data.model.CompetitionCategory
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.CompetitionApiService
import com.careersandbox.app.data.remote.CompetitionDto
import kotlinx.coroutines.CancellationException
import java.util.concurrent.ConcurrentHashMap

/** One page of the competition list. */
data class CompetitionPage(
    val items: List<Competition>,
    val total: Int,
    val hasMore: Boolean,
)

interface CompetitionRepository {
    /** The home "推薦競賽" row, ranked for the signed-in user. */
    suspend fun recommended(limit: Int = 5): Result<List<Competition>>

    /** Open competitions, nearest deadline first; category null = every category. */
    suspend fun page(category: CompetitionCategory?, offset: Int, limit: Int): Result<CompetitionPage>

    /** One competition; served from memory when the home row or the list already loaded it. */
    suspend fun get(id: String): Result<Competition>

    fun cached(id: String): Competition?
}

/**
 * A singleton so the detail screen can reuse what the home row and the list
 * already fetched, and the home row can show its last result while refreshing.
 */
object RemoteCompetitionRepository : CompetitionRepository {

    private val api: CompetitionApiService get() = ApiClient.competitionApi
    private val cache = ConcurrentHashMap<String, Competition>()

    /** Last successful recommended() result; empty until the first one. */
    @Volatile
    var lastRecommended: List<Competition> = emptyList()
        private set

    override fun cached(id: String): Competition? = cache[id]

    override suspend fun recommended(limit: Int): Result<List<Competition>> = try {
        val res = api.recommended(limit)
        val body = res.body()
        if (res.isSuccessful && body != null) {
            val list = cacheAll(body.items)
            lastRecommended = list
            Result.success(list)
        } else {
            httpError(res.code(), "載入推薦競賽")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun page(
        category: CompetitionCategory?,
        offset: Int,
        limit: Int,
    ): Result<CompetitionPage> = try {
        val res = api.list(category?.name, offset, limit)
        val body = res.body()
        if (res.isSuccessful && body != null) {
            val items = cacheAll(body.items)
            Result.success(
                CompetitionPage(
                    items = items,
                    total = body.total ?: (offset + items.size),
                    hasMore = body.hasMore ?: false,
                )
            )
        } else {
            httpError(res.code(), "載入競賽")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun get(id: String): Result<Competition> {
        cache[id]?.let { return Result.success(it) }
        return try {
            val res = api.get(id)
            val comp = res.body()?.toModel()
            when {
                res.isSuccessful && comp != null -> {
                    cache[comp.id] = comp
                    Result.success(comp)
                }
                res.code() == 404 -> Result.failure(Exception("找不到這個競賽"))
                else -> httpError(res.code(), "載入競賽")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("無法連線到伺服器"))
        }
    }

    // Maps to the UI model and remembers each item for the detail screen.
    private fun cacheAll(dtos: List<CompetitionDto>?): List<Competition> =
        (dtos ?: emptyList()).mapNotNull { it.toModel() }.onEach { cache[it.id] = it }

    private fun <T> httpError(code: Int, action: String): Result<T> = Result.failure(
        Exception(if (code == 401) "登入已過期，請重新登入" else "${action}失敗（錯誤碼 $code）")
    )
}

// Rows without an id or a title are dropped rather than shown half empty.
private fun CompetitionDto.toModel(): Competition? {
    val id = id?.takeIf { it.isNotBlank() } ?: return null
    val title = title?.takeIf { it.isNotBlank() } ?: return null
    return Competition(
        id = id,
        title = title,
        organizer = organizer.orEmpty(),
        // Every row in the database has one of the five; anything else shows as 黑客松
        // (the largest group) instead of failing the whole page.
        category = CompetitionCategory.values().firstOrNull { it.name == category }
            ?: CompetitionCategory.HACKATHON,
        deadline = deadline.orEmpty(),
        teamSize = teamSize?.takeIf { it.isNotBlank() } ?: "依競賽辦法",
        prize = prize?.takeIf { it.isNotBlank() } ?: "依競賽辦法",
        tags = tags ?: emptyList(),
        coverColor = coverColor?.takeIf { it.isNotBlank() } ?: "orange",
        coverImageUrl = coverImageUrl.orEmpty(),
    )
}