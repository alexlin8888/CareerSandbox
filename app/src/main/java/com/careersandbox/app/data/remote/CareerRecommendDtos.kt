package com.careersandbox.app.data.remote

// POST /ai/career/recommend  (Node api -> AI service POST /career/recommend)
// Node fills in userId and the user's experiences; an empty query is replaced by a
// default description there, because the AI service requires a non-empty query.
data class CareerRecommendRequest(
    val query: String = "",
)

data class CareerRecommendResponse(
    val recommendations: List<CareerRecDto>?,
)

data class CareerRecDto(
    val id: String?,
    val title: String?,
    val subtitleEn: String?,
    val shortSubtitle: String?,
    val salary: String?,
    val openings: String?,
    val matchScore: Int?,
    val missingSkills: List<String>?,
    val coveredSkills: List<String>?,   // only after the AI service adds it; null until then
    val category: String?,
    val isAcademic: Boolean?,
    val academicNote: String?,
)