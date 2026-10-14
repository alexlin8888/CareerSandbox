package com.careersandbox.app.ui.screens.sandbox2

/** One skill row of an occupation: what to show in the gap line when it is missing or partial. */
data class SkillSpec(val name: String, val missingGap: String, val partialGap: String)

/** Per-occupation content of the result screen. The rules below read the recorded day. */
data class ResultConfig(
    val occupation: String,
    val company: String,
    val skills: List<SkillSpec>,
    val skillNote: String,
    val challengeNote: String,
    val transfers: List<TransferRow>,
    val transferNote: String,
)

/** Skills taken from the player's master resume. TODO: read the real resume instead of this default. */
object ResumeSkills {
    var names: Set<String> = setOf("SQL", "Excel", "基礎統計")
}

val AnalystConfig = ResultConfig(
    occupation = "資料分析師",
    company = "NovaPay",
    skills = listOf(
        SkillSpec("SQL", "SQL", "SQL"),
        SkillSpec("Excel", "Excel", "Excel"),
        SkillSpec("基礎統計", "基礎統計", "區分相關與因果的寫法"),
        SkillSpec("圖表呈現", "圖表呈現", "選圖時對應問題的判斷"),
        SkillSpec("Python／R", "Python 或 R（入門程度）", "Python 或 R"),
    ),
    skillNote = "已具備依你的母版履歷，「部分」來自今天選圖與寫摘要的表現。門檻依 O*NET、104 職缺整理。",
    challengeNote = "從業者經驗文常提到：需求不清時要自己先補問，這在資料分析師的日常裡很常見。",
    transfers = listOf(
        TransferRow("商業分析師", 4, 5),
        TransferRow("產品營運", 4, 5),
        TransferRow("市場研究員", 3, 5),
        TransferRow("資料工程助理", 2, 5),
    ),
    transferNote = "數字是與資料分析師重疊的核心技能項數，依 ESCO 職能對照整理。",
)

/** Turns the recorded day in SandboxV2State into the result screen content. No model involved. */
object SandboxResultBuilder {

    private const val REPLY_CAP_MS = 600_000L   // ignore gaps over 10 minutes (player was away)
    private const val MAX_CHALLENGES = 4

    fun build(config: ResultConfig = AnalystConfig): SandboxResult {
        val skills = buildSkills(config)
        val (quote, note) = friendLines()
        return SandboxResult(
            occupation = config.occupation,
            company = config.company,
            skills = skills,
            skillNote = config.skillNote,
            challenges = buildChallenges(),
            challengeNote = config.challengeNote,
            styleStats = buildStyleStats(),
            friendQuote = quote,
            friendNote = note,
            transfers = config.transfers,
            transferNote = config.transferNote,
            gaps = buildGaps(config, skills),
        )
    }

    // ---- entry feasibility ----

    private fun buildSkills(config: ResultConfig): List<SkillRow> {
        val s = SandboxV2State
        val fits = s.pins.map { SandboxAnalysis.fits(it, s.taskList) }
        // Evidence from today: -1 against, 0 none, 1 for
        val chartEvidence = when {
            fits.isEmpty() -> 0
            fits.all { it } -> 1
            else -> -1
        }
        val statEvidence = when {
            s.causalCandidates().isNotEmpty() -> -1
            s.selectedCands.isNotEmpty() -> 1
            else -> 0
        }
        return config.skills.map { spec ->
            val base = if (spec.name in ResumeSkills.names) SkillStatus.HAVE else SkillStatus.MISSING
            val evidence = when (spec.name) {
                "圖表呈現" -> chartEvidence
                "基礎統計" -> statEvidence
                else -> 0
            }
            val status = when {
                evidence < 0 && base == SkillStatus.HAVE -> SkillStatus.PARTIAL
                evidence > 0 && base == SkillStatus.MISSING -> SkillStatus.PARTIAL
                else -> base
            }
            SkillRow(spec.name, status)
        }
    }

    private fun buildGaps(config: ResultConfig, skills: List<SkillRow>): String {
        val pairs = skills.zip(config.skills)
        val missing = pairs.filter { it.first.status == SkillStatus.MISSING }.map { it.second.missingGap }
        val partial = pairs.filter { it.first.status == SkillStatus.PARTIAL }.map { it.second.partialGap }
        return (missing + partial).joinToString("、").ifEmpty { "今天的紀錄裡沒有看到明顯的缺口" }
    }

    // ---- challenge points ----

    private class Pick(val rank: Int, val order: Int, val item: Challenge)

