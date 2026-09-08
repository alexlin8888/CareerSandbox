package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.InterviewApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// 轉錄結果：除了完整文字，額外把 Whisper 的 segments 整理成模型組要的兩條陣列
// （segmentTexts 對應 Turn.answerSegments，segmentStartsMs 對應 Turn.segmentStartsMs）
data class TranscriptionResult(
    val text: String,
    val segmentTexts: List<String>,
    val segmentStartsMs: List<Long>,
)

interface TranscribeRepository {
    suspend fun transcribe(audioFile: File): Result<TranscriptionResult>
}

class RemoteTranscribeRepository(
    private val api: InterviewApiService = ApiClient.interviewApi
) : TranscribeRepository {

    override suspend fun transcribe(audioFile: File): Result<TranscriptionResult> = try {
        val requestBody = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)

        val res = api.transcribe(part)
        when {
            res.isSuccessful && res.body() != null -> {
                val body = res.body()!!
                Result.success(
                    TranscriptionResult(
                        text = body.text,
                        segmentTexts = body.segments.map { it.text },
                        // 後端給的 start 是秒（可能有小數），轉成毫秒的 Long 才能跟
                        // 模型組要的 segmentStartsMs 格式對齊
                        segmentStartsMs = body.segments.map { (it.start * 1000).toLong() },
                    )
                )
            }
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("轉錄失敗，錯誤碼 ${res.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器，請確認網路連線"))
    }
}