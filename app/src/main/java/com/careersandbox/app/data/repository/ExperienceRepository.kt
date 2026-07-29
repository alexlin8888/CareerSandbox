package com.careersandbox.app.data.repository

import com.careersandbox.app.data.model.Experience
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.CreateExperienceRequest
import com.careersandbox.app.data.remote.ExperienceApiService
import com.careersandbox.app.data.remote.ExperienceResponse

interface ExperienceRepository {
    suspend fun list(): Result<List<Experience>>
    suspend fun get(id: String): Result<ExperienceResponse>
    suspend fun create(request: CreateExperienceRequest): Result<Experience>
    suspend fun update(id: String, request: CreateExperienceRequest): Result<Experience>
    suspend fun delete(id: String): Result<Unit>
}

class RemoteExperienceRepository(
    private val api: ExperienceApiService = ApiClient.experienceApi
) : ExperienceRepository {

    override suspend fun list(): Result<List<Experience>> = try {
        val res = api.list()
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!.map { it.toModel() })
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("讀取失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    // Returns the raw DTO — the edit form needs every field (role, action, learning...)
    override suspend fun get(id: String): Result<ExperienceResponse> = try {
        val res = api.get(id)
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
            res.code() == 404 -> Result.failure(Exception("找不到這筆經歷"))
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("讀取失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun create(request: CreateExperienceRequest): Result<Experience> = try {
        val res = api.create(request)
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!.toModel())
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            res.code() == 400 -> Result.failure(Exception("標題和類別是必填的"))
            else -> Result.failure(Exception("儲存失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun update(id: String, request: CreateExperienceRequest): Result<Experience> = try {
        val res = api.update(id, request)
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!.toModel())
            res.code() == 404 -> Result.failure(Exception("找不到這筆經歷"))
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("更新失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun delete(id: String): Result<Unit> = try {
        val res = api.delete(id)
        if (res.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("刪除失敗（錯誤碼 ${res.code()}）"))
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
}

// Map network DTO → the app's existing display model (period → timeRange)
private fun ExperienceResponse.toModel() = Experience(
    id = id,
    title = title,
    category = category,
    timeRange = period,
    description = description,
    tags = tags,
)