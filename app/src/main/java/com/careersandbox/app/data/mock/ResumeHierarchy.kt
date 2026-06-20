package com.careersandbox.app.data.mock

/* =====================================================================
   履歷三層架構 —— 母版 → 職缺 → 版本(指導教授建議的重建,後端接點)

   把履歷從「一堆平行履歷」改成貼近真實求職的三層樹:
     母版(ResumeMaster):一份完整、不刪改的履歷,所有經歷技能的素材庫,
                        永遠不直接投出去。內容來自「經驗收集」。
     職缺(JobTarget)  :每個應徵目標(公司+職位)。在這層做 JD 客製
                        (從母版挑重點、弱化無關)。
     版本(ResumeVersion):同一職缺底下可有多個版本,各自記投遞狀態
                        (草稿/已投遞/面試中/等回覆/未錄取/錄取)。

   【遷移策略】這個檔只先建資料模型與介面(MockProvider 佔位),現有履歷畫面
   暫不動。之後再一個一個把履歷中心、JD 客製化、投遞追蹤遷到這個結構上。

   【後端接入說明】真接入時:
     - master() 來自使用者的經歷/技能資料表
     - jobTargets() 來自使用者建立的職缺,versions 來自各職缺的履歷版本與投遞紀錄
   實作 ResumeHierarchyProvider 即可替換下方 Mock,UI 不用改。
   ===================================================================== */

/** 投遞狀態 */
enum class SubmissionStatus(val label: String) {
    DRAFT("草稿"),
    SUBMITTED("已投遞"),
    INTERVIEWING("面試中"),
    WAITING("等回覆"),
    REJECTED("未錄取"),
    OFFER("錄取"),
}

/** 版本:某職缺底下的一份履歷版本 + 投遞狀態 */
data class ResumeVersion(
    val id: String,
    val label: String,             // 版本 A / 版本 B
    val status: SubmissionStatus,
    val submittedDate: String?,    // 投遞日期;草稿為 null
    val note: String,              // 這版的差異(強調了什麼)
)

/** 職缺:一個應徵目標 + 其下多個版本 */
data class JobTarget(
    val id: String,
    val title: String,             // 職位名稱
    val company: String,
    val jdKeywords: List<String>,  // 這個 JD 重視的關鍵字(客製依據)
    val versions: List<ResumeVersion>,
)

/** 母版:完整履歷(素材庫) */
data class ResumeMaster(
    val ownerName: String,
    val experienceCount: Int,      // 母版含幾段經歷(來自經驗收集)
    val skills: List<String>,
)

/** 履歷三層架構分析器(後端接點) */
interface ResumeHierarchyProvider {
    fun master(): ResumeMaster
    fun jobTargets(): List<JobTarget>
}

/** Mock 實作:母版接現有使用者資料,職缺/版本用代表性範例。真接後端時整個換掉。 */
object MockResumeHierarchyProvider : ResumeHierarchyProvider {

    override fun master(): ResumeMaster = ResumeMaster(
        ownerName = MockData.currentUser.name,
        experienceCount = MockData.experiences.size,
        skills = MockData.currentUser.skillsHave,
    )

    override fun jobTargets(): List<JobTarget> = listOf(
        JobTarget(
            id = "jt_pm",
            title = "Junior PM",
            company = "Acer",
            jdKeywords = listOf("產品思維", "跨部門協作", "資料判斷"),
            versions = listOf(
                ResumeVersion("v_pm_a", "版本 A", SubmissionStatus.INTERVIEWING, "2026/06/01", "強調產品思維與跨部門協作"),
                ResumeVersion("v_pm_b", "版本 B", SubmissionStatus.DRAFT, null, "改強調資料能力,給技術背景的主管看"),
            ),
        ),
        JobTarget(
            id = "jt_da",
            title = "資料分析師",
            company = "蝦皮",
            jdKeywords = listOf("SQL", "資料視覺化", "A/B 測試"),
            versions = listOf(
                ResumeVersion("v_da_a", "版本 A", SubmissionStatus.WAITING, "2026/06/10", "把 SQL 與量化成果拉到最前面"),
            ),
        ),
        JobTarget(
            id = "jt_mkt",
            title = "行銷企劃",
            company = "Dcard",
            jdKeywords = listOf("社群經營", "內容行銷", "成長"),
            versions = listOf(
                ResumeVersion("v_mkt_a", "版本 A", SubmissionStatus.DRAFT, null, "主打社群從 0 到 1200 的成長實作"),
            ),
        ),
    )
}
