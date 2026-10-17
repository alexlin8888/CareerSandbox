package com.careersandbox.app.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.careersandbox.app.data.mock.JobTarget
import com.careersandbox.app.data.mock.MockResumeHierarchyProvider
import com.careersandbox.app.data.mock.ResumeHierarchyProvider
import com.careersandbox.app.data.mock.ResumeMaster
import com.careersandbox.app.data.mock.ResumeVersion
import com.careersandbox.app.data.mock.SubmissionStatus
import com.careersandbox.app.data.remote.ApiClient
import com.careersandbox.app.data.remote.CreateJobTargetRequest
import com.careersandbox.app.data.remote.CreateVersionRequest
import com.careersandbox.app.data.remote.JobTargetDto
import com.careersandbox.app.data.remote.ResumeApiService
import com.careersandbox.app.data.remote.ResumeVersionDto
import com.careersandbox.app.data.remote.UpdateVersionLabelRequest
import com.careersandbox.app.data.remote.UpdateVersionStatusRequest
import com.careersandbox.app.data.remote.ResumeItemRequest

/**
 * Real implementation of [ResumeHierarchyProvider], backed by the /resume routes on the
 * Node API (collections job_targets and resume_versions).
 *
 * Reads stay synchronous so the screens keep calling jobTargets() / master()
 * exactly as they did with the mock. The data lives in Compose snapshot state:
 * call [refresh] from a LaunchedEffect, and every screen reading jobTargets()
 * recomposes when it lands.
 *
 * Writes go to the server first and only touch the local list on success, so
 * the screen never shows a target that was not actually saved.
 */
object RemoteResumeHierarchyProvider : ResumeHierarchyProvider {

    private val api: ResumeApiService get() = ApiClient.resumeApi

    private val _targets = mutableStateListOf<JobTarget>()

    // Placeholder until the first refresh: the mock master already reads the
    // signed-in user's local profile, so the card is never blank.
    private var _master by mutableStateOf(MockResumeHierarchyProvider.master())

    var loading by mutableStateOf(false)
        private set

    override fun master(): ResumeMaster = _master

    override fun jobTargets(): List<JobTarget> = _targets

