package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CareerApiService {
    // Career recommendation. Runs a vector search over the knowledge base plus
    // one or two LLM calls, so it currently takes around 40 seconds — far past
    // OkHttp's 10s default read timeout. This needs a client with a raised
    // readTimeout (see ApiClient), or every call fails with SocketTimeoutException.
    @POST("career/recommend")
    suspend fun recommend(
        @Body body: CareerRecommendRequest,
    ): Response<CareerRecommendResponse>

    // Cheap reachability check for the AI service, answers in well under a second.
    // Worth calling when the recommendation screen opens: if the service is down,
    // say so immediately instead of leaving the user watching a spinner until the
    // real request times out a minute later.
    @GET("career/health")
    suspend fun aiHealth(): Response<AiHealthResponse>
}
