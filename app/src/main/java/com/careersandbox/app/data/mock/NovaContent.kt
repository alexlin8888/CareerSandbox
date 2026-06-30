package com.careersandbox.app.data.mock

/* =====================================================================
   Nova 套件內容(目前 mock;預留 remote 化 + 預載快取)
   - 各 Nova 畫面的展示內容集中於此,成為單一抽換點。
   - preload():預載快取鉤子。現為同步回傳寫死值;之後可改成 app/沙盒進場時
     非同步從後端拉、存入快取,讓 Nova 畫面開啟即有內容、免 loading。
   - 接後端時:換掉 default*() 的 body 即可,各畫面讀取的介面不變。
   目前已接:NovaDoc(決議文件)。其餘 Nova 畫面可逐步遷移到此。
   ===================================================================== */
object NovaContent {

    data class DocScope(val label: String, val text: String, val done: Boolean)

    data class DecisionDoc(
        val breadcrumb: String,
        val title: String,
        val owner: String,
        val status: String,
        val goals: List<String>,
        val scopes: List<DocScope>,
        val riskTitle: String,
        val riskBody: String,
    )

    // ---- 預載快取 ----
    private var cachedDoc: DecisionDoc? = null

    /** 預載鉤子:於 Nova/沙盒進場呼叫,預先暖快取(現為同步;remote 化後改非同步) */
    fun preload() {
        if (cachedDoc == null) cachedDoc = defaultDecisionDoc()
    }

    /** 決議文件內容(快取;首次存取自動暖快取) */
    val decisionDoc: DecisionDoc
        get() = cachedDoc ?: defaultDecisionDoc().also { cachedDoc = it }

    private fun defaultDecisionDoc() = DecisionDoc(
        breadcrumb = "NovaPay · 產品 · 分帳上線決議",
        title = "分帳上線決議",
        owner = "你（PM）",
        status = "審核中",
        goals = listOf(
            "讓「一鍵分帳」在 demo 前可用、且不出事",
            "在工程、業務、品質之間取得共識",
        ),
        scopes = listOf(
            DocScope("基本版", "：手動建立分帳、平均拆分", done = true),
            DocScope("進階版", "（下一版）：自動金流串接", done = false),
        ),
        riskTitle = "金流串接 race condition，bug 未解",
        riskBody = "硬上線 → demo 出錯機率高；延期 → 對客戶承諾跳票。",
    )
}