    private fun timeOf(station: Int): String = when (station) {
        1 -> "09:12"
        2 -> "10:40"
        3 -> "13:20"
        4 -> if (SandboxV2State.changeChoice == 0) "15:35" else "15:05"
        5 -> "17:30"
        else -> "18:00"
    }

    private fun limitCols(): List<Int> =
        SandboxDataset.columns.indices.filter { SandboxDataset.columns[it].limit != null }

    private fun buildChallenges(): List<Challenge> {
        val s = SandboxV2State
        val picks = mutableListOf<Pick>()
        // Lower rank = more important. Order keeps the list chronological.
        fun add(rank: Int, station: Int, stage: String, text: String, basis: String) {
            picks.add(Pick(rank, station * 10 + picks.size, Challenge(timeOf(station), stage, text, basis)))
        }

        // 09:12 asking the PM
        val n = s.askCount
        when {
            n == 0 -> add(2, 1, "弄清楚需求", "你沒有問小昀，直接去看資料。任務清單只有「收藏清單值不值得做？」一題。", "依據：訊息頁的提問記錄")
            s.clarity == Clarity.VAGUE -> add(2, 1, "弄清楚需求", "你問了 $n 題，沒有問到「誰會用」或「留存」。任務停在「收藏清單值不值得做？」。", "依據：訊息頁的提問記錄")
            s.clarity == Clarity.PARTIAL -> add(
                2, 1, "弄清楚需求",
                "你問了 $n 題，問到${if ("who" in s.asked) "「誰會用」" else "「留存」"}，另一個方向沒有問。",
                "依據：訊息頁的提問記錄",
            )
        }

        // 10:40 data window
        val cols = limitCols()
        val unseen = cols.filter { it !in s.openedCols }
        val rushed = cols.filter { s.limitHandled[it] == 2 }
        if (unseen.isNotEmpty()) {
            val names = unseen.joinToString("、") { "「${SandboxDataset.columns[it].name}」" }
            add(2, 2, "看資料", "資料表有 ${cols.size} 欄附了使用限制，你沒有點開$names。", "依據：資料頁的欄位點選記錄")
        } else if (rushed.isNotEmpty()) {
            val names = rushed.joinToString("、") { "「${SandboxDataset.columns[it].name}」" }
            add(3, 2, "看資料", "${names}你選了先用現有資料分析，沒有向阿凱或小昀確認。", "依據：你對資料限制的處理")
        }

        // 13:20 analysis window
        if (s.pins.isNotEmpty()) {
            val bad = s.pins.count { !SandboxAnalysis.fits(it, s.taskList) }
            if (bad > 0) add(2, 3, "做分析", "你釘了 ${s.pins.size} 張圖，其中 $bad 張對不上要回答的問題。", "依據：圖表與問題的對應")
            when (s.changeChoice) {
                2 -> add(3, 3, "需求變動", "設計師想知道重度使用者會不會用，你把它延到明天。小昀說老闆問到她會說還在看。", "依據：你對需求變動的回覆")
                null -> add(3, 3, "需求變動", "小昀轉述設計師想看重度使用者，你沒有回覆。", "依據：你對需求變動的回覆")
            }
        }

        // 15:05 summary
        if (s.recommendation != null) {
            val summary = mutableListOf<Triple<Int, String, String>>()
            val realLimits = SandboxAnalysis.summaryLimits.withIndex().filter { it.value.col != null }.map { it.index }
            if (realLimits.none { it in s.selectedLimits }) {
                summary.add(Triple(1, "摘要的「看不出來的事」沒有寫到資料表上的任何一項限制。", "依據：摘要的限制勾選"))
            }
            val causal = s.causalCandidates()
            if (causal.isNotEmpty()) {
                summary.add(Triple(1, "摘要裡有一句「${causal.first().text.removeSuffix("。")}」。資料只能看出差異，看不出原因。", "依據：你寫進摘要的句子"))
            }
            val missingQ = s.unansweredQuestions()
            if (missingQ.isNotEmpty()) {
                summary.add(Triple(2, "小昀的第 ${missingQ.joinToString("、") { (it + 1).toString() }} 題沒有圖可以回答。", "依據：題目與圖的對應"))
            }
            val boss = s.bossText
            summary.forEachIndexed { i, (rank, text, basis) ->
                // The manager's line comes from the same rule as the first item
                val extra = if (i == 0 && boss != null && !boss.startsWith("可以")) " 周經理看完摘要後也說：「$boss」" else ""
                add(rank, 4, "寫摘要", text + extra, basis)
            }
        }

        // 17:30 follow-up questions
        val firstText = s.followFirstText
        if (s.followScenario !in setOf("", "clean", "default") && firstText != null) {
            add(2, 5, "被追問", "小昀追問：「${s.followPrompt}」你回覆：「$firstText」", "依據：你在追問時選的回覆")
        }

        val chosen = picks.sortedBy { it.rank }.take(MAX_CHALLENGES).sortedBy { it.order }.map { it.item }
        return chosen.ifEmpty { listOf(longestStage()) }
    }

