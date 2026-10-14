package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApiService {
    @POST("ai/resume/customize")
    suspend fun customizeResume(@Body body: CustomizeResumeRequest): Response<CustomizeResumeResponse>
}