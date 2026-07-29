package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ExperienceApiService {
    @GET("experiences")
    suspend fun list(): Response<List<ExperienceResponse>>

    @GET("experiences/{id}")
    suspend fun get(@Path("id") id: String): Response<ExperienceResponse>

    @POST("experiences")
    suspend fun create(@Body body: CreateExperienceRequest): Response<ExperienceResponse>

    @PATCH("experiences/{id}")
    suspend fun update(@Path("id") id: String, @Body body: CreateExperienceRequest): Response<ExperienceResponse>

    @DELETE("experiences/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}