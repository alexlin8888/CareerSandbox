package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface UserApiService {
    @GET("users/me")
    suspend fun getMe(): Response<UserProfileResponse>
}