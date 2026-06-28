package com.careersandbox.app.data.mock

import androidx.compose.ui.graphics.Color
import com.careersandbox.app.R
import com.careersandbox.app.ui.theme.AccentBlue
import com.careersandbox.app.ui.theme.AccentGreen
import com.careersandbox.app.ui.theme.BrandAmber
import com.careersandbox.app.ui.theme.BrandOrange

/* =====================================================================
   模型驅動沙盒 —— 內容引擎(混合架構的「表層內容」)
   信箱 / LINE / app 內文不再寫死,而是依「第幾天 + 三計量(關係狀態)+ 旗標」產生。
   大方向固定(Day2 永遠 email 風暴),但關係相關的內文隨你的選擇變。

   為什麼 key 在計量上:對話是模型生成的三選一,玩家的選擇 → 計量(主管信任/同事情誼/專業形象)變動。
   所以「內文看計量」= 內文看你做過的選擇。計量一定會被新對話系統更新,變體必觸發、現版即可運作。
   (之後對話 engine 也可額外吐故事旗標,做更細的內文分支。)

   前端只把 day + flags + 三計量丟進來,渲染回傳內容。現用 MockSandboxContentEngine;
   後端就緒換 RemoteSandboxContentEngine(LLM 依劇情生成內文),前端不改 —— 本檔 data class 即合約。
   本批(batch268)先接信箱(MailRow/MailBody);LINE(ChatRow/ChatMsg)下一批接同一套。
   ===================================================================== */

/** 信箱清單一列(欄位對齊原 NovaMailInbox 的 NovaMailRow,渲染碼不變) */
data class MailRow(
    val id: String,
    val sender: String,
    val subject: String,
    val prev: String,
    val time: String,
    val unread: Boolean,
    val star: Boolean,
    val imp: Boolean,
    val attach: Boolean,
    val res: Int? = null,
    val letter: String = "",
    val avBg: Color = Color(0xFFCBD5E1),
    val label: String = "",
    val labelBg: Color = Color.Transparent,
    val labelCol: Color = Color.Transparent,
)

/** 開信內文(欄位對齊原 NovaMailOpen 的 MailContent,渲染碼不變) */
data class MailBody(
    val sender: String,
    val email: String,
    val to: String,
    val subject: String,
    val time: String,
    val avatar: Int?,
    val letter: String,
    val avatarBg: Color,
    val important: Boolean,
    val body: String,
    val attachment: String?,
    val presets: List<String>,
)

/** LINE 清單一列(欄位對齊原 NovaChatList 的 NovaChatRow,渲染碼不變) */
data class ChatRow(
    val id: String,
    val name: String,
    val prev: String,
    val time: String,
    val unread: String,
    val online: Boolean,
    val pin: Boolean,
    val mute: Boolean,
    val res: Int? = null,
    val letter: String = "",
    val bg: Color = Color(0xFFCBD5E1),
    val group: Boolean = false,
)

/** LINE 單則訊息(欄位對齊原 NovaChat 的 ChatMsg) */
data class ChatMsg(
    val sender: String,
    val text: String,
    val time: String,
    val incoming: Boolean,
)

/** LINE 對話(欄位對齊原 NovaChat 的 ChatThread) */
data class ChatThread(
    val name: String,
    val avatar: Int?,
    val avatarLetter: String,
    val avatarBg: Color,
    val status: String,
    val script: List<ChatMsg>,
)

/** 行事曆事件(欄位對齊原 NovaCalendar 的 CalEvent,渲染碼不變) */
data class CalEvent(
    val title: String,
    val sub: String,
    val bar: Color,
    val current: Boolean = false,
    val note: String = "",
)

/** 週列一格(欄位對齊原 NovaCalendar 的 WeekDay) */
data class WeekDay(val label: String, val num: String, val selected: Boolean)

/** 內部社群貼文(欄位對齊原 NovaGram 的 GramPost) */
data class GramPost(val img: Int, val caption: String, val likes: String)

interface SandboxContentEngine {
    fun inbox(day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): List<MailRow>
    fun mailBody(id: String, day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): MailBody?
    fun chatRows(day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): List<ChatRow>
    fun chatThread(id: String, day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): ChatThread?
    fun calendarEvents(day: Int): List<CalEvent>
    fun calendarWeek(day: Int): List<WeekDay>
    fun gramPosts(pb: Int): List<GramPost>
}

