package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateMapOf

enum class Clarity { CLEAR, PARTIAL, VAGUE }
enum class Who { PM, ME, BOSS }

data class ChatLine(val id: Long, val who: Who, val text: String)
data class PmQuestion(val key: String, val text: String, val reply: String)

/**
 * In-memory state of one simulated day. Call reset() when a new day starts.
 * Delayed replies run in an app-level scope so they still arrive when the player
 * is on another screen.
 */
object SandboxV2State {

    val questions = listOf(
        PmQuestion("pilot", "你最後要拿這份分析決定什麼？要不要開發，還是先爭取試點？",
            "先爭取試點吧，老闆不太可能一次給完整開發。所以我需要證明有人會用"),
        PmQuestion("who", "你想知道的是所有使用者，還是某一群人會不會用？",
            "呃 我沒想那麼細。付費的？活躍的？你看資料哪群比較明顯"),
        PmQuestion("ret", "如果真的做了，你覺得什麼結果才算有價值？",
            "留存吧。老闆最近一直在問留存"),
        PmQuestion("data", "目前有記錄使用者把內容存起來的行為嗎？",
            "有個「稍後再看」算類似，但好像不是一開始就有，你問阿凱"),
        PmQuestion("level", "這份摘要是給你初步判斷，還是要能直接拿去跟老闆決定？",
            "初步就好，老闆那邊我來講。但數字要站得住"),
        PmQuestion("design", "這個功能是設計師想的嗎？",
            "對，設計師提的。不過我現在比較想知道下週一到底要不要提"),
    )

    val lines = mutableStateListOf<ChatLine>()
    val asked = mutableStateListOf<String>()
    val taskList = mutableStateListOf<String>()
    val openedCols = mutableStateListOf<Int>()
    val limitHandled = mutableStateMapOf<Int, Int>()   // column index -> chosen option index
    val pins = mutableStateListOf<ChartPin>()
    val selectedCands = mutableStateListOf<String>()   // "pinIndex-candidateIndex"
    val selectedLimits = mutableStateListOf<Int>()     // indexes into SandboxAnalysis.summaryLimits
    var keyChart by mutableStateOf<Int?>(null)
        private set
    var recommendation by mutableStateOf<String?>(null)
        private set
    var outputOpened by mutableStateOf(false)
        private set
    private var outputInit = false

    var askCount by mutableStateOf(0)
        private set
    var askClosed by mutableStateOf(false)
        private set
    var clarity by mutableStateOf(Clarity.VAGUE)
        private set
    var station by mutableStateOf(1)
        private set
    var hasUnread by mutableStateOf(false)
        private set
    var busy by mutableStateOf(false)
        private set
    var optionsReady by mutableStateOf(false)
        private set
    var introCardVisible by mutableStateOf(false)
        private set
    var dataOpened by mutableStateOf(false)
        private set
    var analysisOpened by mutableStateOf(false)
        private set
    var changePending by mutableStateOf(false)
        private set
    var changeChoice by mutableStateOf<Int?>(null)   // 0 finish first, 1 switch, 2 tomorrow, 3 ask PM
        private set
    // ---- station 5: follow-up questions ----
    var followOptions by mutableStateOf<List<String>>(emptyList())
        private set
    var dayOver by mutableStateOf(false)
        private set
    var followScenario by mutableStateOf("")      // which first question was asked
        private set
    var followFirst by mutableStateOf<Int?>(null)  // index of the first answer
        private set
    var followSecond by mutableStateOf<Int?>(null)
        private set
    var eveningMood by mutableStateOf<Int?>(null)      // index of the first answer to 阿哲
        private set
    // ---- records for the result screen ----
    var followPrompt: String = ""
        private set
    var followFirstText: String? = null
        private set
    var followSecondText: String? = null
        private set
    var bossText: String? = null
        private set
    /** How long the player took to answer a PM message, in ms, in order. */
    val replyDelaysMs = mutableListOf<Long>()
    private val stageStart = mutableMapOf<String, Long>()
    private val stageEnd = mutableMapOf<String, Long>()
    private var lastIncomingAt = 0L

    private fun markStart(name: String) {
        if (name !in stageStart) stageStart[name] = System.currentTimeMillis()
    }

    private fun markEnd(name: String) {
        if (name !in stageEnd) stageEnd[name] = System.currentTimeMillis()
    }

