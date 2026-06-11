package com.careersandbox.app.data.mock

// 個人面試設定(設定頁寫入、live 讀取)
// 之後由後端 Interview_Sessions 取代,介面不變
object InterviewConfig {
    var round: String = "初試"        // 初試 / 複試 / 主管面
    var language: String = "中文"     // 中文 / English
    var type: String = "行為"         // 行為 / 技術 / 情境
    var difficulty: String = "中等"   // 新手 / 中等 / 困難
    var groupInterviewers: Int = 1    // 團體面試:1 位主持 / 3 位 panel
}