object SandboxContentEngineProvider {
    val engine: SandboxContentEngine = MockSandboxContentEngine
}

/**
 * 假內容後端(純前端用)。把原本寫死的信搬進來當基底,對 Ken / Vivian 兩封關係信依計量加語氣變體:
 * - Vivian 看「同事情誼」:你把人推開(低)→ 她冷淡防衛;你建立交情(高)→ 她暖、願意一起扛。
 * - Ken 看「主管信任」:信任低 → 他語帶保留、盯得緊;信任高 → 他放手、肯定你。
 * 計量由對話的選擇累積,所以同一封信、不同玩法,讀到的語氣不同。真後端由 LLM 依完整劇情生成。
 */
object MockSandboxContentEngine : SandboxContentEngine {

    // 關係分三段:冷(<=2) / 中 / 暖(>=6)
    private fun tier(v: Int) = if (v <= 2) -1 else if (v >= 6) 1 else 0

    // ---- Vivian:demo 信語氣看「同事情誼」----
    private fun vivianBody(pb: Int): MailBody {
        val body = when (tier(pb)) {
            -1 -> "客戶董事會下週三要看分帳 demo,我已經先答應了。\n\n我知道你忙,但這次別又把我一個人丟在前面——至少生一個能跑的版本給我,可以嗎?\n\n—— Vivian"
            1 -> "客戶董事會下週三要看分帳 demo,我先答應了。\n\n這陣子謝謝你都有 cover 我,這次換我們一起想辦法。你那邊需要我配合什麼,直接說。\n\n—— Vivian"
            else -> "客戶董事會下週三要看分帳的 demo,時間我已經先答應了。\n\n我知道工程那邊卡關,但這場很關鍵。能不能至少生出一個能跑的版本?拜託。\n\n—— Vivian"
        }
        val presets = if (tier(pb) == -1) listOf(
            "這次我會把 demo 顧好,不會再讓你獨自扛。",
            "先跟你對一下要 demo 哪些,把風險講清楚。",
            "我盡量,但工程狀況你也知道。",
        ) else listOf(
            "我來跟工程確認,至少準備一個能 demo 的版本。",
            "先跟你對一下要 demo 哪些功能,免得當場出包。",
            "這場我陪你一起,先把要講的故事想清楚。",
        )
        return MailBody(
            "Vivian", "<vivian@novapay.com>", "寄給 我", "客戶下週要 demo", "13:50",
            R.drawable.colleague_vivian, "", Color(0xFFCBD5E1), true, body, null, presets,
        )
    }

    private fun vivianPrev(pb: Int) = when (tier(pb)) {
        -1 -> "別又把我一個人丟在前面…"
        1 -> "這次換我們一起想辦法…"
        else -> "他們董事會已經排好時間了…"
    }

    // ---- Ken:決議信語氣看「主管信任」----
    private fun kenBody(mt: Int): MailBody {
        val body = when (tier(mt)) {
            -1 -> "分帳出狀況了。今天 5 點前給我建議:照原計畫 / 延期 / 縮減範圍,理由寫清楚。\n\n講重點,別又給我模稜兩可的東西——我需要能直接對客戶交代的版本。\n\n—— Ken"
            1 -> "分帳這邊有點狀況,想聽你的判斷。今天 5 點前給我:照原計畫 / 延期 / 縮減範圍,寫下你的理由就好。\n\n你這陣子的判斷我信得過,放手做。\n\n—— Ken"
            else -> "聽說分帳功能有狀況。今天 5 點前給我你的建議:照原計畫 / 延期 / 縮減範圍,寫清楚理由。\n\n我需要能對客戶交代的版本。\n\n—— Ken"
        }
        return MailBody(
            "Ken", "<ken@novapay.com>", "寄給 我、Vivian、阿哲", "分帳的事", "14:04",
            R.drawable.ken_neutral, "", Color(0xFFCBD5E1), true, body, "分帳上線決議.pdf",
            listOf(
                "建議基本版照原計畫上線、進階版下一版。客戶看得到、工程也守得住。",
                "建議延期兩週,把 bug 清乾淨再上,品質先顧。",
                "建議縮減範圍,月底先上能對客戶交代的部分。",
            ),
        )
    }

