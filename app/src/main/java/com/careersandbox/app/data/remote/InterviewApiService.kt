package com.careersandbox.app.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InterviewApiService {
    // @Multipart：告訴 Retrofit「這支 API 要送檔案，不是純 JSON」
    // MultipartBody.Part：Retrofit 專門用來裝「一份檔案」的容器
    @Multipart
    @POST("interview/transcribe")
    suspend fun transcribe(
        @Part audio: MultipartBody.Part,
    ): Response<TranscribeResponse>
}

// 後端轉錄完之後回傳的格式，先只放文字；
// 之後如果要跟模型組確認的「segments/時間戳記」要加進來，就是在這裡加欄位
data class TranscribeResponse(
    val text: String,
)