    /** Time between first opening a window and finishing it. Null if either end is missing. */
    fun stageMs(name: String): Long? {
        val s = stageStart[name] ?: return null
        val e = stageEnd[name] ?: return null
        return e - s
    }
    var eveningHardest by mutableStateOf<Int?>(null)   // index of the event picked as the most annoying
        private set
    private var followStarted = false
    private var followStage = 0                     // 0 idle, 1 first answer pending, 2 second answer pending
    private var firstCats: List<Int> = emptyList()  // 0 pilot, 1 just do it, 2 depends on the boss
    private var secondCloses: List<String> = emptyList()

    private class FollowStep(val scenario: String, val prompt: String, val options: List<String>, val cats: List<Int>)
    private class SecondStep(val prompt: String, val options: List<String>)
    private var changeStarted = false
    private var extraMin = 0
    private var retentionHot = false

    val changeReplies = listOf(
        "我先照原本的做完，重度使用者那個我再補一張。",
        "好，那我把重度使用者拉出來看。",
        "原本那幾題我先做，重度使用者這個明天可以嗎？",
        "等一下，你下週一要決定的到底是什麼？設計師想知道的跟老闆要看的是同一件事嗎？",
    )
    private var openingStarted = false
    var typing by mutableStateOf(false)
        private set

    private var msgScreenOpen = false
    private var nextId = 0L
    private var dayStarted = false
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    /** PM is "in a meeting" after three questions, or once the player is past the data station. */
    val inMeeting: Boolean get() = askCount >= 3 || station >= 3

    val clockText: String
        get() {
            val base = listOf(9 * 60 + 12, 10 * 60 + 40, 13 * 60 + 20, 15 * 60 + 5, 17 * 60 + 30, 18 * 60)
            val i = (station - 1).coerceIn(0, base.lastIndex)
            // Finishing the original questions first costs 30 minutes at the summary station
            val m = base[i] + if (station == 4) extraMin else 0
            return "%02d:%02d".format(m / 60, m % 60)
        }

    fun reset() {
        job.cancelChildren()
        lines.clear(); asked.clear(); taskList.clear()
        askCount = 0; askClosed = false; clarity = Clarity.VAGUE; station = 1
        hasUnread = false; busy = false; typing = false
        dayStarted = false
        optionsReady = false
        openingStarted = false
        introCardVisible = false
        openedCols.clear(); limitHandled.clear()
        dataOpened = false
        pins.clear(); analysisOpened = false
        changePending = false; changeChoice = null; changeStarted = false
        extraMin = 0; retentionHot = false
        selectedCands.clear(); selectedLimits.clear()
        keyChart = null; recommendation = null; outputOpened = false; outputInit = false
        followOptions = emptyList(); dayOver = false; followScenario = ""
        followFirst = null; followSecond = null
        followStarted = false; followStage = 0; firstCats = emptyList(); secondCloses = emptyList()
        eveningMood = null; eveningHardest = null
        replyDelaysMs.clear(); stageStart.clear(); stageEnd.clear(); lastIncomingAt = 0L
        followPrompt = ""; followFirstText = null; followSecondText = null; bossText = null
    }

    /** Called when the desk screen appears; sends the opening messages once. */
    fun beginDay() {
        if (dayStarted) return
        dayStarted = true
        scope.launch {
            delay(1500)
            pmSay("欸 在嗎", typingMs = 0)
        }
    }

    private fun playOpening() {
        if (openingStarted) return
        openingStarted = true
        scope.launch {
            busy = true
            delay(700)
            pmSay("我想推收藏清單。你幫我看一下，現在有沒有使用者真的需要？", typingMs = 1100)
            delay(400)
            pmSay("下週一要跟老闆報，今天下班前給我一份就好", typingMs = 1000)
            delay(400)
            pmSay("資料我請阿凱開給你了", typingMs = 800)
            delay(900)
            busy = false
            introCardVisible = true   // full-screen task card; options come after the player confirms
        }
    }

    /** Called after the task card has slid into the banner slot. */
    fun confirmTask() { optionsReady = true }

    fun onMsgScreenOpened() { msgScreenOpen = true; hasUnread = false; playOpening() }
    fun onMsgScreenClosed() { msgScreenOpen = false }

    fun ask(key: String) {
        if (busy || key in asked) return
        val q = questions.first { it.key == key }
        asked.add(key)
        post(Who.ME, q.text)
        if (askClosed) lateAsk(q) else firstRoundAsk(q)
    }