    private fun kenPrev(mt: Int) = when (tier(mt)) {
        -1 -> "別又給我模稜兩可的東西…"
        1 -> "你的判斷我信得過,放手做…"
        else -> "聽說分帳功能有狀況。今天 5 點前…"
    }

    // ---- 固定信(不隨關係變)----
    private fun staticBody(id: String): MailBody? = when (id) {
        "ci" -> MailBody(
            "CI 建置通知", "<ci@novapay.com>", "寄給 wallet-team", "[wallet] build #4821 失敗", "10:32",
            null, "CI", Color(0xFF64748B), false,
            "Pipeline failed at stage: integration-test\n\nFAILED: SettlementServiceTest.shouldSplitPayment\nExpected: 3 entries, Actual: 2\n\n完整 log 請至 CI 後台查看。",
            null,
            listOf("我看一下這個 test 為什麼掛。", "轉給阿哲,這是金流串接那塊。"),
        )
        "hr" -> MailBody(
            "NovaPay HR", "<hr@novapay.com>", "寄給 全體同仁", "本週五團隊午餐", "11:20",
            null, "HR", Color(0xFF3B82F6), false,
            "本週五 12:00 部門聚餐,地點:三樓交誼廳。\n\n請於週四前回覆出席與飲食偏好(葷／素／過敏原)。\n\n新同事也很歡迎一起來認識大家!",
            null,
            listOf("我會出席,葷食,沒有過敏原。", "這週比較趕,這次先不參加了。"),
        )
        "promo" -> MailBody(
            "雲端服務電子報", "<news@cloudpro.com>", "寄給 你", "限時優惠:年費 5 折", "昨天",
            null, "雲", Color(0xFF10B981), false,
            "升級 Pro,享 5 倍儲存空間與進階備份。\n\n限時 48 小時,年費 5 折。\n\n[立即升級]　[查看方案]",
            null,
            listOf("沒興趣,退訂這類促銷信。"),
        )
        else -> null
    }

    override fun mailBody(id: String, day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): MailBody? = when (id) {
        "ken" -> kenBody(mt)
        "vivian" -> vivianBody(pb)
        else -> staticBody(id)
    }

    override fun inbox(day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): List<MailRow> = listOf(
        MailRow("ken", "Ken", "分帳的事", kenPrev(mt), "14:04", true, true, true, true, res = R.drawable.ken_neutral),
        MailRow("vivian", "Vivian", "客戶下週要 demo", vivianPrev(pb), "13:50", true, false, true, false, res = R.drawable.colleague_vivian),
        MailRow("ci", "CI 建置通知", "[wallet] build #4821 失敗", "pipeline failed at integration-test…", "10:32", true, false, false, false,
            letter = "CI", avBg = Color(0xFF64748B), label = "工作", labelBg = Color(0xFFFCE8E6), labelCol = Color(0xFFC5392D)),
        MailRow("hr", "NovaPay HR", "本週五團隊午餐", "記得回覆出席與飲食偏好…", "11:20", false, false, false, false,
            letter = "HR", avBg = Color(0xFF3B82F6)),
        MailRow("promo", "雲端服務電子報", "限時優惠:年費 5 折", "升級 Pro 享更多儲存空間…", "昨天", false, false, false, false,
            letter = "雲", avBg = Color(0xFF10B981), label = "促銷內容", labelBg = Color(0xFFE6F4EA), labelCol = Color(0xFF1E8E5A)),
    )

    // ===================== LINE(NovaChat)內容,同樣依計量變 =====================

