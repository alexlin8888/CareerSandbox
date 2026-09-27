package com.careersandbox.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/* =====================================================================
   指向模型組面試 AI 服務（獨立的 Python/FastAPI 服務，跑在 8001）的連線設定。
   跟 ApiClient.kt（指向你們自己的 careersandbox-api，8000）完全獨立、互不影響。

   跟 ApiClient.kt 的差異：
   - 不需要 authInterceptor：這支服務不是你們自己的後端，不用帶 Bearer Token
     （之前 Postman 測試時也證實了，不帶 Authorization 一樣能打通）
   - BASE_URL 目前是本機位址，之後若模型組把服務部署到真正的雲端主機，
     只需要改這裡一行，不影響任何其他代碼
   ===================================================================== */
object InterviewAiApiClient {
    // 10.0.2.2 = 從模擬器角度看你電腦的 localhost；實機測試時要換成區網 IP，
    // 做法跟 ApiClient.kt 的 BASE_URL 完全一樣
    private const val BASE_URL = "http://10.0.2.2:8001/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val interviewAiApi: InterviewAiApiService by lazy { retrofit.create(InterviewAiApiService::class.java) }
}