    fun markColumnOpened(col: Int) { if (col !in openedCols) openedCols.add(col) }

    /** choice: 0 ask engineer, 1 note it, 2 analyze anyway, 3 ask PM. */
    fun handleLimit(col: Int, choice: Int) {
        if (col in limitHandled) return
        val c = SandboxDataset.columns[col]
        val lim = c.limit ?: return
        limitHandled[col] = choice
        if (choice == 3) {
            post(Who.ME, "「${c.name}」那欄有個問題：${lim.prompt} 這欄必要嗎？")
            if (inMeeting) {
                post(Who.PM, "會議中")
                scope.launch { delay(15_000); post(Who.PM, lim.pmReply) }
            } else {
                scope.launch { pmSay(lim.pmReply) }
            }
        }
    }

    fun finishData() { markEnd("data"); if (station < 3) station = 3 }
    fun onAnalysisOpened() { analysisOpened = true; markStart("analysis") }

    fun pinChart(pin: ChartPin) {
        if (pins.size >= 3) return
        pins.add(pin)
        if (pins.size == 1) startChangeEvent()
    }

    fun finishAnalysis() {
        markEnd("analysis")
        changePending = false   // an unanswered change request just expires
        if (station < 4) station = 4
    }
    /** First open of the output window pre-ticks the limitations the player already handled in the data window. */
    fun onOutputOpened() {
        outputOpened = true
        markStart("output")
        if (outputInit) return
        outputInit = true
        SandboxAnalysis.summaryLimits.forEachIndexed { i, l ->
            val col = l.col
            if (col != null && limitHandled[col] in setOf(0, 1, 3)) selectedLimits.add(i)
        }
    }

    fun toggleCand(key: String) { if (!selectedCands.remove(key)) selectedCands.add(key) }
    fun toggleLimit(i: Int) { if (!selectedLimits.remove(i)) selectedLimits.add(i) }
    fun chooseKeyChart(i: Int) { keyChart = i }
    fun chooseRecommendation(r: String) { recommendation = r }

    /** TODO: station 5 (follow-up questions) goes between submit and the result screen. */
    /** Submitting the summary starts station 5: the PM follows up. */
    fun submitOutput() {
        if (followStarted) return
        followStarted = true
        markEnd("output")
        station = 5
        scope.launch {
            delay(1500)
            val step = firstFollowStep()
            followScenario = step.scenario
            followPrompt = step.prompt
            followPrompt = step.prompt
            firstCats = step.cats
            pmSay(step.prompt, 900)
            followStage = 1
            followOptions = step.options
        }
    }
    fun setEveningMood(i: Int) { eveningMood = i }
    fun setEveningHardest(i: Int) { eveningHardest = i }

    fun answerFollow(i: Int) {
        val opts = followOptions
        if (followStage == 0 || i !in opts.indices) return
        followOptions = emptyList()
        post(Who.ME, opts[i])
        if (followStage == 1) {
            followStage = 2
            followFirst = i
            followFirstText = opts[i]
            val cat = firstCats.getOrElse(i) { 0 }
            scope.launch {
                val step = secondFollowStep(cat)
                secondCloses = listOf("好 那我這樣報", "好吧", "…行")
                pmSay(step.prompt, 900)
                followOptions = step.options
            }
        } else {
            followStage = 3
            followSecond = i
            followSecondText = opts[i]
            scope.launch {
                pmSay(secondCloses.getOrElse(i) { "好" }, 700)
                delay(2500)
                val boss = bossLine()
                bossText = boss
                post(Who.BOSS, boss)
                delay(1500)
                station = 6
                dayOver = true
            }
        }
    }

    fun selectedCandidates(): List<Candidate> = selectedCands.mapNotNull { key ->
        val parts = key.split("-")
        val pin = pins.getOrNull(parts[0].toInt()) ?: return@mapNotNull null
        SandboxAnalysis.candidates(pin).getOrNull(parts[1].toInt())
    }

    fun causalCandidates() = selectedCandidates().filter { it.kind == 'c' || it.kind == 'x' }

    /** Questions in the task list that no fitting chart answers. Empty in free mode. */
    fun unansweredQuestions(): List<Int> {
        if (clarity == Clarity.VAGUE) return emptyList()
        return taskList.indices.filter { qi ->
            pins.none { it.questionIndex == qi && SandboxAnalysis.fits(it, taskList) }
        }
    }