    /** Reload master and all targets (with their versions) from the server. */
    suspend fun refresh(): Result<Unit> {
        loading = true
        return try {
            val res = api.listTargets()
            if (!res.isSuccessful || res.body() == null) return httpError(res.code(), "讀取職缺")
            val fresh = res.body()!!.mapNotNull { it.toModel() }
            _targets.clear()
            _targets.addAll(fresh)

            // The master card is secondary; a failure here should not hide the targets.
            runCatching { api.master() }.getOrNull()?.body()?.let { m ->
                _master = ResumeMaster(
                    ownerName = m.ownerName.orEmpty(),
                    experienceCount = m.experienceCount ?: 0,
                    skills = m.skills ?: emptyList(),
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("無法連線到伺服器"))
        } finally {
            loading = false
        }
    }

    /**
     * Create a target in job_targets.
     * Typed in by the user: leave [jobId] empty and pass the pasted JD as [jdSnapshot].
     * Picked from the crawled jobs: pass that job's sourceId and a copy of its JD.
     */
    suspend fun addJobTarget(
        title: String,
        company: String,
        jdSnapshot: String = "",
        jobId: String = "",
        jdKeywords: List<String> = emptyList(),
    ): Result<JobTarget> = try {
        val res = api.createTarget(
            CreateJobTargetRequest(
                title = title.trim(),
                company = company.trim(),
                jdSnapshot = jdSnapshot.trim(),
                jobId = jobId,
                jdKeywords = jdKeywords,
            )
        )
        val created = res.body()?.toModel()
        if (res.isSuccessful && created != null) {
            // The server lists newest first, so keep the same order locally.
            _targets.add(0, created)
            Result.success(created)
        } else {
            httpError(res.code(), "建立職缺")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    /** Delete a target; the server also deletes every version under it. */
    suspend fun removeJobTarget(targetId: String): Result<Unit> = try {
        val res = api.deleteTarget(targetId)
        if (res.isSuccessful) {
            _targets.removeAll { it.id == targetId }
            Result.success(Unit)
        } else {
            httpError(res.code(), "刪除職缺")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    suspend fun updateVersionStatus(versionId: String, status: SubmissionStatus): Result<Unit> = try {
        val res = api.updateVersionStatus(versionId, UpdateVersionStatusRequest(status.name))
        val updated = res.body()?.toModel()
        if (res.isSuccessful && updated != null) {
            replaceVersion(versionId) { updated }
            Result.success(Unit)
        } else {
            httpError(res.code(), "更新狀態")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    /**
     * Rename a version. The label is applied locally from what we sent,
     * so this does not depend on the shape of the PATCH response body.
     */
    suspend fun renameVersion(versionId: String, label: String): Result<Unit> = try {
        val res = api.updateVersionLabel(versionId, UpdateVersionLabelRequest(label))
        if (res.isSuccessful) {
            replaceVersion(versionId) { it.copy(label = label) }
            Result.success(Unit)
        } else {
            httpError(res.code(), "重新命名")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    suspend fun removeVersion(versionId: String): Result<Unit> = try {
        val res = api.deleteVersion(versionId)
        if (res.isSuccessful) {
            for (i in _targets.indices) {
                val t = _targets[i]
                if (t.versions.any { it.id == versionId }) {
                    _targets[i] = t.copy(versions = t.versions.filterNot { it.id == versionId })
                    break
                }
            }
            Result.success(Unit)
        } else {
            httpError(res.code(), "刪除版本")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }

    /**
     * Copy a version as a new draft under the same target.
     * The API has no copy endpoint yet, so this creates a new version carrying
     * the note only; the customized items are not duplicated.
     */
    /**
     * Save a customization result as a new draft version under [targetId].
     * [items] is the full customized list in experience order, with [highlighted]
     * reflecting what the user kept (AI picks plus anything they pulled back).
     */
    suspend fun saveVersion(
        targetId: String,
        note: String,
        items: List<ResumeItemRequest>,
    ): Result<ResumeVersion> = try {
        val res = api.createVersion(targetId, CreateVersionRequest(note = note, items = items))
        val created = res.body()?.toModel()
        if (res.isSuccessful && created != null) {
            val i = _targets.indexOfFirst { it.id == targetId }
            if (i >= 0) _targets[i] = _targets[i].copy(versions = _targets[i].versions + created)
            Result.success(created)
        } else {
            httpError(res.code(), "儲存版本")
        }
    } catch (e: Exception) {
        Result.failure(Exception("無法連線到伺服器"))
    }
    suspend fun duplicateVersion(versionId: String): Result<Unit> {
        val target = _targets.firstOrNull { t -> t.versions.any { it.id == versionId } }
            ?: return Result.failure(Exception("找不到這個版本"))
        val source = target.versions.first { it.id == versionId }
        return try {
            val res = api.createVersion(target.id, CreateVersionRequest(note = source.note + "(複製)"))
            val created = res.body()?.toModel()
            if (res.isSuccessful && created != null) {
                val i = _targets.indexOfFirst { it.id == target.id }
                if (i >= 0) _targets[i] = _targets[i].copy(versions = _targets[i].versions + created)
                Result.success(Unit)
            } else {
                httpError(res.code(), "複製版本")
            }
        } catch (e: Exception) {
            Result.failure(Exception("無法連線到伺服器"))
        }
    }

    private fun replaceVersion(versionId: String, transform: (ResumeVersion) -> ResumeVersion) {
        for (i in _targets.indices) {
            val t = _targets[i]
            val vi = t.versions.indexOfFirst { it.id == versionId }
            if (vi >= 0) {
                val newVersions = t.versions.toMutableList()
                newVersions[vi] = transform(newVersions[vi])
                _targets[i] = t.copy(versions = newVersions)
                return
            }
        }
    }

    private fun <T> httpError(code: Int, action: String): Result<T> = Result.failure(
        Exception(
            when (code) {
                401 -> "登入已過期，請重新登入"
                404 -> "找不到這筆資料"
                422 -> "請填寫職位名稱與公司"
                else -> "${action}失敗（錯誤碼 $code）"
            }
        )
    )
}

// A target without an id cannot be edited or deleted later, so drop it.
private fun JobTargetDto.toModel(): JobTarget? {
    val targetId = id ?: return null
    return JobTarget(
        id = targetId,
        title = title.orEmpty(),
        company = company.orEmpty(),
        jdKeywords = jdKeywords ?: emptyList(),
        versions = (versions ?: emptyList()).mapNotNull { it.toModel() },
        jdSnippet = jdSnippet.orEmpty(),
        matchScore = matchScore,
        styleTag = styleTag.orEmpty(),
        requiredSkills = requiredSkills ?: emptyList(),
        createdAt = createdAt.orEmpty(),
    )
}
private fun ResumeVersionDto.toModel(): ResumeVersion? {
    val versionId = id ?: return null
    return ResumeVersion(
        id = versionId,
        label = label.orEmpty(),
        // Unknown values fall back to DRAFT instead of crashing the list.
        status = runCatching { SubmissionStatus.valueOf(status.orEmpty()) }.getOrDefault(SubmissionStatus.DRAFT),
        submittedDate = submittedDate,
        note = note.orEmpty(),
    )
}
