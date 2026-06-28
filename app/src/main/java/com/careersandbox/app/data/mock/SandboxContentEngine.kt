package com.careersandbox.app.data.mock

import androidx.compose.ui.graphics.Color
import com.careersandbox.app.R

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

interface SandboxContentEngine {
    fun inbox(day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): List<MailRow>
    fun mailBody(id: String, day: Int, flags: List<String>, mt: Int, pb: Int, pi: Int): MailBody?
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
}
