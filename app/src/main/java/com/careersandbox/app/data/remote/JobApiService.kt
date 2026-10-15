package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// GET /jobs/search on the Node API: keyword search over the crawled `jobs`
// collection. Public data, no auth header needed (the interceptor adds one anyway).
interface JobApiService {
    @GET("jobs/search")
    suspend fun search(
        @Query("q") q: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
    ): Response<JobSearchResponse>

    @GET("jobs/random")
    suspend fun random(@Query("n") n: Int = 10): Response<JobSearchResponse>
}

data class JobSearchResponse(
    val data: List<JobDto>?,
    val count: Int?,
    val pagination: Pagination?,
)

data class Pagination(
    val page: Int?,
    val limit: Int?,
    val total: Int?,
    val pages: Int?,
)

// Only the fields the picker shows or stores. Other crawled fields are ignored by Gson.
data class JobDto(
    val sourceId: String?,
    val title: String?,
    val company: String?,
    val location: String?,
    val industry: String?,
    val salaryRaw: String?,
    val description: String?,
    val conditionOther: String?,
    val requiredSkills: List<String>?,
    val jobCategory: List<String>?,
    val education: String?,
    val workExp: String?,
    val tags: List<String>?,
)