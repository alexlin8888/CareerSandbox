package com.careersandbox.app.data.remote

data class CreateExperienceRequest(
    val title: String,
    val category: String,
    val period: String = "",
    val role: String = "",
    val action: String = "",
    val result: String = "",
    val learning: String = "",
    val tags: List<String> = emptyList(),
)

data class ExperienceResponse(
    val id: String,
    val title: String,
    val category: String,
    val period: String,
    val role: String,
    val action: String,
    val result: String,
    val learning: String,
    val description: String,
    val tags: List<String>,
)