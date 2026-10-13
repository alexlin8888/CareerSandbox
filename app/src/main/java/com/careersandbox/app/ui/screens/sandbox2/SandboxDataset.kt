package com.careersandbox.app.ui.screens.sandbox2

data class DataLimit(
    val prompt: String,    // what the player notices when opening the column
    val engReply: String,  // reply when asking the engineer
    val pmReply: String,   // reply when asking the PM
)

data class DataColumn(
    val name: String,
    val desc: String,
    val note: String,
    val limit: DataLimit? = null,
)

/** Sample dataset for station 2. Replace columns/rows here when the real fake dataset is ready. */
object SandboxDataset {

    val columns = listOf(
        DataColumn("user_id", "匿名代號。", "跨系統一致"),
        DataColumn("註冊日", "帳號建立日。", "台灣時間"),
        DataColumn("方案", "free / plus / pro。", "以當月最後狀態為準，換方案不回填"),
        DataColumn(
            "稍後再看次數", "加進「稍後再看」的次數。", "2026-06-01 起記錄",
            DataLimit(
                prompt = "你發現六月以前註冊的使用者，這欄全部是 0。這代表他們沒有用，還是當時根本沒有記錄？",
                engReply = "六月前沒有，埋點那時候才上。硬要比只能比六月後註冊的人",
                pmReply = "喔 那先看六月後的就好，報告裡講一下",
            ),
        ),
        DataColumn("最近活躍", "最後開啟日。", "每日更新"),
        DataColumn(
            "次月留存", "註冊後第二個月有無活躍。", "活躍定義見阿凱；未滿一個月為空",
            DataLimit(
                prompt = "留存的「活躍」是阿凱定的，營運報表用的是另一個定義。你要用哪個？",
                engReply = "留存你用哪個定義？營運是看 30 天內有登入，我這邊是有開 app",
                pmReply = "用營運的吧 不然老闆會問為什麼跟報表不一樣",
            ),
        ),
        DataColumn(
            "客服詢問", "有無問過「能不能收藏」。", "人工標記，近三個月工單",
            DataLimit(
                prompt = "客服詢問只記到近三個月，而且是人工標的。三個月以前的需求看不到。",
                engReply = "客服那份是手動的，我這邊沒別的",
                pmReply = "那就寫近三個月",
            ),
        ),
    )

    val rows: List<List<String>> = listOf(
        listOf("u_1042", "2026-03-14", "free", "0", "09-10", "是", "否"),
        listOf("u_1187", "2026-05-02", "plus", "7", "09-15", "是", "是"),
        listOf("u_1203", "2026-06-21", "free", "2", "08-30", "否", "否"),
        listOf("u_1258", "2026-04-09", "pro", "12", "09-14", "是", "否"),
        listOf("u_1311", "2026-07-03", "free", "0", "07-20", "否", "否"),
        listOf("u_1376", "2026-06-30", "plus", "4", "09-13", "是", "是"),
        listOf("u_1402", "2026-08-18", "free", "1", "09-12", "", "否"),
        listOf("u_1455", "2026-02-27", "pro", "9", "09-15", "是", "否"),
    )
}