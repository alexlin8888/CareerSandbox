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

    // 8 篇真實學職涯文章(內容改寫整理,來源見 url)
    val articles = listOf(
        Article(
            id = "a1",
            category = ArticleCategory.RESUME,
            title = "人資愛看的履歷怎麼寫?7 大重點與範例",
            excerpt = "若已有一定工作經驗,「學歷」段落應放在「工作經驗」之後。重點集中在實戰經歷上。專業技能放最後當總結。",
            source = "Cake 求職平台",
            publishedDate = "2025.10",
            readMinutes = 8,
            url = "https://www.cake.me/resources/resume/resume-outline-and-samples",
            bodyContent = listOf(
                ArticleBlock.Paragraph("好的履歷不是把所有經歷塞滿,而是把對的東西放在對的位置。人資每天看上百份履歷,平均停留時間 6-10 秒,排版邏輯決定你能不能進到下一輪。"),
                ArticleBlock.Heading("一、個人資訊"),
                ArticleBlock.Paragraph("姓名、聯絡方式、應徵職位這三項放最上面。一張清楚的照片可以加分,但不是必要。LinkedIn / GitHub / 作品集連結也放這區。"),
                ArticleBlock.Heading("二、自傳精華(個人簡介)"),
                ArticleBlock.Paragraph("400 字以內,用一段話講清楚:你是誰、有什麼能力、為什麼適合這份工作。避免寫成流水帳。"),
                ArticleBlock.Heading("三、工作經歷"),
                ArticleBlock.Paragraph("如果已有工作經驗,這段放在學歷之前。每段經歷至少包含:公司名稱、職位、時間、3-5 項具體成果(帶數字)。"),
                ArticleBlock.Heading("四、學歷"),
                ArticleBlock.Paragraph("簡潔列出最高學歷至大學資訊即可。新鮮人可以加上 GPA(3.5 以上才寫)、重要修課。"),
                ArticleBlock.Heading("五、專業技能"),
                ArticleBlock.BulletList(listOf(
                    "硬實力:工具、程式語言、證照(分類列點)",
                    "軟實力:溝通、領導、專案管理(舉例佐證)",
                    "語言能力:標示等級(如 TOEIC 850、日檢 N2)",
                )),
                ArticleBlock.Quote("一個項目的描述,通常一到兩句重點即可。把履歷通篇劃重點等於沒有重點。"),
                ArticleBlock.Heading("六、作品集"),
                ArticleBlock.Paragraph("如果有 GitHub / Behance / Medium / 個人網站,放最後。3-5 個代表作就夠了,精挑而非求多。"),
                ArticleBlock.Heading("七、量身打造"),
                ArticleBlock.Paragraph("每投一個職缺,履歷至少要調整 30% — 把跟這份工作相關的經驗往前排,關鍵字對應到 JD。一份履歷打天下的時代已經過了。"),
            ),
        ),
        Article(
            id = "a2",
            category = ArticleCategory.INTERVIEW,
            title = "面試自我介紹怎麼說?4 步驟打造亮眼第一印象",
            excerpt = "30 秒、1 分鐘、3 分鐘自我介紹範本。避免過度詳述工作經歷、講與工作無關的興趣、過於謙虛、誇大其詞四大地雷。",
            source = "Yourator",
            publishedDate = "2025.05",
            readMinutes = 6,
            url = "https://www.yourator.co/articles/80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("自我介紹是面試的第一題,也是面試官評估表達能力與邏輯思考的關鍵時刻。但 90% 的人都在這一題就被默默扣分。"),
                ArticleBlock.Heading("為什麼自我介紹很關鍵?"),
                ArticleBlock.Paragraph("面試官想透過自我介紹快速了解兩件事:一、你的背景跟職位適配度;二、你的表達能力與邏輯思考。所以不能流水帳念履歷,要展現選擇與重點。"),
                ArticleBlock.Heading("自我介紹三段架構"),
                ArticleBlock.BulletList(listOf(
                    "開場:一句話概括你的職涯定位 + 強項",
                    "中段:挑 1-2 個最相關的經歷詳述(用 STAR 原則)",
                    "收尾:為什麼是這家公司、能帶來什麼價值",
                )),
                ArticleBlock.Heading("依時間調整深度"),
                ArticleBlock.Paragraph("