    // 阿哲(zhe):看「同事情誼」——你把他推開→他封閉;建立交情→他願意一起扛
    private fun zheThread(pb: Int): ChatThread {
        val script = when (tier(pb)) {
            -1 -> listOf(
                ChatMsg("阿哲", "分帳金流串接卡關,有 bug 一直測不完。", "14:00", true),
                ChatMsg("阿哲", "下週一上線不可能,至少再兩週。", "14:01", true),
                ChatMsg("你", "這麼嚴重?", "14:01", false),
                ChatMsg("阿哲", "嗯。我自己處理,有結果再跟你說。", "14:03", true),
            )
            1 -> listOf(
                ChatMsg("阿哲", "早,分帳金流串接卡關,有個 bug 一直測不完。", "14:00", true),
                ChatMsg("阿哲", "下週一上線真的有風險,但我想跟你一起看怎麼壓時間。", "14:01", true),
                ChatMsg("你", "好,我們一起看。業務那邊我會說明。", "14:01", false),
                ChatMsg("阿哲", "謝啦,我把 bug 清單整理給你,我們對一下優先序。", "14:02", true),
            )
            else -> listOf(
                ChatMsg("阿哲", "早,分帳功能金流串接卡關,有個 bug 一直測不完。", "14:00", true),
                ChatMsg("阿哲", "下週一上線不可能,至少再兩週。", "14:01", true),
                ChatMsg("你", "這麼嚴重?業務那邊知道了嗎", "14:01", false),
                ChatMsg("你", "Vivian 一直催,她說客戶不能跳票", "14:01", false),
                ChatMsg("阿哲", "我先看一下狀況,等等回你。", "14:02", true),
            )
        }
        return ChatThread("阿哲", R.drawable.colleague_quiet, "", Color(0xFFCBD5E1), "工程組長 · 線上", script)
    }
    private fun zhePrev(pb: Int) = when (tier(pb)) {
        -1 -> "我自己處理,有結果再說。"
        1 -> "我把 bug 清單整理給你,一起對。"
        else -> "下週一上線不可能,至少再兩週。"
    }

    // Vivian:看「同事情誼」
    private fun vivianThread(pb: Int): ChatThread {
        val script = when (tier(pb)) {
            -1 -> listOf(
                ChatMsg("Vivian", "分帳 demo 下週三客戶要看,時間我先答應了。", "13:55", true),
                ChatMsg("Vivian", "工程說要延。你那邊能生個能跑的版本嗎?", "13:56", true),
                ChatMsg("你", "我去問阿哲。", "13:57", false),
                ChatMsg("Vivian", "好,那就麻煩你了。", "13:58", true),
            )
            else -> listOf(
                ChatMsg("Vivian", "分帳的 demo 下週三客戶要看,時間我先答應了。", "13:55", true),
                ChatMsg("Vivian", "工程說要延,但這場真的不能跳票 🙏", "13:56", true),
                ChatMsg("你", "我了解,我去跟阿哲確認能不能先生一個 demo 版本", "13:57", false),
                ChatMsg("Vivian", if (tier(pb) == 1) "拜託你了!有你頂著我安心多了 😭" else "拜託你了!有你頂著我安心多了", "13:58", true),
            )
        }
        return ChatThread("Vivian", R.drawable.colleague_vivian, "", Color(0xFFCBD5E1), "業務 · 線上", script)
    }
    private fun vivianChatPrev(pb: Int) = if (tier(pb) == -1) "那就麻煩你了。" else "客戶不能跳票"

    // Ken:看「主管信任」
    private fun kenThread(mt: Int): ChatThread {
        val script = when (tier(mt)) {
            -1 -> listOf(
                ChatMsg("Ken", "看一下你信箱,分帳的事我寄給你了。", "13:45", true),
                ChatMsg("Ken", "5 點前給我建議,別拖,理由寫清楚。", "13:45", true),
                ChatMsg("你", "收到。", "13:46", false),
            )
            1 -> listOf(
                ChatMsg("Ken", "看一下你信箱,分帳的事我寄給你了。", "13:45", true),
                ChatMsg("Ken", "你看完回我就好,不急,我相信你的判斷。", "13:45", true),
                ChatMsg("你", "收到,我看完馬上回您。", "13:46", false),
            )
            else -> listOf(
                ChatMsg("Ken", "看一下你信箱,分帳的事我寄給你了。", "13:45", true),
                ChatMsg("Ken", "5 點前給我建議,記得寫清楚理由。", "13:45", true),
                ChatMsg("你", "收到,我看完馬上回您。", "13:46", false),
            )
        }
        return ChatThread("Ken", R.drawable.ken_neutral, "", Color(0xFFCBD5E1), "你的主管 · 線上", script)
    }
    private fun kenChatPrev(mt: Int) = when (tier(mt)) {
        -1 -> "5 點前給我建議,別拖。"
        1 -> "你看完回我就好,不急。"
        else -> "看一下你信箱"
    }

