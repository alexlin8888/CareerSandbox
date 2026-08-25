package com.careersandbox.app.data.remote

import com.careersandbox.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 = your computer's localhost, as seen from the Android emulator
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Attaches "Authorization: Bearer <token>" to every request, when we have one
    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val experienceApi: ExperienceApiService by lazy { retrofit.create(ExperienceApiService::class.java) }

    val experienceChatApi: ExperienceChatApiService by lazy { retrofit.create(ExperienceChatApiService::class.java) }

}