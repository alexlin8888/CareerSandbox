package com.careersandbox.app.data.repository

import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.InterviewApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

interface TranscribeRepository {
    suspend fun transcribe(audioFile: File): Result<String>
}

class RemoteTranscribeRepository(
    private val api: InterviewApiService = ApiClient.interviewApi
) : TranscribeRepository {

    override suspend fun transcribe(audioFile: File): Result<String> = try {
        // 把本機的檔案包成 Retrofit 看得懂的「檔案零件」：
        // asRequestBody(...) 先告訴系統這個檔案的類型是音檔
        // MultipartBody.Part.createFormData("audio", ...) 再包成一份表單欄位，
        // "audio" 這個名字要跟後端 multer 那邊設定的欄位名一致（下一步做後端時會對到）
        val requestBody = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("audio", audioFile.name, requestBody)

        val res = api.transcribe(part)
        when {
            res.isSuccessful && res.body() != null -> Result.success(res.body()!!.text)
            res.code() == 401 -> Result.failure(Exception("登入已過期，請重新登入"))
            else -> Result.failure(Exception("轉錄失敗，錯誤碼 ${res.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器，請確認網路連線"))
    }
}