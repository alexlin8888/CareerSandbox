package com.careersandbox.app.data.remote

// Mirrors POST /ai/resume/customize on the Node API,
// which forwards to the AI service's /resume/customize.
data class CustomizeResumeRequest(
    val jobId: String,
)

data class CustomizedItemResponse(
    val text: String?,
    val matchedKeywords: List<String>?,
    val highlighted: Boolean?,
)

data class CustomizeResumeResponse(
    val jdKeywords: List<String>?,
    val coveredKeywords: List<String>?,
    val items: List<CustomizedItemResponse>?,
)