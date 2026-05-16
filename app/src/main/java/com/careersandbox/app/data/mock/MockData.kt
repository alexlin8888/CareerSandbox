package com.careersandbox.app.data.mock

import com.careersandbox.app.data.model.*

object MockData {

    val currentUser = User(
        id = "u1",
        name = "Alex",
        school = "國立政治大學",
        department = "資訊管理學系",
        year = "大三",
        email = "alex.lin@example.com",
        phone = "0912-345-678",
        bio = "資管系大三,專注於使用者研究與資料分析。曾在零售業實習做過 A/B 測試,也在新創接案做過產品邏輯設計。下一步想往 PM 方向發展,目標是進到關注亞洲市場的科技公司。",
        linkedin = "linkedin.com/in/alex-lin",
        github = "github.com/alexlin",
        portfolio = "alex-portfolio.cc",
        interests = listOf("產品經理", "使用者研究", "資料分析"),
        skillsHave = listOf("Excel", "簡報製作", "SQL", "Notion", "使用者訪談", "A/B 測試"),
        skillsWant = listOf("Figma", "Python", "GA4"),
        languages = listOf(
            LanguageProficiency("中文", "母語"),
            LanguageProficiency("英文", "TOEIC 875"),
            LanguageProficiency("日文", "JLPT N2"),
        ),
        activities = listOf(
            ActivityRecord(
                title = "政大資管之夜",
                role = "行銷組組長",
                period = "2024.09 - 2025.03",
                highlight = "IG 從 0 經營到 1200 追蹤,觸及破社團新高",
            ),
            ActivityRecord(
                title = "全國大專商業個案大賽",
                role = "隊長",
                period = "2024.10",
                highlight = "進入決賽,獲北區優勝(前 8 名)",
            ),
            ActivityRecord(
                title = "新創實習 - 電商產品團隊",
                role = "PM 實習生",
                period = "2025.07 - 2025.09",
                highlight = "主導 2 個 A/B 測試,轉換率提升 14%",
            ),
        ),
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
            coverImageUrl = "https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=800&q=80",
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
            coverImageUrl = "https://images.unsplash.com/photo-1521737711867-e3b97375f902?w=800&q=80",
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
                ArticleBlock.Paragraph("30 秒版本:只講開場 + 一個關鍵成就 + 動機。1 分鐘版:三段架構各 20 秒。3 分鐘版:中段可以舉 2-3 個例子,給更多細節跟結果。"),
                ArticleBlock.Heading("四大地雷"),
                ArticleBlock.BulletList(listOf(
                    "把履歷從頭念到尾(面試官早就看過了)",
                    "講與工作無關的興趣(打電動、追劇)",
                    "過於謙虛(「我什麼都不會,請多指教」)",
                    "誇大其詞(沒做過的事說得像主導)",
                )),
                ArticleBlock.Quote("自我介紹的關鍵目標是:在短時間內讓面試官留下好印象,展現強項、勾起興趣。"),
            ),
        ),
        Article(
            id = "a3",
            category = ArticleCategory.INTERVIEW,
            title = "STAR 原則:面試這樣答邏輯清楚超加分",
            excerpt = "Situation 情境、Task 任務、Action 行動、Result 結果。撰寫履歷時可用「做了什麼+產生什麼影響」。自我介紹也適用。",
            source = "Yourator",
            publishedDate = "2025.03",
            readMinutes = 5,
            url = "https://www.yourator.co/articles/177",
            coverImageUrl = "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("被問「請分享一次解決問題的經驗」,然後就在那裡支吾其詞 — STAR 原則就是為了避免這種尷尬發明的萬用框架。"),
                ArticleBlock.Heading("STAR 四步驟"),
                ArticleBlock.BulletList(listOf(
                    "S - Situation 情境:發生什麼事?背景是什麼?",
                    "T - Task 任務:你當時的目標是什麼?越具體越好。",
                    "A - Action 行動:你做了哪些事?重點來了,要具體!",
                    "R - Result 結果:成果如何?最好有數字。",
                )),
                ArticleBlock.Heading("實際範例"),
                ArticleBlock.Paragraph("Q:請分享一次解決問題的經驗。"),
                ArticleBlock.Paragraph("S:某次社團成發前一週,主視覺設計師臨時退出。"),
                ArticleBlock.Paragraph("T:我得在 7 天內補上設計、印製文宣、不影響活動進度。"),
                ArticleBlock.Paragraph("A:當天召集行銷組會議重分工,我自己學 Canva 接設計工作,聯絡備案印刷廠加速排程,並把宣傳改成短影片補強。"),
                ArticleBlock.Paragraph("R:活動如期舉辦,到場人數 220 人,比去年多 40%,IG 觸及創社團新高。"),
                ArticleBlock.Heading("不只面試,履歷也適用"),
                ArticleBlock.Paragraph("把履歷上每段經歷都用 STAR 拆一遍:「做了什麼」+「產生什麼影響」。比起「負責社團行銷」,「9 個月內把 IG 從 0 經營到 1200 追蹤」更有說服力。"),
                ArticleBlock.Quote("讓經驗說話,面試表現就會亮眼。"),
            ),
        ),
        Article(
            id = "a4",
            category = ArticleCategory.CAREER_EXPLORATION,
            title = "畢業後不知道做什麼?4 步驟釐清生涯方向",
            excerpt = "認識自己:看心理測驗、書籍、列特質。認識工作:看求職網職缺條件、實際接觸該領域工作者。配對:找到交集再下手。",
            source = "Yourator",
            publishedDate = "2024.05",
            readMinutes = 7,
            url = "https://www.yourator.co/articles/260",
            coverImageUrl = "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("大四了,朋友都在拿 offer,只有自己還不知道要做什麼 — 這是大多數人都會經歷的階段,不丟臉,也不用慌。"),
                ArticleBlock.Heading("Step 1:認識自己"),
                ArticleBlock.BulletList(listOf(
                    "心理測驗:MBTI、Holland、Strong Interest 都可參考",
                    "列出你做過最有成就感的 3-5 件事,找共通點",
                    "問身邊朋友:「你覺得我擅長什麼?」常會聽到自己沒注意到的優點",
                )),
                ArticleBlock.Heading("Step 2:認識工作"),
                ArticleBlock.Paragraph("最快的方法是上 104、Yourator、Cake 看具體職缺。看 JD 內容、required skills、薪資範圍,而不是只看職稱。同一個「行銷專員」在不同公司做的事可能差很多。"),
                ArticleBlock.Heading("Step 3:實際接觸"),
                ArticleBlock.Paragraph("找 3-5 個該領域的學長姊或前輩做生涯訪談,一杯咖啡的時間,問他們:每天工作內容、最痛苦/最有成就的點、入行建議。"),
                ArticleBlock.Heading("Step 4:配對下手"),
                ArticleBlock.Paragraph("把「自己擅長 + 喜歡」跟「市場需要 + 待遇合理」交集,選 2-3 個方向,各投 5-10 個職缺試水溫。面試本身就是最快的市場驗證。"),
                ArticleBlock.Quote("天無絕人之路 — 沒有人在 22 歲就確定一輩子要做什麼。先動起來再說。"),
            ),
        ),
        Article(
            id = "a5",
            category = ArticleCategory.CAREER_EXPLORATION,
            title = "找不到有熱情的工作?那就先找喜歡的能力",
            excerpt = "工作不會完完全全是自己喜歡的樣子。在現有工作中觀察、發掘小小成就感的點,再思考朝什麼方向擴展,設定下一個職涯方向。",
            source = "方格子 vocus",
            publishedDate = "2022.06",
            readMinutes = 6,
            url = "https://vocus.cc/article/628cb426fd8978000158de4b",
            coverImageUrl = "https://images.unsplash.com/photo-1521791136064-7986c2920216?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("「找到你的熱情就會成功」這句話害了很多人 — 因為大多數人根本沒有清晰的熱情,於是陷入無止盡的迷茫。"),
                ArticleBlock.Heading("熱情 ≠ 興趣"),
                ArticleBlock.Paragraph("興趣是你閒下來會做的事,熱情是「為了它你願意承受痛苦」。打電動是興趣,職業電競選手是熱情(因為他願意每天練到肩膀職業傷害)。"),
                ArticleBlock.Heading("反過來:從能力找熱情"),
                ArticleBlock.Paragraph("與其問「我熱愛什麼」,不如問「我擅長什麼,而且做的時候會有成就感」。一個餐飲系實習生在飯店推銷月餅做到同期第一名,結果發現自己不討厭跟人接觸 — 後來轉行做業務,做得比同期同事都好。"),
                ArticleBlock.Heading("找小成就感"),
                ArticleBlock.BulletList(listOf(
                    "下班後還願意回想的事,通常是你樂在其中的能力",
                    "別人覺得難但你做起來輕鬆,就是你的天賦",
                    "可以從現有工作裡找 — 你不需要立刻換工作才能找到熱情",
                )),
                ArticleBlock.Quote("工作不會完完全全是自己喜歡的樣子,打開天線,在現有工作中觀察你的小小樂趣。"),
            ),
        ),
        Article(
            id = "a6",
            category = ArticleCategory.RESUME,
            title = "新鮮人履歷怎麼寫?4 大履歷範本解析",
            excerpt = "實習目標應放在「能帶給企業何種益處」,而非懇求學習機會。實習規劃可分短中長期切入,提供人資評估動機與職缺適配度。",
            source = "Cake 求職平台",
            publishedDate = "2026.03",
            readMinutes = 9,
            url = "https://www.cake.me/resources/resume/fresh-grad-resume",
            coverImageUrl = "https://images.unsplash.com/photo-1523580494863-6f3031224c94?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("新鮮人沒工作經驗,履歷上最容易犯的錯就是「懇求學習機會」 — 但企業要的是能帶來價值的人,不是免費培訓學員。"),
                ArticleBlock.Heading("換位思考:企業在意什麼?"),
                ArticleBlock.BulletList(listOf(
                    "你能解決什麼問題、提升什麼產值",
                    "你跟其他候選人比有什麼特別之處",
                    "你會待多久,值不值得培訓投資",
                )),
                ArticleBlock.Heading("實習目標怎麼寫?"),
                ArticleBlock.Paragraph("錯誤寫法:「希望能藉由實習學習行銷實務,累積工作經驗。」(企業想:那我為什麼要請你?)"),
                ArticleBlock.Paragraph("正確寫法:「希望能將社群經營的實戰經驗(社團 IG 從 0 到 1200 追蹤)帶到貴公司的品牌經營上,在三個月內提升年輕族群的觸及率。」"),
                ArticleBlock.Heading("沒有實習經驗怎麼辦?"),
                ArticleBlock.BulletList(listOf(
                    "社團經驗:用數字量化(辦了 X 場活動、招募了 Y 人)",
                    "課堂專案:挑跟職位最相關的,寫成 mini case study",
                    "競賽經歷:即使沒得獎也可以講過程跟學到什麼",
                    "自學專案:GitHub、Medium、個人作品",
                )),
                ArticleBlock.Heading("實習規劃:長中短期"),
                ArticleBlock.Paragraph("短期(1-3 個月):熟悉工作流程,完成基礎任務。中期(3-6 個月):獨立負責 X 項目。長期(實習結束):建立 X 領域的 portfolio,延伸應徵正職。"),
                ArticleBlock.Quote("一份好的實習履歷,是讓人資相信「請這個人,值得」。"),
            ),
        ),
        Article(
            id = "a7",
            category = ArticleCategory.INTERVIEW,
            title = "新創實習履歷教學:求職信 3 大重點",
            excerpt = "精簡有力:擷取重點和求職優勢,200 字內讓人資留下印象。一句話原則:用一句話闡述為什麼對這家新創有興趣。",
            source = "Cake 求職平台",
            publishedDate = "2024.05",
            readMinutes = 5,
            url = "https://www.cake.me/resources/resume-samples-startup-internship-resume-tutorial",
            coverImageUrl = "https://images.unsplash.com/photo-1556761175-b413da4baf72?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("新創公司收到的履歷少、人資也少,一封寫得好的求職信(cover letter)幾乎能直接拿到面試 — 但寫得糟,則是直接 ban。"),
                ArticleBlock.Heading("一、精簡有力"),
                ArticleBlock.Paragraph("200 字內。新創 founder 自己看履歷,沒人有時間讀小說。把重點壓縮在 3-5 句:你是誰、為何適合、能帶來什麼、什麼時候可以開始。"),
                ArticleBlock.Heading("二、一句話原則"),
                ArticleBlock.Paragraph("用一句話講「為什麼是這家新創」。不要寫「貴公司產業前景看好」 — 這是廢話。"),
                ArticleBlock.Paragraph("好的版本:「我用過貴公司的 X 產品,在 Y 場景遇到 Z 痛點,看到 JD 上提到要解決這個方向,所以想加入。」"),
                ArticleBlock.Heading("三、開始時間"),
                ArticleBlock.Paragraph("最後一定要寫:你最快什麼時候可以開始、每週能 commit 幾天。新創缺人缺到燒眉毛,能立刻入手的候選人優先級立刻往上。"),
                ArticleBlock.Heading("最重要的:不能有錯字"),
                ArticleBlock.Paragraph("一個錯字 = 不細心 = 不適合。寫完一定要冷靜 5 分鐘再回來看一遍。"),
                ArticleBlock.Quote("精簡有力,200 字內讓人資對你留下印象 — 求職信是你的行銷文案,自己是商品。"),
            ),
        ),
        Article(
            id = "a8",
            category = ArticleCategory.WORKPLACE,
            title = "履歷專長興趣怎麼寫?新鮮人必看指南",
            excerpt = "興趣不必非常厲害,只要能替生活充電就算。挑選與應徵職務最相關的興趣專長,讓履歷上的專長顯得有說服力。",
            source = "Yourator",
            publishedDate = "2025.06",
            readMinutes = 5,
            url = "https://www.yourator.co/articles/323",
            coverImageUrl = "https://images.unsplash.com/photo-1517842645767-c639042777db?w=800&q=80",
            bodyContent = listOf(
                ArticleBlock.Paragraph("「興趣:看電影、聽音樂、運動」 — 這是 90% 新鮮人履歷的興趣欄,等於沒寫。"),
                ArticleBlock.Heading("為什麼要寫興趣?"),
                ArticleBlock.BulletList(listOf(
                    "破冰:面試官的小話題,讓對話自然",
                    "個性側面:展現你怎麼過生活、你是什麼樣的人",
                    "潛在 match:也許跟團隊文化或某個同事的興趣呼應",
                )),
                ArticleBlock.Heading("興趣不必很厲害"),
                ArticleBlock.Paragraph("不一定要會樂器或極限運動。能替生活充電的事就算。例如:每週末做不同國家料理、收集各大甜點店並評比與朋友分享、追某個 podcast 累積筆記 200+ 篇 — 這些都比「看電影」有記憶點。"),
                ArticleBlock.Heading("挑跟職位有關的"),
                ArticleBlock.Paragraph("應徵行銷 → 寫「分析喜歡的品牌廣告做過 50+ 篇心得」。應徵 PM → 寫「自己用 Notion 管理生活待辦,設計過 X 個工作流」。應徵設計 → 寫「臨摹 X 設計師作品累積 100+ 張」。"),
                ArticleBlock.Heading("專長怎麼寫?"),
                ArticleBlock.BulletList(listOf(
                    "硬實力分類列點:工具、語言、證照",
                    "標等級:不要寫「會」,寫「熟悉/精通/曾用於 X 專案」",
                    "用具體成果佐證:不只寫「會 SQL」,寫「用 SQL 整理 X 萬筆資料產出週報」",
                )),
                ArticleBlock.Quote("挑選與應徵職務最相關的興趣專長,讓履歷上的專長顯得有說服力。"),
            ),
        ),
    )

 