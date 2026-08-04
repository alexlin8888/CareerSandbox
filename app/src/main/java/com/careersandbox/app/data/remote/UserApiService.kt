package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {
    @GET("users/me")
    suspend fun getMe(): Response<UserProfileResponse>

    @PATCH("users/me")
    suspend fun updateMe(@Body body: UpdateProfileRequest): Response<UserProfileResponse>
}