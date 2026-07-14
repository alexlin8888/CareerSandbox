package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.AuthApiService
import com.careersandbox.app.data.remote.LoginRequest
import com.careersandbox.app.data.remote.LoginResponse
import com.careersandbox.app.data.remote.RegisterRequest
import com.careersandbox.app.data.remote.RegisterResponse

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
    suspend fun login(email: String, password: String): Result<LoginResponse>
}

class RemoteAuthRepository(
    private val api: AuthApiService = ApiClient.authApi
) : AuthRepository {

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val res = api.register(request)
            when {
                res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
                res.code() == 409 -> Result.failure(Exception("此 Email 已被註冊"))
                else -> Result.failure(Exception("註冊失敗（錯誤碼 ${res.code()}）"))
            }
        } catch (e: Exception) {
            // Server not running, wrong URL, no network, etc.
            Result.failure(Exception("無法連線到伺服器，請確認後端已啟動"))
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val res = api.login(LoginRequest(email, password))
            when {
                res.isSuccessful && res.body() != null -> Result.success(res.body()!!)
                res.code() == 401 -> Result.failure(Exception("Email 或密碼錯誤"))
                else -> Result.failure(Exception("登入失敗（錯誤碼 ${res.code()}）"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("無法連線到伺服器，請確認後端已啟動"))
        }
    }
}