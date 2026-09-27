package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface InterviewAiApiService {
    @POST("interviews")
    suspend fun startInterview(@Body body: StartInterviewRequest): Response<StartInterviewResponse>

    // 對應合約的 POST /interviews/{sessionId}/turns（每輪答題）
    @POST("interviews/{sessionId}/turns")
    suspend fun submitTurn(
        @Path("sessionId") sessionId: String,
        @Body body: TurnRequest,
    ): Response<TurnResponse>

    // 對應合約的 POST /interviews/{sessionId}/report（面試結束，產出報告）
    @POST("interviews/{sessionId}/report")
    suspend fun getReport(
        @Path("sessionId") sessionId: String,
        @Body body: ReportRequest,
    ): Response<ReportResponse>
}