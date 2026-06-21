package com.careersandbox.app.data.mock

import com.careersandbox.app.data.model.Experience

/* =====================================================================
   JD 客製化器 —— 把母版經歷依 JD 切成「強化 / 弱化」(後端接點)

   原本 JD 客製化結果頁用寫死的假 bullet。改成讀母版的實際經歷
   (MockData.experiences),依 JD 命中的關鍵字決定每段要強化還是弱化,
   讓客製化真的反映使用者母版的內容。

   後端接入:把下方 Mock 換成真正的 JD ↔ 經歷比對或 LLM 評分,介面不變。
   ===================================================================== */

/** 客製化後的一段履歷項目 */
data class CustomizedItem(
    val text: String,                   // 經歷描述(來自母版)
    val matchedKeywords: List<String>,  // 命中的 JD 關鍵字
    val highlighted: Boolean,           // true=強化, false=弱化
)

/** JD 客製化器(後端接點) */
interface JdCustomizer {
    fun customize(experiences: List<Experience>): List<CustomizedItem>
}

/**
 * Mock:以「資料分析類 JD」的關鍵字為準,某段經歷命中 >=2 個相關 tag 就強化,否則弱化。
 * 真接後端時換成真正的 JD ↔ 經歷比對或 LLM 評分,介面不變。
 */
object MockJdCustomizer : JdCustomizer {
    /** 這份 JD 看重的關鍵技能(後端接入時來自 JD 解析) */
    val jdKeywords: List<String> = listOf("數據分析", "SQL", "報表", "Excel", "Python")
    private val jdRelevantTags = jdKeywords.toSet()

    override fun customize(experiences: List<Experience>): List<CustomizedItem> =
        experiences.map { exp ->
            val matched = exp.tags.filter { it in jdRelevantTags }
            CustomizedItem(
                text = exp.description,
                matchedKeywords = matched,
                highlighted = matched.size >= 2,
            )
        }

    /** 母版涵蓋到的 JD 關鍵字(用來算命中率與適配度) */
    fun coveredKeywords(experiences: List<Experience>): List<String> =
        jdKeywords.filter { kw -> experiences.any { kw in it.tags } }
}
