package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompetitionApiService {

    // Open competitions, nearest deadline first. A null category is left out of
    // the query string, which means every category.
    @GET("competitions")
    suspend fun list(
        @Query("category") category: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<CompetitionPageResponse>

    // Ranked for the signed-in user by interests, skills and department.
    @GET("competitions/recommended")
    suspend fun recommended(@Query("limit") limit: Int): Response<CompetitionListResponse>

    @GET("competitions/{id}")
    suspend fun get(@Path("id") id: String): Response<CompetitionDto>
}