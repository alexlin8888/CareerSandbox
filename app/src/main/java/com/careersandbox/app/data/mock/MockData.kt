package com.careersandbox.app.data.mock

import com.careersandbox.app.data.model.*

object MockData {

    val currentUser = User(
        id = "u1",
        name = "Alex",
        school = "國立政治大學",
        department = "資訊管理學系",
        year = "大三",
        interests = listOf("產品經理", "使用者研究", "資料分析"),
        skillsHave = listOf("Excel", "簡報製作", "SQL"),
        skillsWant = listOf("Figma", "Python", "A/B 測試"),
    )

    val homeStat = HomeStat(
        resumeCompletion = 64,
        weeklyInterviews = 2,
        recommendedJobs = 12,
    )

    val experiences = listOf(
        Experience("e1", "校內社團行銷組長", "社團", "2024.09 - 2025.06",
            "負責年度成發活動的社群推廣,從零開始建立 IG 帳號,九個月內累積 1200 名追蹤。",
            listOf("領導", "內容創作", "數據追蹤")),
        Experience("e2", "電商公司資料分析實習", "工作", "2025.07 - 2025.09",
            "協助業務團隊整理銷售數據,用 SQL + Excel 產出週報。",
            listOf("分析", "SQL", "報表")),
        Experience("e3", "全國商業個案分析競賽", "競賽", "2025.03",
            "四人組隊分析某連鎖餐飲品牌轉型策略,獲得佳作。",
            listOf("跨團隊", "策略", "簡報")),
        Experience("e4", "資料庫系統課程專題", "學業", "2024.09 - 2025.01",
            "用 PostgreSQL 與 React 做出一個小型圖書管理系統。",
            listOf("技術", "全端")),
    )

    val resumes = listOf(
        Resume("r1", "產品經理-綜合版", "Junior PM", "今天", "v3", 82),
        Resume("r2", "資料分析師-技能強化", "Data Analyst", "3 天前", "v1", 64),
        Resume("r3", "UX 研究員-草稿", "UX Researcher", "2 週前", "v0", 32),
    )

    val interviewHistory = listOf(
        InterviewRecord("i1", InterviewType.INDIVIDUAL, "Junior PM", 78, "今天 14:20"),
        InterviewRecord("i2", InterviewType.GROUP, "管理顧問", 72, "昨天"),
        InterviewRecord("i3", InterviewType.INDIVIDUAL, "Data Analyst", 65, "3 天前"),
        InterviewRecord("i4", InterviewType.GROUP, "行銷企劃", 70, "上週"),
    )

    val notifications = listOf(
        NotificationItem("n1", "履歷 AI 建議", "「電商實習」段落可以加上量化數字", "10 分鐘前"),
        NotificationItem("n2", "面試提醒", "你設定了下午 4 點要練習一場面試", "1 小時前"),
        NotificationItem("n3", "競賽截止提醒", "全國行銷大賽還剩 3 天報名", "今天早上"),
    )

    val individualInterviewScript = listOf(
        ChatMessage("m1", "面試官", "先請你做一個簡短的自我介紹,大約一分鐘。", isInterviewer = true),
        ChatMessage("m2", "你", "你好,我是政大資管系大三的 Alex,過去主要做過社團行銷和資料分析實習,想往產品經理發展。", isUser = true),
        ChatMessage("m3", "面試官", "你提到做過社團行銷,可以講一個你覺得做得不太好的決定嗎?", isInterviewer = true),
        ChatMessage("m4", "你", "我們曾經辦過一場聯名活動,前期沒有先測試小規模就直接全推,結果觸及只有預期的三成。", isUser = true),
        ChatMessage("m5", "面試官", "你從這件事學到什麼?具體下次會怎麼做?", isInterviewer = true),
    )

    val groupInterviewScript = listOf(
        ChatMessage("g1", "主考官", "今天的個案是:某連鎖咖啡品牌想推年輕客群,預算有限。請小組討論 20 分鐘。", isInterviewer = true),
        ChatMessage("g2", "AI-強勢", "我先開個頭,我覺得應該直接做 IG 短影音,成本低觸及高。", isUser = false),
        ChatMessage("g3", "AI-邏輯", "等一下,我們應該先定義「年輕客群」是誰、預算具體多少,不然會發散。", isUser = false),
        ChatMessage("g4", "AI-親切", "邏輯說得有道理。要不我們先分工:一個人查市場、一個人想創意、一個人做時程?", isUser = false),
        ChatMessage("g5", "你", "我同意先定義範圍。如果預算 50 萬以下,我傾向把錢花在跟在地創作者合作而不是大規模投廣告。", isUser = true),
        ChatMessage("g6", "AI-沉默", "(點頭) 我支持 Alex 的方向,但需要評估合作對象的真實影響力。", isUser = false),
        ChatMessage("g7", "AI-強勢", "創作者合作週期太長,我們可能來不及做完整 case。", isUser = false),
        ChatMessage("g8", "主考官", "時間剩 10 分鐘,請開始收斂結論。", isInterviewer = true),
    )

    val jobInterests = listOf(
        "行銷企劃", "資料分析", "使用者研究", "產品經理",
        "軟體工程", "視覺設計", "人力資源", "業務開發",
        "財務管理", "內容創作", "客戶成功", "營運專案"
    )

    val skills = listOf(
        "Excel", "SQL", "Python", "Figma", "簡報製作",
        "數據視覺化", "專案管理", "提案撰寫", "使用者訪談",
        "A/B 測試", "SEO", "廣告投放", "Notion", "Tableau"
    )
}