    private fun shortQuestion(q: String): String = when {
        q.startsWith("誰會用") -> "誰會用"
        q.startsWith("用的人，留存") -> "用的人留存有沒有比較高"
        q.startsWith("現在沒這功能") -> "大家是不是在用別的方式存"
        q.startsWith("重度使用者") -> "重度使用者會不會用"
        else -> "整體有沒有人需要"
    }

    private fun genericFirstOptions(rec: String): List<String> {
        val a = when (rec) {
            "小規模試點" -> "以目前的資料，我會建議先做小規模試點，不直接全面開發，先看實際使用情況。"
            "先補資料再評估" -> "以目前的資料，我會建議先補資料再評估，不直接決定要不要開發。"
            "直接開發" -> "以目前的資料，我會建議直接開發，限制我已經寫在摘要裡了。"
            else -> "以目前的資料，我會建議先暫緩，等資料補齊再看。"
        }
        val short = when (rec) {
            "小規模試點" -> "先試點"
            "先補資料再評估" -> "先補資料"
            "直接開發" -> "直接開發"
            else -> "先暫緩"
        }
        return listOf(a, "那就先做吧，至少下週一有東西可以報。", "要看老闆願意承擔多少不確定性。如果一定要今天決定，我會說$short。")
    }

    private fun firstFollowStep(): FollowStep {
        val rec = recommendation ?: "小規模試點"
        val causal = causalCandidates()

        if (clarity == Clarity.VAGUE) {
            val intents = pins.map { it.intent }
            // Free mode scenarios in order C, B, A, D. Answer categories: 0 pilot, 1 just do it, 2 depends on the boss
            return when {
                causal.isNotEmpty() -> FollowStep(
                    "C", "你寫「${causal.first().text.removeSuffix("。")}」。這兩件事中間，你有證據嗎？",
                    listOf(
                        "沒有，這裡只能說有關聯，不能直接說是功能造成的。",
                        "沒有直接證據，但我覺得可以先這樣寫。",
                        "那我改成建議先做小規模試點。",
                        "我只是想讓摘要看起來有結論。",
                    ), listOf(0, 0, 2, 1),
                )
                pins.size >= 2 && (intents.any { it == null } || intents.toSet().size > 1) -> FollowStep(
                    "B", "你放了 ${pins.size} 張圖，但我還是不太知道你想回答什麼。你是想確認使用者需求，還是想看留存？",
                    listOf(
                        "我想先把目前看得到的都整理出來。",
                        "我主要想看哪些人可能會用。",
                        "我想確認這個功能有沒有機會影響留存。",
                        "我還沒有把問題收斂好。",
                    ), listOf(0, 0, 2, 1),
                )
                pins.size == 1 -> FollowStep(
                    "A", "你今天主要看這一件事？還是其他方向你來不及看？",
                    listOf(
                        "我先確認這個方向，其他的目前沒有足夠資料。",
                        "我覺得這是最直接跟功能有關的。",
                        "我還沒想清楚，所以先看這張。",
                        "我以為這樣就可以判斷要不要做。",
                    ), listOf(0, 0, 2, 1),
                )
                selectedLimits.size > selectedCands.size -> FollowStep(
                    "D", "你花不少篇幅寫資料問題。那今天這份摘要，你想幫我做什麼決定？",
                    listOf(
                        "先確認哪些資料需要補，再決定是否開發。",
                        "我想先讓你知道目前結論的限制。",
                        "我覺得現在資料還不能支持直接開發。",
                        "我沒有想那麼多，只是看到問題就先記下來。",
                    ), listOf(0, 0, 2, 1),
                )
                else -> FollowStep(
                    "default", "所以到底做不做？老闆不會想聽需要更多資料",
                    genericFirstOptions(rec), listOf(0, 1, 2),
                )
            }
        }

        // Question mode: first rule that holds
        val unanswered = unansweredQuestions()
        if (unanswered.isNotEmpty()) {
            return FollowStep(
                "unanswered", "你前面不是說要看${shortQuestion(taskList[unanswered.first()])}嗎？那題現在有答案嗎？",
                genericFirstOptions(rec), listOf(0, 1, 2),
            )
        }
        if (causal.isNotEmpty()) {
            return FollowStep(
                "causal", "你寫「${causal.first().text.removeSuffix("。")}」。這兩件事真的有關嗎？",
                genericFirstOptions(rec), listOf(0, 1, 2),
            )
        }
        val loose = SandboxAnalysis.summaryLimits.withIndex().firstOrNull { (i, l) ->
            l.col != null && limitHandled[l.col] !in setOf(0, 1, 3) && i !in selectedLimits
        }
        if (loose != null) {
            val prompt = when (loose.value.col) {
                3 -> "稍後再看那欄六月以前都是 0，那你這個結論可以套到全部使用者嗎？"
                5 -> "留存的算法跟營運報表不一樣，老闆拿去對數字的時候怎麼辦？"
                else -> "客服詢問只有近三個月，你說的需求，有比這更長的資料嗎？"
            }
            return FollowStep("limit", prompt, genericFirstOptions(rec), listOf(0, 1, 2))
        }
        val clean = rec == "小規模試點" || rec == "先補資料再評估"
        return FollowStep(
            "clean",
            if (clean) "那你現在是建議先試，還是要等資料補齊？" else "所以到底做不做？老闆不會想聽需要更多資料",
            genericFirstOptions(rec), listOf(0, 1, 2),
        )
    }

