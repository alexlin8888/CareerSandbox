package com.careersandbox.app.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InterviewSessionApiService {
    // The response body is ignored on purpose: a parse failure on the echoed record
    // must not make a successful save look like a failure.
    @POST("interview-sessions")
    suspend fun save(@Body body: SaveInterviewSessionRequest): Response<ResponseBody>

    @GET("interview-sessions")
    suspend fun list(): Response<List<InterviewSessionSummaryDto>>

    @GET("interview-sessions/{id}")
    suspend fun get(@Path("id") id: String): Response<InterviewSessionDetailDto>
}