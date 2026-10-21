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
    val jdSnippet: String?,
    val matchScore: Int?,
    val styleTag: String?,
    val requiredSkills: List<String>?,
    val createdAt: String?,
    val salary: String?
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

data class CreateVersionRequest(
    val label: String? = null,
    val note: String = "",
    val items: List<ResumeItemRequest> = emptyList(),
)

// Mirrors resumeItemSchema on the server
data class ResumeItemRequest(
    val experienceId: String?,
    val text: String,
    val matchedKeywords: List<String>,
    val highlighted: Boolean,
)

// PATCH /resume/versions/{vid}/status
data class UpdateVersionStatusRequest(
    val status: String,
)

// Renames a version; the server caps the label at 100 characters.
data class UpdateVersionLabelRequest(
    val label: String,
)

// GET /resume-versions/{id}: the saved customization. Gson ignores the other fields.
data class SavedResumeItemDto(
    val experienceId: String?,
    val text: String?,
    val matchedKeywords: List<String>?,
    val highlighted: Boolean?,
)

data class SavedResumeVersionDto(
    val label: String?,
    val items: List<SavedResumeItemDto>?,
)

// PATCH /resume-versions/{id} with { items }: replaces the whole list.
data class UpdateVersionItemsRequest(
    val items: List<ResumeItemRequest>,
)

data class FitTargetResponse(
    val target: JobTargetDto?,
    val requiredSkills: List<String>?,
    val tags: List<String>?,
)