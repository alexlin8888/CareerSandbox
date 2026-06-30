package com.careersandbox.app.data.mock

// 個人面試設定(設定頁寫入、live 讀取)
// 之後由後端 Interview_Sessions 取代,介面不變
object InterviewConfig {
    var round: String = "初試"        // 初試 / 複試 / 主管面
    var language: String = "中文"     // 中文 / English
    var type: String = "行為"         // 行為 / 技術 / 情境
    var difficulty: String = "中等"   // 新手 / 中等 / 困難
    var groupInterviewers: Int = 1    // 團體面試:1 位主持 / 3 位 panel
    // 自訂職位脈絡(讓 AI 出題更準)
    var customRole: String = ""
    var customCompany: String = ""
    var customSeniority: String = "新鮮人"   // 新鮮人 / 1-3年 / 資深
    var customIndustry: String = ""
    var customJd: String = ""

    // 剛結束的是不是「影像面試」——報告用它決定要不要顯示影像維度區塊。
    // 視訊面試進入時設 true,報告讀取後重置,避免文字/語音面試誤顯示鏡頭指標。
    // 之後接後端時改由 Interview_Sessions.type 帶,介面不變。
    var lastWasVideo: Boolean = false

    // 剛結束的是不是「團體面試」——報告用它決定要不要顯示協作維度區塊。
    // 群面進入時設 true,報告讀取後重置。之後接後端時改由 Interview_Sessions.type 帶,介面不變。
    var lastWasGroup: Boolean = false
}
