package com.careersandbox.app.data.local

/** One career direction from the AI service catalog. */
data class CatalogCareer(
    val id: String,
    val title: String,
    val subtitleEn: String,
    val category: String,
    val salary: String,
    val openings: String,
    val requiredSkills: List<String>,
)

/**
 * Copy of CareerSandboxModule fixtures/careers/careers_v1.json (commit c52ff24, 2026-10-25),
 * the same 30 directions POST /career/recommend scores against.
 * Used only to show a few random directions before the user searches, so opening the
 * career page needs no AI call. Refresh it when the model team changes the catalog.
 */
object CareerCatalog {
    val all: List<CatalogCareer> = listOf(
        CatalogCareer(
            id = "software_engineer", title = "軟體工程師", subtitleEn = "Software Engineer",
            category = "工程", salary = "38-62k", openings = "58",
            requiredSkills = listOf("軟體程式設計", "JavaScript", "HTML", "軟體工程系統開發", "CSS", "C++", "系統整合分析", "系統架構規劃"),
        ),
        CatalogCareer(
            id = "frontend_engineer", title = "前端工程師", subtitleEn = "Frontend Engineer",
            category = "工程", salary = "依市場", openings = "19",
            requiredSkills = listOf("Git", "JavaScript", "CSS", "VueJS", "軟體程式設計", "網頁語法撰寫", "HTML", "使用者介面設計"),
        ),
        CatalogCareer(
            id = "fullstack_engineer", title = "全端工程師", subtitleEn = "Fullstack Engineer",
            category = "工程", salary = "依市場", openings = "14",
            requiredSkills = listOf("ASP.NET", "C#", "JavaScript"),
        ),
        CatalogCareer(
            id = "ai_data_engineer", title = "AI／資料工程師", subtitleEn = "AI/Data Engineer",
            category = "數據", salary = "依市場", openings = "10",
            requiredSkills = listOf("Airflow", "Docker", "ETL", "Linux", "Python", "SQL", "Spark", "資料倉儲", "資料建模", "雲端平台"),
        ),
        CatalogCareer(
            id = "qa_engineer", title = "測試／品保工程師", subtitleEn = "QA Engineer",
            category = "工程", salary = "30-35k", openings = "20",
            requiredSkills = listOf("Excel", "Word", "新產品與進出貨檢驗", "原料及產品品質管制監控", "Outlook", "規劃並執行品質管理系統", "ISO 9000", "產品驗證作業"),
        ),
        CatalogCareer(
            id = "network_engineer", title = "網路／系統工程師", subtitleEn = "Network Engineer",
            category = "工程", salary = "依市場", openings = "10",
            requiredSkills = listOf("作業系統基本操作", "安裝與維護網路安全系統", "規劃與管理防火牆", "資訊設備操作檢修", "資料備份與復原", "資訊設備環境設定", "系統架構規劃與維護"),
        ),
        CatalogCareer(
            id = "it_support", title = "資訊支援人員", subtitleEn = "IT Support",
            category = "工程", salary = "32-38k", openings = "6",
            requiredSkills = listOf("Word", "Excel", "PowerPoint", "網路設備設定安裝", "作業系統基本操作", "系統架構規劃與維護", "資訊設備環境設定", "電腦設備裝配"),
        ),
        CatalogCareer(
            id = "data_analyst", title = "資料／數據分析師", subtitleEn = "Data Analyst",
            category = "數據", salary = "依市場", openings = "11",
            requiredSkills = listOf("A/B 測試", "Python", "SQL", "統計", "資料視覺化"),
        ),
        CatalogCareer(
            id = "pm", title = "產品／專案管理", subtitleEn = "Product Planner",
            category = "產品", salary = "38-50k", openings = "118",
            requiredSkills = listOf("A/B 測試", "PRD 撰寫", "SQL", "使用者訪談"),
        ),
        CatalogCareer(
            id = "uiux_designer", title = "UI／UX 設計師", subtitleEn = "UI/UX Designer",
            category = "設計", salary = "50-70k", openings = "10",
            requiredSkills = listOf("Figma", "Illustrator", "設計表現能力", "電腦排版設計", "Adobe Photoshop", "使用者介面設計", "使用者體驗設計", "網頁語法撰寫"),
        ),
        CatalogCareer(
            id = "graphic_designer", title = "視覺／平面設計師", subtitleEn = "Graphic Designer",
            category = "設計", salary = "32-40k", openings = "47",
            requiredSkills = listOf("Adobe Photoshop", "Illustrator", "視覺設計相關知識", "產品包裝設計", "設計表現能力", "繪圖工具與軟體操作", "各式封面設計", "廣告設計概念"),
        ),
        CatalogCareer(
            id = "content_creator", title = "數位內容製作", subtitleEn = "Content Creator",
            category = "設計", salary = "30-45k", openings = "7",
            requiredSkills = listOf("Illustrator", "Adobe Photoshop", "Premiere", "Word", "PowerPoint", "Excel", "短影音", "Instagram"),
        ),
        CatalogCareer(
            id = "marketing", title = "行銷企劃", subtitleEn = "Marketing",
            category = "行銷", salary = "32-40k", openings = "211",
            requiredSkills = listOf("Excel", "PowerPoint", "Word", "社群媒體經營管理", "廣告企劃案╱文案撰寫", "品牌行銷管理", "網路活動規劃與執行", "行銷策略擬定"),
        ),
        CatalogCareer(
            id = "sales", title = "業務銷售", subtitleEn = "Sales",
            category = "業務", salary = "32-40k", openings = "332",
            requiredSkills = listOf("Excel", "Word", "PowerPoint", "產品介紹及解說銷售", "Outlook", "業務或通路開發", "業績目標分配與績效達成", "行政事務處理"),
        ),
        CatalogCareer(
            id = "customer_service", title = "客服人員", subtitleEn = "Customer Service",
            category = "客服", salary = "31-38k", openings = "103",
            requiredSkills = listOf("Excel", "客訴案件處理", "中文打字20~50", "Word", "電話接聽與人員接待事項", "文件或資料輸入建檔處理", "行政事務處理", "客戶資料更新維護"),
        ),
        CatalogCareer(
            id = "hr", title = "人力資源", subtitleEn = "HR Specialist",
            category = "人資", salary = "33-45k", openings = "97",
            requiredSkills = listOf("具備人力資源相關知識", "Excel", "Word", "PowerPoint", "執行日常招募業務", "勞工保險相關法規", "面試技巧", "績效與薪酬管理"),
        ),
        CatalogCareer(
            id = "finance_accounting", title = "財務會計", subtitleEn = "Finance Accounting",
            category = "財會", salary = "32-39k", openings = "125",
            requiredSkills = listOf("Excel", "Word", "會計核算和帳務處理", "結帳作業與帳務處理", "會計或審計專業知識", "編製帳務報表", "代辦會計相關事項", "Outlook"),
        ),
        CatalogCareer(
            id = "procurement", title = "採購／資材", subtitleEn = "Procurement",
            category = "採購", salary = "30-36k", openings = "119",
            requiredSkills = listOf("Excel", "Word", "商品進貨入庫作業", "倉庫貨物標示╱置放╱盤點", "理貨╱揀貨╱補貨作業", "商品收貨╱退貨作業", "簽收╱驗收作業", "倉儲系統維護與管理"),
        ),
        CatalogCareer(
            id = "logistics", title = "倉儲物流", subtitleEn = "Logistics",
            category = "物流", salary = "30-36k", openings = "112",
            requiredSkills = listOf("理貨╱揀貨╱補貨作業", "倉庫貨物標示╱置放╱盤點", "Excel", "商品進貨入庫作業", "商品收貨╱退貨作業", "Word", "簽收╱驗收作業", "倉儲系統維護與管理"),
        ),
        CatalogCareer(
            id = "admin", title = "行政／總務", subtitleEn = "Admin",
            category = "行政", salary = "30-35k", openings = "400",
            requiredSkills = listOf("Excel", "Word", "行政事務處理", "PowerPoint", "文件或資料輸入建檔處理", "文書處理╱排版能力", "文件收發與檔案管理", "文件檔案資料處理、轉換及整合工作"),
        ),
        CatalogCareer(
            id = "operations_mgmt", title = "營運／門市管理", subtitleEn = "Operations Mgmt",
            category = "營運", salary = "0-35k", openings = "322",
            requiredSkills = listOf("櫃檯門市接待與需求服務", "產品介紹及解說銷售", "客訴案件處理", "Excel", "Word", "業績目標分配與績效達成", "電話接聽與人員接待事項", "客戶情報蒐集"),
        ),
        CatalogCareer(
            id = "reception", title = "櫃檯／接待", subtitleEn = "Reception",
            category = "服務", salary = "30-35k", openings = "66",
            requiredSkills = listOf("Excel", "Word", "櫃檯門市接待與需求服務", "行政事務處理", "電話接聽與人員接待事項", "文件或資料輸入建檔處理", "文書處理╱排版能力", "客訴案件處理"),
        ),
        CatalogCareer(
            id = "beauty", title = "美容美療", subtitleEn = "Beauty",
            category = "服務", salary = "29-35k", openings = "24",
            requiredSkills = listOf("了解皮膚構造與特性", "臉部保養技巧", "基本按摩手法", "基礎潔膚╱卸妝技巧", "美容器材使用", "美容業經營概念", "SPA護理知識", "Excel"),
        ),
        CatalogCareer(
            id = "food_service", title = "餐飲服務", subtitleEn = "Food Service",
            category = "服務", salary = "0-38k", openings = "89",
            requiredSkills = listOf("食物製前準備", "門市餐飲管理", "食材貯存管理", "飲料調製", "吧台前置作業準備", "櫃檯門市接待與需求服務", "產品介紹及解說銷售", "排盤與食品裝飾"),
        ),
        CatalogCareer(
            id = "driver", title = "駕駛／配送", subtitleEn = "Driver",
            category = "運輸", salary = "33-48k", openings = "40",
            requiredSkills = listOf("貨物運送與資材配送", "車輛清潔保管與檢修", "載送乘客與行李運送服務", "車故╱貨故問題處理能力", "運輸路線規劃管理", "商品收貨╱退貨作業"),
        ),
        CatalogCareer(
            id = "technician", title = "技術／維修", subtitleEn = "Technician",
            category = "技術", salary = "29-32k", openings = "220",
            requiredSkills = listOf("貨品包裝作業", "理貨╱揀貨╱補貨作業", "倉庫貨物標示╱置放╱盤點", "達成產能及出貨目標", "Excel", "Word", "簽收╱驗收作業", "商品進貨入庫作業"),
        ),
        CatalogCareer(
            id = "travel", title = "旅遊／旅行社", subtitleEn = "Travel",
            category = "服務", salary = "29-35k", openings = "8",
            requiredSkills = listOf("Excel", "Word", "旅遊安全與緊急事件處理", "團體旅遊相關食宿安排", "活動主持與場控", "景點文物導覽解說", "緊急事件危機處理", "領隊帶團技巧"),
        ),
        CatalogCareer(
            id = "legal", title = "法務", subtitleEn = "Legal",
            category = "法務", salary = "32-38k", openings = "14",
            requiredSkills = listOf("Word", "Excel", "執行法律文件及契約撰寫", "文件收發與檔案管理", "檢驗各項文件之法律效力", "PowerPoint", "文書處理╱排版能力", "檢驗法律紀錄和相關文件"),
        ),
        CatalogCareer(
            id = "public_sector", title = "公部門", subtitleEn = "Public Sector",
            category = "公職", salary = "36-36k", openings = "5",
            requiredSkills = listOf("Excel", "Word", "行政事務處理", "中文打字20~50", "文件收發與檔案管理", "文書處理╱排版能力", "報表彙整與管理", "電話接聽與人員接待事項"),
        ),
        CatalogCareer(
            id = "healthcare", title = "醫護", subtitleEn = "Healthcare",
            category = "醫護", salary = "31-40k", openings = "84",
            requiredSkills = listOf("Excel", "產品介紹及解說銷售", "Word", "PowerPoint", "業務或通路開發", "了解皮膚構造與特性", "中文打字20~50", "業績目標分配與績效達成"),
        ),
    )

    fun byId(id: String): CatalogCareer? = all.firstOrNull { it.id == id }

    fun randomPicks(n: Int = 5): List<CatalogCareer> = all.shuffled().take(n)
}