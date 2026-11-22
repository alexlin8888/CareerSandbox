package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.InterviewSessionApiService
import com.careersandbox.app.data.remote.InterviewSessionDetailDto
import com.careersandbox.app.data.remote.InterviewSessionSummaryDto
import com.careersandbox.app.data.remote.SaveInterviewSessionRequest

interface InterviewSessionRepository {
    suspend fun save(request: SaveInterviewSessionRequest): Result<Unit>
    suspend fun list(): Result<List<InterviewSessionSummaryDto>>
    suspend fun get(id: String): Result<InterviewSessionDetailDto>
}

class RemoteInterviewSessionRepository(
    private val api: InterviewSessionApiService = ApiClient.interviewSessionApi
) : InterviewSessionRepository {

    override suspend fun save(request: SaveInterviewSessionRequest): Result<Unit> = try {
        val res = api.save(request)
        when {
            res.isSuccessful -> Result.success(Unit)
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            res.code() == 409 -> Result.failure(Exception("這場面試的編號已被使用"))
            res.code() == 422 -> Result.failure(Exception("面試資料格式不正確"))
            else -> Result.failure(Exception("儲存失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun list(): Result<List<InterviewSessionSummaryDto>> = try {
        val res = api.list()
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("讀取失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    override suspend fun get(id: String): Result<InterviewSessionDetailDto> = try {
        val res = api.get(id)
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
            res.code() == 404 -> Result.failure(Exception("找不到這筆面試紀錄"))
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("讀取失敗（錯誤碼 ${res.code()}）"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
}