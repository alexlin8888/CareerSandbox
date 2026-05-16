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

data class Resume(
    val id: String,
    val title: String,
    val targetJob: String,
    val lastEdited: String,
    val version: String,
    val completion: Int,
)

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
)

enum class CompetitionCategory(val label: String) {
    CASE("商業個案"),
    HACKATHON("黑客松"),
    PITCH("提案 / 創業"),
    DESIGN("設計"),
    RESEARCH("研究"),
}
