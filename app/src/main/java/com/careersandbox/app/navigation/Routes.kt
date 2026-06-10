package com.careersandbox.app.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"

    const val HOME = "home"
    const val RESUME_HUB = "resume_hub"
    const val INTERVIEW_HUB = "interview_hub"
    const val WORKPLACE_SANDBOX = "workplace_sandbox"
    const val WORKPLACE_CHAT = "workplace_chat"
    const val WORKPLACE_EMAIL = "workplace_email"
    const val PROFILE = "profile"

    const val EXPERIENCE_LIST = "experience_list"
    const val EXPERIENCE_EDIT = "experience_edit"
    const val TEAM_CHAT = "team_chat"
    const val RESUME_EDITOR = "resume_editor"
    const val JD_CUSTOMIZE = "jd_customize"

    const val INTERVIEW_SETUP_INDIVIDUAL = "interview_setup_individual"
    const val INTERVIEW_SETUP_GROUP = "interview_setup_group"
    const val INTERVIEW_LIVE_INDIVIDUAL = "interview_live_individual"
    const val INTERVIEW_LIVE_GROUP = "interview_live_group"
    const val INTERVIEW_LIVE_PANEL = "interview_live_panel"
    const val INTERVIEW_REPORT = "interview_report"
    const val INTERVIEW_HISTORY = "interview_history"

    const val NOTIFICATIONS_ALL = "notifications_all"
    const val ARTICLE_DETAIL = "article_detail/{articleId}"
    fun articleDetail(articleId: String) = "article_detail/$articleId"

    const val RESUME_PROFILE = "resume_profile"
    const val RESUME_UPLOAD_PROCESSING = "resume_upload_processing"
    const val CAREER_EXPLORATION = "career_exploration"
    const val FIT_ANALYSIS = "fit_analysis"

    const val COMPETITION_LIST = "competition_list"
    const val COMPETITION_DETAIL = "competition_detail/{compId}"
    fun competitionDetail(compId: String) = "competition_detail/$compId"
    const val NEW_JOB_APPLICATION = "new_job_application"
    const val JOB_APPLICATION_DETAIL = "job_application_detail/{jobId}"
    fun jobApplicationDetail(jobId: String) = "job_application_detail/$jobId"
    const val PDF_EXPORT_DIALOG = "pdf_export_dialog/{versionId}"
    fun pdfExportDialog(versionId: String) = "pdf_export_dialog/$versionId"

    const val SETTINGS_PROFILE = "settings_profile"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val SETTINGS_PRIVACY = "settings_privacy"
    const val SETTINGS_HELP = "settings_help"
    const val SETTINGS_LOGOUT = "settings_logout"
}
