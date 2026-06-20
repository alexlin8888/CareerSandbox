package com.careersandbox.app.data.mock

/* =====================================================================
   面試教練 —— 「該提、卻沒提」漏講點資料層(後端接點)

   【為什麼有這層】
   這是指導教授點名的最高價值教練功能:面試回饋不只看使用者「講了什麼」,
   更要抓出他「有、卻整場沒講到」的加分點 —— 這正是一般面試者最痛、
   最難自評的盲區。

   【後端接入說明】
   真實的漏講偵測在面試結束後由後端計算,輸入三項:
     - 本場面試的目標 JD
     - 使用者的履歷母版(經歷 / 量化成果 / 技能)
     - 本場逐字稿(每題問答)
   流程(後端,LLM):
     1. 從 JD 抽出重視的能力與關鍵字(資料能力、協作領導、量化思考…)。
     2. 從履歷母版找出使用者「確實有」的對應亮點。
     3. 與逐字稿比對,取「履歷有 ∩ JD 重視 ∩ 本場沒講到」的差集 → 漏講點。
     4. 每個漏講點附「為什麼這段對這份 JD 重要」,讓回饋可行動。

   接入時:把後端回傳轉成 List<MissingPoint>,實作 MissingPointsAnalyzer,
   替換下方 MockMissingPointsAnalyzer 即可 —— UI(OmissionSection)完全不用改。
   ===================================================================== */

/** 一個「該提、卻沒提」的漏講加分點 */
data class MissingPoint(
    val point: String,  // 該提的亮點(履歷有、本場沒講到)
    val why: String,    // 為什麼這段對這份 JD 重要
)

/** 漏講點分析器(後端接點) */
interface MissingPointsAnalyzer {
    /** 回傳本場面試「該提卻沒提」的漏講點清單 */
    fun analyze(): List<MissingPoint>
}

/**
 * Mock 實作:用代表性的範例佔位,讓 UI 在 demo / UIUX 階段可完整展示。
 * 真接後端時整個物件換掉即可,介面與資料形狀不變。
 */
object MockMissingPointsAnalyzer : MissingPointsAnalyzer {
    override fun analyze(): List<MissingPoint> = listOf(
        MissingPoint(
            "SQL 把週報效率提升 4 倍",
            "這份 JD 很重視數據能力,但你整場沒提到這段量化成果。",
        ),
        MissingPoint(
            "社團 IG 從 0 經營到 1200 追蹤",
            "能證明你的成長行銷實作,面試官通常很買單。",
        ),
        MissingPoint(
            "帶 5 人團隊完成聯名專案",
            "JD 要求協作與領導,這段沒帶到很可惜。",
        ),
    )
}
