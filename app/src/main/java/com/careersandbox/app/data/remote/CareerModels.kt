package com.careersandbox.app.data.remote

// Request body for POST /career/recommend.
// Only the free-text goal is sent; the backend reads userId from the token and
// pulls the experience list from the database itself.
data class CareerRecommendRequest(
    val query: String,
)

// One recommended career direction.
// Every field arrives display-ready from the backend — no formatting needed.
data class CareerRecommendation(
    val id: String,
    val title: String,
    val subtitleEn: String = "",
    val shortSubtitle: String = "",
    val salary: String = "",
    val openings: String = "",
    val matchScore: Int = 0,
    val missingSkills: List<String> = emptyList(),
    val category: String = "",
    val isAcademic: Boolean = false,
    val academicNote: String = "",
)

data class CareerRecommendResponse(
    val recommendations: List<CareerRecommendation> = emptyList(),

    // How many experiences the backend found for this user.
    val experienceCount: Int = 0,

    // False when the user has no experiences yet: the AI service answers from a
    // fixture in that case, so the results look real but are not this person's.
    // Prompt them to add experiences rather than showing canned picks as theirs.
    val isPersonalized: Boolean = false,
)

// GET /career/health — "up" | "down" | "error"
data class AiHealthResponse(
    val aiService: String = "down",
    val url: String = "",
)
