package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExperienceChatApiService {
    @POST("experience-chat/turn")
    suspend fun turn(@Body body: ChatTurnRequest): Response<ChatTurnResponse>
}