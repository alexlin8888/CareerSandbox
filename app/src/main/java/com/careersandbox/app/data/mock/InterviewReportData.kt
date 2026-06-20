package com.careersandbox.app.data.mock

/* =====================================================================
   面試報告 —— 核心回饋資料層(後端接點)

   【涵蓋】教授要求的三維度回饋:內容 / 結構 / 表達(含語調 prosody),
   外加細項分數與逐題回顧。目前前端用 mock 佔位,接點已留好。

   【後端接入說明】
   一場面試結束後,後端用本場逐字稿 + 目標 JD + 履歷母版計算:
     - faceDimensions():三大面向各自的分數、總評與可行動建議;
       「表達」面向另附 prosody(語速 / 停頓 / 語調 / 填充詞 / 開口前思考),
       這幾項屬語音側,由 VAD + STT 統計(見 VideoFaceMetrics 的語速說明)。
     - subScores():六項細分能力分數(內容深度、邏輯清晰度…)。
     - questionFeedbacks():逐題的「你的回答 / 教練點評 / 更好的講法」。

   接入時:把後端回傳轉成下列 data class,實作 InterviewReportProvider,
   替換 MockInterviewReportProvider 即可 —— UI(InterviewReportScreen)不用改。
   ===================================================================== */

/** 三大面向之一(內容 / 結構 / 表達) */
data class FaceDimension(
    val letter: String,                          // 單字標記(內 / 構 / 達)
    val name: String,                            // 面向名稱
    val score: Int,                              // 0–100
    val verdict: String,                         // 一句總評
    val points: List<String>,                    // 可行動建議
    val prosody: List<Pair<String, String>>? = null, // 僅「表達」面向有:語音指標
)

/** 一項細分能力分數 */
data class SubScore(val name: String, val score: Int)

/** 逐題回顧:你的回答 + 點評 + 更好的講法 */
data class QuestionFeedback(
    val question: String,
    val answer: String,
    val comment: String,
    val better: String,
)

/** 面試報告核心回饋分析器(後端接點) */
interface InterviewReportProvider {
    fun faceDimensions(): List<FaceDimension>
    fun subScores(): List<SubScore>
    fun questionFeedbacks(): List<QuestionFeedback>
}

/** Mock 實作:代表性範例,讓 UI 在 demo / UIUX 階段可完整展示。真接後端時整個換掉。 */
object MockInterviewReportProvider : InterviewReportProvider {

    override fun faceDimensions(): List<FaceDimension> = listOf(
        FaceDimension(
            letter = "內", name = "內容", score = 71,
            verdict = "有講到重點,但缺乏具體數字與亮點。",
            points = listOf(
                "自我介紹加 1-2 個量化成就(追蹤數、效率提升倍數)",
                "回答時多舉一個具體例子,少用空泛形容詞",
            ),
        ),
        FaceDimension(
            letter = "構", name = "結構", score = 82,
            verdict = "邏輯清楚,但講失敗經歷時 STAR 的 Result 段常缺。",
            points = listOf(
                "講經歷一律用 STAR:情境 → 任務 → 行動 → 結果",
                "結尾補一句「我從中學到什麼」",
            ),
        ),
        FaceDimension(
            letter = "達", name = "表達", score = 70,
            verdict = "整體流暢,但語速偏快、語調起伏不足。",
            points = listOf(
                "放慢語速,重點句講完停半秒",
                "用語調強調關鍵字,不要從頭平到尾",
            ),
            prosody = listOf(
                "語速" to "220 字/分 · 偏快",
                "停頓" to "偏少 · 句子間幾乎不停",
                "語調" to "起伏不足 · 偏平",
                "填充詞" to "「嗯 / 那個」8 次",
                "開口前思考" to "平均 4.2 秒 · 最長 11 秒",
            ),
        ),
    )

    override fun subScores(): List<SubScore> = listOf(
        SubScore("內容深度", 78),
        SubScore("邏輯清晰度", 82),
        SubScore("表達流暢度", 71),
        SubScore("互動能力", 68),
        SubScore("應變能力", 64),
        SubScore("自信程度", 80),
    )

    override fun questionFeedbacks(): List<QuestionFeedback> = listOf(
        QuestionFeedback(
            question = "請你做一個簡短的自我介紹。",
            answer = "你好,我是中山資管系大三的 Alex,過去主要做過社團行銷和資料分析實習,想往產品經理發展。",
            comment = "有清楚交代背景,但缺乏亮點。可以加 1-2 個具體成就。",
            better = "我是中山資管大三的 Alex,把社團 IG 從 0 經營到 1200 追蹤,實習用 SQL 把週報效率提升 4 倍,接下來想把這些經驗帶到產品端。",
        ),
        QuestionFeedback(
            question = "可以講一個你覺得做得不太好的決定嗎?",
            answer = "我們曾經辦過一場聯名活動,前期沒有先測試小規模就直接全推,結果觸及只有預期的三成。",
            comment = "誠實面對失誤是好的,但只講事實沒有反思。STAR 結構缺了 Result 的學習段落。",
            better = "觸及只有預期三成,我覆盤後發現缺了「先小規模測試」這一步。下次再辦時我先用兩個小貼文測流量,結果觸及達標。",
        ),
    )
}
