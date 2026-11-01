package com.careersandbox.app.data.remote

// Responses of GET /competitions, /competitions/recommended and /competitions/{id}.
// Every field is nullable: Gson ignores Kotlin defaults, so a missing field
// arrives as null and is filled in by the repository.

data class CompetitionDto(
    val id: String?,              // sourceId, stable across re-crawls
    val title: String?,
    val organizer: String?,
    val category: String?,        // CASE / HACKATHON / PITCH / DESIGN / RESEARCH
    val deadline: String?,        // "2026/11/04"
    val teamSize: String?,
    val prize: String?,
    val tags: List<String>?,
    val skills: List<String>?,
    val coverColor: String?,
    val coverImageUrl: String?,   // absolute; local covers already carry the server address
    val sourceUrl: String?,
)

/** One page of the list: total counts every open competition in the category. */
data class CompetitionPageResponse(
    val items: List<CompetitionDto>?,
    val total: Int?,
    val offset: Int?,
    val hasMore: Boolean?,
)

data class CompetitionListResponse(
    val items: List<CompetitionDto>?,
)