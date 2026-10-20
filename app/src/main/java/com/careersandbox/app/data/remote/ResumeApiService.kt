package com.careersandbox.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ResumeApiService {
    @GET("resume/master")
    suspend fun master(): Response<ResumeMasterDto>

    @GET("resume/targets")
    suspend fun listTargets(): Response<List<JobTargetDto>>

    @POST("resume/targets")
    suspend fun createTarget(@Body body: CreateJobTargetRequest): Response<JobTargetDto>

    @DELETE("resume/targets/{id}")
    suspend fun deleteTarget(@Path("id") id: String): Response<Unit>

    @POST("resume/targets/{id}/versions")
    suspend fun createVersion(
        @Path("id") targetId: String,
        @Body body: CreateVersionRequest,
    ): Response<ResumeVersionDto>

    @POST("resume/targets/{id}/fit")
    suspend fun fitTarget(@Path("id") targetId: String): Response<FitTargetResponse>

    @PATCH("resume/versions/{vid}/status")
    suspend fun updateVersionStatus(
        @Path("vid") versionId: String,
        @Body body: UpdateVersionStatusRequest,
    ): Response<ResumeVersionDto>

    @DELETE("resume/versions/{vid}")
    suspend fun deleteVersion(@Path("vid") versionId: String): Response<Unit>
}