    private fun secondFollowStep(cat: Int): SecondStep {
        val rec = recommendation ?: "小規模試點"
        return when (cat) {
            0 -> SecondStep(
                "試點我懂，但老闆可能會問，為什麼不直接做完整？",
                listOf(
                    "因為現在的資料只看得到一群人有需求，看不到上線後留存會不會變，先試比較不會白做",
                    "那就寫直接開發，你比較知道老闆",
                    "你覺得他會問嗎？會的話我摘要裡多寫一段",
                ),
            )
            1 -> SecondStep(
                "可是你摘要裡寫${rec}欸，我要改掉嗎",
                listOf(
                    "不用改，摘要照寫，我口頭上是說如果非決定不可",
                    "改吧，寫試點",
                    "你先別動，我再看一下",
                ),
            )
            else -> SecondStep(
                "你就給我一句話啦，我明天要寫簡報",
                listOf(
                    "一句話：先試點，資料不夠支持直接開發",
                    "一句話：做，有需求",
                    "一句話：等資料補齊再說",
                ),
            )
        }
    }

    /** The manager speaks once and only reads the summary. First rule that holds. */
    private fun bossLine(): String {
        val realLimits = SandboxAnalysis.summaryLimits.withIndex().filter { it.value.col != null }.map { it.index }
        return when {
            realLimits.none { it in selectedLimits } -> "限制那段要補，資料只有三個月這件事不能不講。"
            causalCandidates().isNotEmpty() -> "發現有一句超過資料能講的，你自己看得出來是哪句。"
            unansweredQuestions().isNotEmpty() -> "有一題沒答到，小昀會被問。"
            else -> "可以。老闆問到再說。"
        }
    }

    /** After the first pinned chart, the PM relays a request change from the designer. */
    private fun startChangeEvent() {
        if (changeStarted) return
        changeStarted = true
        scope.launch {
            delay(1500)
            pmSay("我剛跟設計師聊過 他們比較想知道重度使用者會不會用")
            pmSay("這個你今天看得出來嗎", 600)
            changePending = true
        }
    }

    fun answerChange(choice: Int) {
        if (!changePending) return
        changePending = false
        changeChoice = choice
        post(Who.ME, changeReplies[choice])
        when (choice) {
            0 -> {
                extraMin = 30
                rebuildTaskList()
                scope.launch { pmSay("好 那你看時間", 600) }
            }
            1 -> {
                rebuildTaskList()
                scope.launch { pmSay("好", 500) }
            }
            2 -> scope.launch { pmSay("…可以啦 但老闆問到我就說還在看", 900) }
            3 -> {
                post(Who.PM, "會議中")
                scope.launch {
                    delay(15_000)
                    post(Who.PM, "老闆比較在意留存啦 設計師那個先不用")
                    retentionHot = true
                    rebuildTaskList()
                }
            }
        }
    }

    /** Called when the data window opens. If the PM conversation was left unfinished, wrap it up. */
    fun onDataOpened() {
        dataOpened = true
        markStart("data")
        if (askClosed || busy) return
        if (askCount == 0) {
            skipAsking()
        } else {
            scope.launch {
                busy = true
                closeAsk()
                busy = false
            }
        }
    }

