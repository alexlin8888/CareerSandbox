package com.careersandbox.app.data.pdf

import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.local.UserStore
import com.careersandbox.app.data.mock.CustomizedItem
import com.careersandbox.app.data.mock.MockJdCustomizer
import com.careersandbox.app.data.repository.RemoteExperienceRepository
import com.careersandbox.app.data.model.JobApplication

/** 客製化結果頁按下「匯出」的那一刻，把當下畫面算好的資料先存在這裡，
 *  匯出流程直接讀這份，避免重新計算跟預覽對不起來。用完即清空。 */
object PendingCustomExport {
    var data: CustomResumeData? = null
}


/**
 * 客製化履歷 PDF 專用的資料結構（定案二素材清單，交接文件三）。
 * PDF 繪製邏輯只依賴這個結構，不管資料實際從 MockData 還是後端 API 來，
 * 之後接上真實資料時，只需要新增一個組裝函式，繪製邏輯完全不用改。
 */
data class CustomResumeData(
    val name: String,
    val schoolLine: String,             // "學校 · 系所 · 年級"
    val bio: String,
    val keywords: List<String>,         // 對應 User.interests
    val email: String,
    val phone: String,
    val linkedin: String,
    val github: String,
    val portfolio: String,
    val coveredSkills: List<String>,    // 此職缺看重且已具備；來源：未來 B1 API 的 coveredKeywords
    val otherSkills: List<String>,      // 對應 User.skillsHave 減去 coveredSkills
    val languages: List<Pair<String, String>>,
    val experienceItems: List<CustomResumeItem>,
)

data class CustomResumeItem(
    val title: String,       // 取自 Experience.title
    val timeRange: String,   // 取自 Experience.timeRange
    val text: String,        // 目前用 Experience.description；未來換成 B1 客製化後的 item.text
)

/**
 * 用真實登入使用者資料(UserStore.me)+ 真實經歷(RemoteExperienceRepository)+ 實際客製化結果組資料。
 * 回傳 null 代表使用者資料或經歷資料任一項拿不到,呼叫端要自行處理。
 */
suspend fun buildCustomResumeDataFromCustomization(
    customized: List<CustomizedItem>,
    includedTexts: Set<String>,
    experiences: List<com.careersandbox.app.data.model.Experience>,
): CustomResumeData? {
    val user = UserStore.me ?: return null
    val coveredSkills = MockJdCustomizer.coveredKeywords(experiences, MockJdCustomizer.defaultJdKeywords)
    return CustomResumeData(
        name = user.name,
        schoolLine = "${user.school} · ${user.department} · ${user.year}",
        bio = user.bio,
        keywords = user.interests,
        email = user.email,
        phone = user.phone,
        linkedin = user.linkedin,
        github = user.github,
        portfolio = user.portfolio,
        coveredSkills = coveredSkills,
        otherSkills = user.skillsHave.filterNot { it in coveredSkills },
        languages = user.languages.map { it.language to it.level },
        experienceItems = experiences.mapIndexedNotNull { i, exp ->
            val item = customized.getOrNull(i) ?: return@mapIndexedNotNull null
            if (item.highlighted || item.text in includedTexts) {
                CustomResumeItem(title = exp.title, timeRange = exp.timeRange, text = item.text)
            } else null
        },
    )
}

/**
 * 匯出流程專用:抓真實經歷 + 用真實使用者資料 + 預設客製化結果(全部收錄)組資料。
 * 經歷資料抓取失敗時回傳 null。
 */
suspend fun buildCustomResumeDataForExport(
    job: JobApplication?,
): CustomResumeData? {
    val experiences = RemoteExperienceRepository().list().getOrNull() ?: return null
    val jdKeywords = job?.jdKeywords?.takeIf { it.isNotEmpty() } ?: MockJdCustomizer.defaultJdKeywords
    val customized = MockJdCustomizer.customize(experiences, jdKeywords)
    return buildCustomResumeDataFromCustomization(
        customized = customized,
        includedTexts = customized.filter { it.highlighted }.map { it.text }.toSet(),
        experiences = experiences,
    )
}

/**
 * 母版履歷專用:直接抓真實使用者資料 + 真實經歷(原文,不經過客製化改寫)。
 * 沒有「此職缺看重」的概念,所有技能都放 otherSkills,不分 coveredSkills。
 * 回傳 null 代表使用者資料或經歷資料任一項拿不到。
 */
suspend fun buildCustomResumeDataForMasterExport(): CustomResumeData? {
    val user = UserStore.me ?: UserStore.refresh().let { UserStore.me } ?: return null
    val experiences = RemoteExperienceRepository().list().getOrNull() ?: return null
    return CustomResumeData(
        name = user.name,
        schoolLine = "${user.school} · ${user.department} · ${user.year}",
        bio = user.bio,
        keywords = user.interests,
        email = user.email,
        phone = user.phone,
        linkedin = user.linkedin,
        github = user.github,
        portfolio = user.portfolio,
        coveredSkills = emptyList(),
        otherSkills = user.skillsHave,
        languages = user.languages.map { it.language to it.level },
        experienceItems = experiences.map { exp ->
            CustomResumeItem(title = exp.title, timeRange = exp.timeRange, text = exp.description)
        },
    )
}