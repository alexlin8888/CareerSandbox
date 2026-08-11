package com.careersandbox.app.data.pdf

import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.local.UserStore
import com.careersandbox.app.data.mock.CustomizedItem
import com.careersandbox.app.data.mock.MockJdCustomizer

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
 * 用真實登入使用者資料（UserStore.me，來自 /users/me）+ 實際客製化結果組資料。
 * user 為 null 時代表 UserStore 還沒載入，呼叫端要自行處理（例如先 UserStore.refresh()）。
 */
fun buildCustomResumeDataFromCustomization(
    customized: List<CustomizedItem>,
    includedTexts: Set<String>,
): CustomResumeData? {
    val user = UserStore.me ?: return null
    val coveredSkills = MockJdCustomizer.coveredKeywords(MockData.experiences)
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
        experienceItems = MockData.experiences.mapIndexedNotNull { i, exp ->
            val item = customized.getOrNull(i) ?: return@mapIndexedNotNull null
            if (item.highlighted || item.text in includedTexts) {
                CustomResumeItem(title = exp.title, timeRange = exp.timeRange, text = item.text)
            } else null
        },
    )
}

/**
 * 匯出流程專用：用真實使用者資料 + 預設客製化結果（全部收錄）組資料。
 * ⚠️ 經歷列表（title/timeRange/text）目前仍讀 MockData.experiences，尚未換成 RemoteExperienceRepository，
 * 屬已知待辦——經歷的欄位結構（ExperienceResponse）跟客製化比對邏輯需要另外處理，先不在這次範圍內。
 */
fun buildCustomResumeDataForExport(): CustomResumeData? {
    val customized = MockJdCustomizer.customize(MockData.experiences)
    return buildCustomResumeDataFromCustomization(
        customized = customized,
        includedTexts = customized.map { it.text }.toSet(),
    )
}

