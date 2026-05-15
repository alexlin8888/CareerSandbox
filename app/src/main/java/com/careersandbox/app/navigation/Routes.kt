package com.careersandbox.app.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"

    const val HOME = "home"
    const val RESUME_HUB = "resume_hub"
    const val INTERVIEW_HUB = "interview_hub"
    const val EXPLORE_HUB = "explore_hub"
    const val PROFILE = "profile"

    const val EXPERIENCE_LIST = "experience_list"
    const val EXPERIENCE_EDIT = "experience_edit"
    const val RESUME_EDITOR = "resume_editor"
    const val JD_CUSTOMIZE = "jd_customize"

    const val INTERVIEW_SETUP_INDIVIDUAL = "interview_setup_individual"
    const val INTERVIEW_SETUP_GROUP = "interview_setup_group"
    const val INTERVIEW_LIVE_INDIVIDUAL = "interview_live_individual"
    const val INTERVIEW_LIVE_GROUP = "interview_live_group"
    const val INTERVIEW_REPORT = "interview_report"
    const val INTERVIEW_HISTORY = "interview_history"

    const val NOTIFICATIONS_ALL = "notifications_all"
    const val ARTICLE_DETAIL = "article_detail/{articleId}"
    fun articleDetail(articleId: String) = "article_detail/$articleId"

    const val RESUME_PROFILE = "resume_profile"
    const val RESUME_UPLOAD_PROCESSING = "resume_upload_processing"
    const val JD_CU