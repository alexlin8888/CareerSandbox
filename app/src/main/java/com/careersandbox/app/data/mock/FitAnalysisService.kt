package com.careersandbox.app.data.mock

/**
 * 一份職缺對使用者母版的適配結果。
 * 後端 AI 匹配實作 FitAnalysisService 後,依 jobId 回填真結果即可,前端 UI 不用動。
 */
data class JobFit(
    val jobId: String,
    val title: String,
    val company: String,
    val tags: List<String>,
    val salary: String,
    val deadline: String,
    val matchScore: Int,
    val styleTag: String,            // 例:數據導向型
    val requiredSkills: List<String>, // 這份 JD 要求的技能(用來算技能差距)
)

/**
 * 適配分析資料來源。
 *
 * === 後端接點 ===
 * 目前由 MockFitAnalysisService 回傳示範資料。
 * 接上 AI 匹配後,改成真實作:availableJobs() 回傳使用者投遞/收藏的職缺,
 * fitFor(jobId) 依該職缺的 JD 跑匹配回傳 JobFit(含真實分數與要求技能)。
 * 前端只認這個介面,後端怎麼算都不影響 UI。
 */
interface FitAnalysisService {
    fun availableJobs(): List<JobFit>
    fun fitFor(jobId: String): JobFit
}

object MockFitAnalysisService : FitAnalysisService {
    private val jobs = listOf(
        JobFit(
            jobId = "fit_pm",
            title = "Junior PM",
            company = "Acer",
            tags = listOf("產品實習", "全職", "初級"),
            salary = "50-80k",
            deadline = "10/14 截止",
            matchScore = 82,
            styleTag = "數據導向型",
            requiredSkills = listOf("SQL", "使用者訪談", "A/B 測試", "Figma", "Python", "GA4"),
        ),
        JobFit(
            jobId = "fit_da",
            title = "資料分析師",
            company = "蝦皮",
            tags = listOf("資料", "全職", "初級"),
            salary = "55-85k",
            deadline = "11/02 截止",
            matchScore = 88,
            styleTag = "硬實力強",
            requiredSkills = listOf("SQL", "Python", "統計", "資料視覺化", "A/B 測試"),
        ),
        JobFit(
            jobId = "fit_mkt",
            title = "行銷企劃",
            company = "Dcard",
            tags = listOf("行銷", "全職", "初級"),
            salary = "45-65k",
            deadline = "10/28 截止",
            matchScore = 71,
            styleTag = "溝通導向型",
            requiredSkills = listOf("GA4", "內容行銷", "社群經營", "使用者訪談", "SQL"),
        ),
    )

    override fun availableJobs(): List<JobFit> = jobs
    override fun fitFor(jobId: String): JobFit = jobs.firstOrNull { it.jobId == jobId } ?: jobs.first()
}
