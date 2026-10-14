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

enum class Who { PM, ME }
enum class Clarity { CLEAR, PARTIAL, VAGUE }

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
        get() = when (station) {
            1 -> "09:12"
            2 -> "10:40"
            3 -> "13:20"
            4 -> "15:05"
            5 -> "17:30"
            else -> "18:00"
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

    fun finishData() { if (station < 3) station = 3 }
    fun onAnalysisOpened() { analysisOpened = true }

    fun pinChart(pin: ChartPin) { if (pins.size < 3) pins.add(pin) }

    fun finishAnalysis() { if (station < 4) station = 4 }

    /** Called when the data window opens. If the PM conversation was left unfinished, wrap it up. */
    fun onDataOpened() {
        dataOpened = true
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
    }

    private suspend fun pmSay(text: String, typingMs: Long = 700) {
        typing = true
        delay(typingMs)
        typing = false
        post(Who.PM, text)
    }

    private fun post(who: Who, text: String) {
        lines.add(ChatLine(nextId++, who, text))
        if (who == Who.PM && !msgScreenOpen) hasUnread = true
    }
}