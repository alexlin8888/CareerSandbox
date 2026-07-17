package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.UserApiService
import com.careersandbox.app.data.remote.UserProfileResponse

interface UserRepository {
    suspend fun getMe(): Result<UserProfileResponse>
}

class RemoteUserRepository(
    private val api: UserApiService = ApiClient.userApi
) : UserRepository {
    override suspend fun getMe(): Result<UserProfileResponse> {
        return try {
            val res = api.getMe()
            when {
                res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
                res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
                else -> Result.failure(Exception("讀取資料失敗（錯誤碼 ${res.code()}）"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("無法連線到伺服器"))
        }
    }
}