    // 固定對話(群組/公告/家人)
    private fun staticThread(id: String): ChatThread? = when (id) {
        "group" -> ChatThread("產品群組 (8)", null, "群", Color(0xFFF59E0B), "8 位成員",
            listOf(
                ChatMsg("阿哲", "阿哲:我先 push 一版分帳修正,大家測一下。", "13:28", true),
                ChatMsg("Vivian", "Vivian:客戶下週要 demo,範圍能先確認嗎?", "13:29", true),
                ChatMsg("Ken", "Ken:今天 5 點前我要一份建議,誰整理?", "13:30", true),
                ChatMsg("你", "我來整理,等等貼到決議。", "13:31", false),
            ))
        "notice" -> ChatThread("NovaPay 公告", null, "公", Color(0xFF6B7280), "官方帳號",
            listOf(
                ChatMsg("NovaPay 公告", "【系統維護】今晚 23:00 起例行維護,預計 30 分鐘。", "11:05", true),
                ChatMsg("NovaPay 公告", "維護期間部分服務暫停,造成不便敬請見諒。", "11:05", true),
            ))
        "mom" -> ChatThread("媽", null, "媽", Color(0xFFB85C3A), "家人",
            listOf(
                ChatMsg("媽", "記得吃飯,不要又熬夜。", "昨天", true),
                ChatMsg("媽", "工作再忙也要顧身體啊。", "昨天", true),
                ChatMsg("你", "知道啦,我會早點睡。", "昨天", false),
            ))
        else -> null
    }

    override fun chatThread(id: String, day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): ChatThread? = when (id) {
        "zhe" -> zheThread(pb)
        "vivian" -> vivianThread(pb)
        "ken" -> kenThread(mt)
        else -> staticThread(id)
    }

    override fun chatRows(day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): List<ChatRow> = listOf(
        ChatRow("zhe", "阿哲", zhePrev(pb), "14:01", "2", true, true, false, res = R.drawable.colleague_quiet),
        ChatRow("vivian", "Vivian", vivianChatPrev(pb), "13:58", "1", true, true, false, res = R.drawable.colleague_vivian),
        ChatRow("group", "產品群組 (8)", "[阿哲] 我先 push 一版,大家測一下", "13:30", "9", false, false, false, group = true, bg = Color(0xFFFFE0B2)),
        ChatRow("ken", "Ken", kenChatPrev(mt), "13:45", "", true, false, false, res = R.drawable.ken_neutral),
        ChatRow("notice", "NovaPay 公告", "【系統維護】今晚 23:00 起例行維護", "11:05", "", false, false, true, letter = "公", bg = Color(0xFF6B7280)),
        ChatRow("mom", "媽", "記得吃飯,不要又熬夜", "昨天", "", false, false, false, letter = "媽", bg = Color(0xFFB85C3A)),
    )

    // ===================== 行事曆(NovaCalendar):依「第幾天」顯示當天行程 =====================
    private val purple = Color(0xFF8B5CF6)
    private val red = Color(0xFFEF4444)

    override fun calendarWeek(day: Int): List<WeekDay> {
        val sel = (if (day < 1) 1 else if (day > 5) 5 else day) + 22   // Day1→23 … Day5→27
        val nums = listOf("22", "23", "24", "25", "26", "27", "28")
        val labels = listOf("日", "一", "二", "三", "四", "五", "六")
        return nums.indices.map { WeekDay(labels[it], nums[it], nums[it].toInt() == sel) }
    }

