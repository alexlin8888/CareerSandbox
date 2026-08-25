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
    fun customize(experiences: List<Experience>, jdKeywords: List<String>): List<CustomizedItem>
}

object MockJdCustomizer : JdCustomizer {
    /** 備援關鍵字,只給還沒接上真實職缺的舊呼叫端當預設值用。*/
    val defaultJdKeywords: List<String> = listOf("需求分析", "SQL", "簡報", "Excel", "Python")

    override fun customize(experiences: List<Experience>, jdKeywords: List<String>): List<CustomizedItem> {
        val relevantTags = jdKeywords.toSet()
        return experiences.map { exp ->
            val matched = exp.tags.filter { it in relevantTags }
            CustomizedItem(
                text = exp.description,
                matchedKeywords = matched,
                highlighted = matched.size >= 2,
            )
        }
    }

    fun coveredKeywords(experiences: List<Experience>, jdKeywords: List<String> = defaultJdKeywords): List<String> =
        jdKeywords.filter { kw -> experiences.any { kw in it.tags } }
}
