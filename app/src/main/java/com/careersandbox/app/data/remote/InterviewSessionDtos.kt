package com.careersandbox.app.data.remote

import com.google.gson.JsonObject

// POST /interview-sessions body. The server stores `report` as-is and reads
// config.targetJobTitle for the history list.
data class SaveInterviewSessionRequest(
    val sessionId: String,
    val mode: String,
    val report: ReportResponse,
    val config: Map<String, Any?> = emptyMap(),
    val participants: List<PersonaDto> = emptyList(),
    val turns: List<TurnDto> = emptyList(),
    val groupSays: List<UtteranceDto> = emptyList(),
    val jdSkills: List<String> = emptyList(),
    val openingIntro: String = "",
    val resumeVersionId: String? = null,
    val overallScore: Double? = null,
    val scoreBasis: Int? = null,
)

// GET /interview-sessions: one row per saved interview, newest first.
data class InterviewSessionSummaryDto(
    val id: String = "",
    val mode: String? = null,
    val jobTitle: String? = null,
    val score: Double? = null,
    val createdAt: String? = null,
)

// GET /interview-sessions/{id}: the full record.
data class InterviewSessionDetailDto(
    val id: String = "",
    val mode: String? = null,
    val score: Double? = null,
    val scoreBasis: Int? = null,
    val config: JsonObject? = null,
    val participants: List<PersonaDto>? = null,
    val turns: List<TurnDto>? = null,
    val groupSays: List<UtteranceDto>? = null,
    val jdSkills: List<String>? = null,
    val openingIntro: String? = null,
    val report: ReportResponse? = null,
    val createdAt: String? = null,
)