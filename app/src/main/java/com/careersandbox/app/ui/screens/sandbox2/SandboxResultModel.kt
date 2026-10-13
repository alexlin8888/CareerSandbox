package com.careersandbox.app.ui.screens.sandbox2

enum class SkillStatus { HAVE, PARTIAL, MISSING }

data class SkillRow(val name: String, val status: SkillStatus)
data class Challenge(val time: String, val stage: String, val text: String, val basis: String)
data class StyleStat(val label: String, val value: String)
data class TransferRow(val job: String, val overlap: Int, val total: Int)

data class SandboxResult(
    val occupation: String,
    val company: String,
    val skills: List<SkillRow>,
    val skillNote: String,
    val challenges: List<Challenge>,
    val challengeNote: String,
    val styleStats: List<StyleStat>,
    val friendQuote: String,
    val friendNote: String,
    val transfers: List<TransferRow>,
    val transferNote: String,
    val gaps: String,
)

/** Hard-coded sample data for the demo. Replace with real simulation records later. */
fun sampleResult() = SandboxResult(
    occupation = "資料分析師",
    company = "NovaPay",
    skills = listOf(
        SkillRow("SQL", SkillStatus.HAVE),
        SkillRow("Excel", SkillStatus.HAVE),
        SkillRow("基礎統計", SkillStatus.HAVE),
        SkillRow("圖表呈現", SkillStatus.PARTIAL),
        SkillRow("Python／R", SkillStatus.MISSING),
    ),
    skillNote = "已具備依你的母版履歷，「部分」來自今天選圖的表現。門檻依 O*NET、104 職缺整理。",
    challenges = listOf(
        Challenge(
            "10:40", "看資料",
            "你在資料頁停了 4 分鐘，沒有回頭問小昀範圍。之後 2 張圖與問題對不上。",
            "依據：資料頁停留時間、圖表與問題的對應",
        ),
        Challenge(
            "17:30", "被追問",
            "周經理問「所以要不要做」，你的回答沒有提到資料看不出來的部分。",
            "依據：你在追問時的文字回覆",
        ),
    ),
    challengeNote = "從業者經驗文常提到：需求不清時要自己先補問，這在資料分析師的日常裡很常見。",
    styleStats = listOf(
        StyleStat("回覆小昀訊息", "平均 52 秒"),
        StyleStat("接需求時", "先問 2 題再動手"),
        StyleStat("需求中途變更", "重做圖表"),
        StyleStat("溝通方式", "偏好文字補充"),
    ),
    friendQuote = "你對阿哲說：今天最煩的是不確定要不要先問清楚。",
    friendNote = "對照 10:40 的紀錄：你問了 2 題，但沒有問範圍",
    transfers = listOf(
        TransferRow("商業分析師", 4, 5),
        TransferRow("產品營運", 4, 5),
        TransferRow("市場研究員", 3, 5),
        TransferRow("資料工程助理", 2, 5),
    ),
    transferNote = "數字是與資料分析師重疊的核心技能項數，依 ESCO 職能對照整理。",
    gaps = "Python 或 R（入門程度）、選圖時對應問題的判斷",
)