package com.careersandbox.app.data.remote

// POST /ai/skills/reinforce  (Node api -> AI service POST /skills/reinforce)
// userId / school are filled in by the Node api, so the app only sends missingSkills.
data class ReinforceRequest(
    val missingSkills: List<String>,
)

data class ReinforceResponse(
    val items: List<ReinforceItemResponse>?,
)

data class ReinforceItemResponse(
    val skill: String?,
    val resources: List<ReinforceResourceResponse>?,
)

data class ReinforceResourceResponse(
    val resourceId: String?,
    val type: String?,        // "course" | "competition"
    val title: String?,
    val provider: String?,
    val url: String?,         // may be empty
    val deadline: String?,    // competitions: YYYY-MM-DD, courses: ""
    val note: String?,        // may be empty
)