    override fun calendarEvents(day: Int): List<CalEvent> = when (if (day < 1) 1 else day) {
        2 -> listOf(
            CalEvent("清理信箱", "09:30 · 收件匣", BrandOrange, current = true,
                note = "今天信會炸;先分優先序,別被最吵的那封綁架。"),
            CalEvent("與 Vivian 對 demo", "11:00 · 客戶案", purple,
                note = "確認 demo 真正要展示什麼,別當場開天窗。"),
            CalEvent("Sprint 站會", "11:30–11:45 · 線上", AccentGreen,
                note = "同步昨天決議造成的影響。"),
            CalEvent("看 CI 失敗", "14:00 · wallet-team", red,
                note = "build #4821 掛了,先確認是不是擋上線的關鍵。"),
        )
        3 -> listOf(
            CalEvent("會前準備", "09:30 · 整理立場", AccentBlue,
                note = "想清楚你要主張什麼,別到會議室才想。"),
            CalEvent("跨部門排程會議", "10:30–11:30 · 大會議室", BrandOrange, current = true,
                note = "今天的硬仗:排程要調,每條路都有人受傷。"),
            CalEvent("Sprint 站會", "13:30 · 線上", AccentGreen,
                note = "把會議決議同步給團隊。"),
            CalEvent("更新分帳 spec", "15:00 · 文件", purple,
                note = "把今天定的範圍寫回文件。"),
        )
        4 -> listOf(
            CalEvent("Sprint 站會", "11:00 · 線上", AccentGreen,
                note = "進度同步。"),
            CalEvent("團隊午餐", "12:00 · 三樓交誼廳", BrandAmber, current = true,
                note = "HR 辦的聚餐,放鬆一下,順便修補關係。"),
            CalEvent("阿哲的工具分享", "15:00 · 茶水間", AccentBlue,
                note = "他週末寫的東西;捧個場,關係會不一樣。"),
        )
        5 -> listOf(
            CalEvent("週報整理", "10:00 · 個人", AccentBlue,
                note = "把這週做的整理一下,等下 Ken 會問。"),
            CalEvent("與 Ken 週回顧", "16:00 · 會議室 A", BrandOrange, current = true,
                note = "第一週收尾;誠實看自己哪裡好、哪裡會重來。"),
            CalEvent("週五收工", "18:00", BrandAmber,
                note = "撐過第一週了。"),
        )
        else -> listOf(
            CalEvent("與 Ken 的 1on1", "09:00–09:30 · 會議室 A", AccentBlue,
                note = "帶上分帳問題的初步判斷,Ken 會問你怎麼看。"),
            CalEvent("Sprint 站會", "11:00–11:15 · 線上", AccentGreen,
                note = "簡短同步進度;阿哲可能會提排程風險。"),
            CalEvent("午餐 · 小芳", "12:00", BrandAmber,
                note = "認識同事的好機會,別整頓飯都在講工作。"),
            CalEvent("讀分帳 spec", "14:00–15:30 · 專注時段", BrandOrange, current = true,
                note = "今天的重點:把分帳邏輯讀透,下午要做判斷。"),
            CalEvent("回 Vivian", "16:00 · 客戶需求", purple,
                note = "客戶 demo 在催,回覆前先確認工程實際可行的範圍。"),
        )
    }

    // ===================== 內部社群(NovaGram):依「同事情誼」顯示團隊氣氛 =====================
    override fun gramPosts(pb: Int): List<GramPost> = when (tier(pb)) {
        -1 -> listOf(
            GramPost(R.drawable.feed_1, "又是一個人加班,辦公室只剩我的鍵盤聲", "12"),
            GramPost(R.drawable.feed_2, "新環境第一週,還在找誰能說上話", "31"),
            GramPost(R.drawable.feed_3, "週末上山,把一週的悶氣都丟在山上", "54"),
        )
        1 -> listOf(
            GramPost(R.drawable.feed_1, "加班後同事揪夜市,原來大家都不容易", "96"),
            GramPost(R.drawable.feed_2, "新辦公室第一週,還好遇到一群願意罩的人", "152"),
            GramPost(R.drawable.feed_3, "週末上山充電,下週跟夥伴繼續拚", "188"),
        )
        else -> listOf(
            GramPost(R.drawable.feed_1, "加班後的小確幸,夜市犒賞自己", "42"),
            GramPost(R.drawable.feed_2, "新辦公室第一週,假裝自己很從容", "88"),
            GramPost(R.drawable.feed_3, "週末上山把腦袋清空,下週再戰", "126"),
        )
    }
}
