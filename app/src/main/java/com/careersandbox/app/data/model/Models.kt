package com.careersandbox.app.data.model

data class User(
    val id: String,
    val name: String,
    val school: String,
    val department: String,
    val year: String,
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val linkedin: String = "",
    val github: String = "",
    val portfolio: String = "",
    val interests: List<String> = emptyList(),
    val skillsHave: List<String> = emptyList(),
    val skillsWant: List<String> = emptyList(),
    val languages: List<LanguageProficiency> = emptyList(),
    val activities: List<ActivityRecord> = emptyList(),
)

data class LanguageProficiency(
    val language: String,
    val level: String,
)

data class ActivityRecord(
    val title: String,
    val role: String,
    val period: String,
    val highlight: String = "",
)

data class Experience(
    val id: String,
    val title: String,
    val category: String,
    val timeRange: String,
    val description: String,
    val tags: List<String> = emptyList(),
)

/**
 * 母版履歷 — 使用者只維護 1 份,所有經歷不修飾全部寫下來
 */
data class Resume(
    val id: String,
    val lastEdited: String,
    val totalExperiences: Int,
    val totalSkills: Int,
    val completion: Int,
)

/**
 * 職缺 — 1 個職位+公司組合,底下可有多個版本
 */
data class JobApplication(
    val id: String,
    val company: String,
    val position: String,
    val jdSnippet: String,
    val jdKeywords: List<String> = emptyList(),
    val matchScore: Int,
    val createdAt: String,
    val versions: List<ResumeVersion>,
)

/**
 * 版本 — 針對職缺的客製化履歷版本
 */
data class ResumeVersion(
    val id: String,
    val versionNumber: Int,
    val status: VersionStatus,
    val createdAt: String,
    val submittedAt: String = "",
    val notes: String = "",
)

enum class VersionStatus(val label: String) {
    DRAFT("草稿"),
    EDITING("編輯中"),
    SUBMITTED("已投遞"),
    ARCHIVED("封存"),
}

enum class ResumeTemplate(val label: String, val sublabel: String) {
    TW_CLASSIC("台式中文", "104 經典版型 ・ 含照片"),
    EN_ONE_PAGE("英文一頁式", "Yourator / LinkedIn 風格"),
    MODERN_DARK("現代深色", "新創公司投遞首選"),
}

data class InterviewRecord(
    val id: String,
    val type: InterviewType,
    val jobTitle: String,
    val score: Int,
    val date: String,
)

enum class InterviewType(val label: String) {
    INDIVIDUAL("個人面試"),
    GROUP("團體面試")
}

data class ChatMessage(
    val id: String,
    val speaker: String,
    val content: String,
    val isUser: Boolean = false,
    val isInterviewer: Boolean = false,
)

data class HomeStat(
    val resumeCompletion: Int,
    val weeklyInterviews: Int,
    val recommendedJobs: Int,
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val read: Boolean = false,
)

data class Article(
    val id: String,
    val category: ArticleCategory,
    val title: String,
    val excerpt: String,
    val source: String,
    val publishedDate: String,
    val readMinutes: Int,
    val url: String,
    val coverImageUrl: String = "",
    val bodyContent: List<ArticleBlock> = emptyList(),
)

sealed class ArticleBlock {
    data class Heading(val text: String) : ArticleBlock()
    data class Paragraph(val text: String) : ArticleBlock()
    data class BulletList(val items: List<String>) : ArticleBlock()
    data class Quote(val text: String) : ArticleBlock()
}

enum class ArticleCategory(val label: String) {
    CAREER_EXPLORATION("職涯探索"),
    INTERVIEW("面試"),
    RESUME("履歷"),
    WORKPLACE("職場"),
}

data class Competition(
    val id: String,
    val title: String,
    val organizer: String,
    val category: CompetitionCategory,
    val deadline: String,
    val teamSize: String,
    val prize: String,
    val tags: List<String>,
    val coverColor: String = "orange",
    val coverImageUrl: String = "",
)

enum class CompetitionCategory(val label: String) {
    CASE("商業個案"),
    HACKATHON("黑客松"),
    PITCH("提案 / 創業"),
    DESIGN("設計"),
    RESEARCH("研究"),
}

/** 推薦隊友 — 依職能標籤互補性媒合(模組 ④)*/
data class TeamMate(
    val id: String,
    val name: String,
    val school: String,
    val dept: String,
    val skills: List<String>,
    val matchReason: String,
    val matchScore: Int,
)

/** 現有可加入的隊伍 */
data class CompetitionTeam(
    val id: String,
    val name: String,
    val leaderName: String,
    val currentSize: Int,
    val targetSize: Int,
    val lookingFor: List<String>,
    val note: String,
)
