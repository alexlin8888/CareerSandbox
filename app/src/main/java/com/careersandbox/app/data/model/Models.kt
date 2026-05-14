package com.careersandbox.app.data.model

data class User(
    val id: String,
    val name: String,
    val school: String,
    val department: String,
    val year: String,
    val interests: List<String> = emptyList(),
    val skillsHave: List<String> = emptyList(),
    val skillsWant: List<String> = emptyList(),
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