    /** Fallback when no rule fired: the stage the player stayed in longest. */
    private fun longestStage(): Challenge {
        val s = SandboxV2State
        val stages = listOf(Triple("data", "看資料", 2), Triple("analysis", "做分析", 3), Triple("output", "寫摘要", 4))
        val top = stages.mapNotNull { (key, label, station) -> s.stageMs(key)?.let { Triple(label, station, it) } }
            .maxByOrNull { it.third }
            ?: return Challenge("—", "整天", "今天沒有特別卡住的時間點。", "依據：整天的操作記錄")
        return Challenge(timeOf(top.second), top.first, "這一段你停留最久，${fmtMs(top.third)}。", "依據：各階段的停留時間")
    }

    // ---- working style ----

    private fun buildStyleStats(): List<StyleStat> {
        val s = SandboxV2State
        val delays = s.replyDelaysMs.filter { it in 0L..REPLY_CAP_MS }.sorted()
        val reply = if (delays.isEmpty()) "—" else "約 " + fmtShort(delays[delays.size / 2])
        val ask = if (s.askCount == 0) "沒問就動手" else "先問 ${s.askCount} 題再動手"
        val data = s.stageMs("data")?.let { fmtMs(it) } ?: "—"
        return listOf(
            StyleStat("回覆小昀訊息", reply),
            StyleStat("接需求時", ask),
            StyleStat("需求中途變更", changeLabel()),
            StyleStat("看資料用了", data),
        )
    }

    private fun changeLabel(): String = when (SandboxV2State.changeChoice) {
        0 -> "先做完原題再補"
        1 -> "改做重度使用者"
        2 -> "延到明天"
        3 -> "先問小昀要決定什麼"
        else -> if (SandboxV2State.pins.isEmpty()) "沒有遇到" else "沒有回覆"
    }

    // ---- what the player told 阿哲 ----

    private fun friendLines(): Pair<String, String> {
        val s = SandboxV2State
        val h = s.eveningHardest ?: return "你沒有對阿哲說今天哪段最煩。" to "—"
        val quote = "你對阿哲說：最煩的是「${HardestOptions.getOrElse(h) { "" }}」。"
        val note = when (h) {
            0 -> {
                val n = s.askCount
                val detail = when {
                    n == 0 -> "你沒有問小昀"
                    s.clarity == Clarity.CLEAR -> "你問了 $n 題，問到「誰會用」和「留存」"
                    s.clarity == Clarity.PARTIAL -> "你問了 $n 題，只問到其中一個方向"
                    else -> "你問了 $n 題，沒有問到「誰會用」或「留存」"
                }
                "對照 ${timeOf(1)} 的紀錄：$detail"
            }
            1 -> {
                val cols = limitCols()
                val opened = cols.count { it in s.openedCols }
                val used = s.stageMs("data")?.let { "資料頁用了 ${fmtMs(it)}，" } ?: ""
                "對照 ${timeOf(2)} 的紀錄：${used}點開 $opened／${cols.size} 個有限制的欄位"
            }
            2 -> "對照 ${timeOf(3)} 的紀錄：你的處理是「${changeLabel()}」"
            3 -> {
                val fit = s.pins.count { SandboxAnalysis.fits(it, s.taskList) }
                "對照 ${timeOf(4)} 的紀錄：你釘了 ${s.pins.size} 張圖，$fit 張對得上問題，摘要裡寫了 ${s.selectedCands.size} 句發現"
            }
            else -> {
                val p = s.followPrompt
                if (p.isBlank()) "對照 ${timeOf(5)} 的紀錄：沒有被追問" else "對照 ${timeOf(5)} 的紀錄：小昀問「$p」"
            }
        }
        return quote to note
    }

    // ---- formatting ----

    private fun fmtMs(ms: Long): String {
        val sec = ms / 1000
        return when {
            sec < 1 -> "不到 1 秒"
            sec < 60 -> "$sec 秒"
            else -> "${sec / 60} 分 ${sec % 60} 秒"
        }
    }

    private fun fmtShort(ms: Long): String {
        val sec = maxOf(1L, ms / 1000)
        return if (sec < 60) "$sec 秒" else "${sec / 60} 分"
    }
}