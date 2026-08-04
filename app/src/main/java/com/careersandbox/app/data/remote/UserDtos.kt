package com.careersandbox.app.data.remote

data class LanguageDto(
    val language: String,
    val level: String,
)

data class UserProfileResponse(
    val userId: String,
    val name: String,
    val email: String,
    val school: String = "",
    val department: String = "",
    val year: String = "",
    val phone: String = "",
    val bio: String = "",
    val linkedin: String = "",
    val github: String = "",
    val portfolio: String = "",
    val interests: List<String> = emptyList(),
    val skillsHave: List<String> = emptyList(),
    val skillsWant: List<String> = emptyList(),
    val languages: List<LanguageDto> = emptyList(),
)

// PATCH body — every field optional; Gson omits nulls, so only
// the fields you actually set get sent (true partial update)
data class UpdateProfileRequest(
    val name: String? = null,
    val school: String? = null,
    val department: String? = null,
    val year: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val linkedin: String? = null,
    val github: String? = null,
    val portfolio: String? = null,
    val interests: List<String>? = null,
    val skillsHave: List<String>? = null,
    val skillsWant: List<String>? = null,
    val languages: List<LanguageDto>? = null,
)