package com.careersandbox.app.data.remote

// Mirrors careersandbox-api routes/resumeHierarchy.js (/resume/*).
// Response fields are nullable because Gson leaves missing JSON fields null
// even when the Kotlin type says otherwise; the repository fills defaults.

data class ResumeMasterDto(
    val ownerName: String?,
    val experienceCount: Int?,
    val skills: List<String>?,
)

data class ResumeVersionDto(
    val id: String?,
    val label: String?,
    val status: String?,
    val submittedDate: String?,
    val note: String?,
)

data class JobTargetDto(
    val id: String?,
    val title: String?,
    val company: String?,
    val jdKeywords: List<String>?,
    val versions: List<ResumeVersionDto>?,
)

// POST /resume/targets
// Typed-in target: jobId stays empty and the pasted JD goes into jdSnapshot.
// Target picked from the crawled jobs: jobId is that job's sourceId and
// jdSnapshot is a copy of its JD, so the target survives the posting changing.
data class CreateJobTargetRequest(
    val title: String,
    val company: String,
    val jdSnapshot: String = "",
    val jobId: String = "",
    val jdKeywords: List<String> = emptyList(),
)

// POST /resume/targets/{id}/versions — label is auto-assigned (版本 A/B/C) when null
data class CreateVersionRequest(
    val label: String? = null,
    val note: String? = null,
)

// PATCH /resume/versions/{vid}/status
data class UpdateVersionStatusRequest(
    val status: String,
)
