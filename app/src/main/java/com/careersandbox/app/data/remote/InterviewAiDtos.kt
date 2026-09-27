package com.careersandbox.app.data.remote

/* =====================================================================
   對應 CareerSandboxInterview 的 app/schemas/interview.py。
   Python 那邊用 alias_generator=to_camel，JSON 欄位是 camelCase，
   剛好跟 Kotlin 的命名習慣一致，這裡的欄位名稱不用另外用
   @SerializedName 轉換，直接跟 Python 的欄位名一一對應即可。
   ===================================================================== */

// 對應 InterviewContext —— 面試設定，這次先建好格式，
// 之後串進畫面時再從 InterviewConfig 轉換成這個格式送出去
data class InterviewContextDto(
    val round: String = "初試",
    val transcriptionEngine: String = "api",
    val language: String = "中文",
    val type: String = "行為",
    val difficulty: String = "中等",
    val groupInterviewers: Int = 1,
    val groupSize: Int = 4,
    val groupRole: String = "一般應徵者",
    val customRole: String = "",
    val customCompany: String = "",
    val customSeniority: String = "新鮮人",
    val customIndustry: String = "",
    val customJd: String = "",
)

// 對應 ExperienceDTO —— 使用者的一筆經歷
data class ExperienceDto(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val period: String = "",
    val role: String = "",
    val action: String = "",
    val result: String = "",
    val learning: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
)

// 對應 Persona —— 一位面試場上的角色（panel 的主管、group 的 AI 同儕）
data class PersonaDto(
    val id: String,
    val displayName: String,
    val role: String = "interviewer",
    val blurb: String = "",
)

// 對應 StartInterviewRequest
data class StartInterviewRequest(
    val mode: String, // "single" / "panel" / "group"
    val context: InterviewContextDto = InterviewContextDto(),
    val experiences: List<ExperienceDto> = emptyList(),
)

// 對應 StartInterviewResponse
data class StartInterviewResponse(
    val sessionId: String,
    val mode: String,
    val openingQuestion: String,
    val openingSpeaker: String = "",
    val openingTopic: String = "",
    val personas: List<PersonaDto> = emptyList(),
    val interruptLines: List<String> = emptyList(),
    val interruptCap: Int = 0,
    val fallbackProbes: List<String> = emptyList(),
    val notices: List<String> = emptyList(),
)

// 對應 TurnRequest —— 每答完一題，送出這一輪的內容
data class TurnRequest(
    val answer: String,
    val followUpIdx: Int = 0,
    val question: String = "",
    val fallback: List<String> = emptyList(),
    val mode: String? = null,
    val inputMode: String = "unknown", // "voice" / "typed" / "unknown"
    val endedBy: String = "unknown",   // "user" / "timeout" / "unknown"
    val context: InterviewContextDto = InterviewContextDto(),
    val spokenBy: List<String> = emptyList(),
    val askedTopics: List<String> = emptyList(),
)

// 對應 TurnResponse —— 後端回傳這輪的結果跟下一題
data class TurnResponse(
    val speaker: String = "",
    val nextQuestion: String,
    val reaction: String = "",
    val isFollowUp: Boolean = false,
    val shouldAdvance: Boolean = false,
    val topic: String = "",
    val notices: List<String> = emptyList(),
)

// 對應 UtteranceDTO —— 群面的一則發言（這次個人面試會送空陣列，但欄位型別要存在）
data class UtteranceDto(
    val speaker: String = "user",
    val content: String = "",
    val isUser: Boolean = false,
    val segments: List<String> = emptyList(),
    val segmentStartsMs: List<Long> = emptyList(),
    val inputMode: String = "unknown",
    val startMs: Long = 0,
    val endMs: Long = 0,
)

// 對應 TurnDTO —— 報告請求裡「整場逐字稿」用的格式，注意這跟 TurnRequest 不是同一個東西
// TurnRequest 是「每答一題」送的；TurnDTO 是「面試結束後，整場逐字稿」送的
data class TurnDto(
    val question: String = "",
    val answer: String = "",
    val inputMode: String = "unknown",
    val answerSegments: List<String> = emptyList(),
    val segmentStartsMs: List<Long> = emptyList(),
    val endedBy: String = "unknown",
)

// 對應 ReportRequest
data class ReportRequest(
    val mode: String,
    val context: InterviewContextDto = InterviewContextDto(),
    val experiences: List<ExperienceDto> = emptyList(),
    val turns: List<TurnDto> = emptyList(),
    val groupSays: List<UtteranceDto> = emptyList(),
)

// 對應 ProsodyItem
data class ProsodyItemDto(
    val label: String,
    val value: String,
)

// 對應 FaceDimensionDTO
data class FaceDimensionDto(
    val letter: String,
    val name: String,
    val score: Int,
    val verdict: String,
    val points: List<String> = emptyList(),
    val prosody: List<ProsodyItemDto>? = null,
)

// 對應 SubScoreDTO
data class SubScoreDto(
    val name: String,
    val score: Int,
)

// 對應 QuestionFeedbackDTO
data class QuestionFeedbackDto(
    val question: String,
    val answer: String,
    val comment: String = "",
    val better: String = "",
)

// 對應 StarPartDTO
data class StarPartDto(
    val key: String,
    val name: String,
    val present: Boolean,
    val fromAnswer: String = "",
    val hint: String = "",
)

// 對應 VideoDimDTO（這次一定是空陣列，本期沒做，但型別要存在）
data class VideoDimDto(
    val name: String,
    val score: Int,
    val hint: String = "",
)

// 對應 CollabDimDTO（一對一模式這次也一定是空陣列，只有 group 才有內容）
data class CollabDimDto(
    val name: String,
    val score: Int,
    val hint: String = "",
    val evidence: String = "",
)

// 對應 MissingPointDTO
data class MissingPointDto(
    val point: String,
    val why: String,
)

// 對應 ReportResponse
data class ReportResponse(
    val mode: String,
    val faceDimensions: List<FaceDimensionDto> = emptyList(),
    val subScores: List<SubScoreDto> = emptyList(),
    val questionFeedbacks: List<QuestionFeedbackDto> = emptyList(),
    val starParts: List<StarPartDto> = emptyList(),
    val videoDims: List<VideoDimDto> = emptyList(),
    val collabDims: List<CollabDimDto> = emptyList(),
    val improvements: List<String> = emptyList(),
    val missingPoints: List<MissingPointDto> = emptyList(),
    val resumeGrounded: Boolean = false,
    val notices: List<String> = emptyList(),
)