    fun skipAsking() {
        if (busy || askClosed) return
        scope.launch {
            busy = true
            if (askCount == 0) {
                post(Who.ME, "好，我先看資料。")
                pmSay("好 有問題再敲我")
            }
            closeAsk()
            busy = false
        }
    }

    private fun firstRoundAsk(q: PmQuestion) {
        scope.launch {
            busy = true
            askCount++
            pmSay(q.reply)
            if (askCount == 1) pmSay("我等一下要進會議，你先問最想確認的，剩下的看資料再說")
            if (askCount >= 3) closeAsk()
            busy = false
        }
    }

    private fun lateAsk(q: PmQuestion) {
        if (inMeeting) {
            post(Who.PM, "會議中，晚點")
            scope.launch {
                delay(15_000)
                post(Who.PM, q.reply)
                afterAnswer()
            }
        } else {
            askCount++
            scope.launch {
                busy = true
                pmSay(q.reply)
                afterAnswer()
                if (askCount >= 3) pmSay("我先進會議了 剩下的看資料再說")
                busy = false
            }
        }
    }

    private suspend fun afterAnswer() {
        val before = clarity
        updateClarity()
        if (clarity != before) {
            pmSay("喔對 那你順便看一下這個", 600)
            rebuildTaskList()
        }
    }

    private suspend fun closeAsk() {
        askClosed = true
        updateClarity()
        rebuildTaskList()
        when (clarity) {
            Clarity.CLEAR -> {
                pmSay("那你幫我看這幾個")
                pmSay("1 誰會用 2 用的人留存有沒有比較高 3 現在沒這功能大家是不是在用別的方式存")
                pmSay("下班前給我 我進會議了")
            }
            Clarity.PARTIAL -> {
                val one = if ("who" in asked) "誰會用" else "用的人留存有沒有比較高"
                pmSay("那你幫我看 1 $one 2 整體有沒有人需要")
                pmSay("我進會議了")
            }
            Clarity.VAGUE -> {
                pmSay("那就先幫我看有沒有人需要")
                pmSay("我在位子上 有事敲我")
            }
        }
        rebuildTaskList()
        station = 2
    }

    private fun updateClarity() {
        val who = "who" in asked
        val ret = "ret" in asked
        clarity = when {
            who && ret -> Clarity.CLEAR
            who || ret -> Clarity.PARTIAL
            else -> Clarity.VAGUE
        }
    }

    private fun rebuildTaskList() {
        taskList.clear()
        taskList.addAll(
            when (clarity) {
                Clarity.CLEAR -> listOf("誰會用這個功能？", "用的人，留存有沒有比較高？", "現在沒這功能，大家是不是在用別的方式存？")
                Clarity.PARTIAL -> listOf(
                    if ("who" in asked) "誰會用這個功能？" else "用的人，留存有沒有比較高？",
                    "整體有沒有人需要？",
                )
                Clarity.VAGUE -> listOf("收藏清單值不值得做？")
            }
        )
        applyChangeToTaskList()
    }

    /** Re-applies the player's answer to the request change after every rebuild. */
    private fun applyChangeToTaskList() {
        if (clarity == Clarity.VAGUE) return   // free mode has no question list to edit
        val heavy = "重度使用者會不會用？"
        when (changeChoice) {
            0 -> if (heavy !in taskList) taskList.add(heavy)
            1 -> {
                val i = taskList.indexOf("誰會用這個功能？")
                if (i >= 0) taskList[i] = heavy else if (heavy !in taskList) taskList.add(heavy)
            }
        }
        if (retentionHot) {
            val i = taskList.indexOfFirst { it.startsWith("用的人，留存") }
            if (i >= 0 && !taskList[i].endsWith("）")) taskList[i] = taskList[i] + "（小昀說這題最重要）"
        }
    }

    private suspend fun pmSay(text: String, typingMs: Long = 700) {
        typing = true
        delay(typingMs)
        typing = false
        post(Who.PM, text)
    }

    private fun post(who: Who, text: String) {
        val now = System.currentTimeMillis()
        if (who == Who.ME) {
            // Reply latency: from the last incoming message to this one
            if (lastIncomingAt > 0L) {
                replyDelaysMs.add(now - lastIncomingAt)
                lastIncomingAt = 0L
            }
        } else {
            lastIncomingAt = now
        }
        lines.add(ChatLine(nextId++, who, text))
        if (who != Who.ME && !msgScreenOpen) hasUnread = true
    }
}