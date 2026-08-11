package com.careersandbox.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 依 category 字串換對應圖示。
 * 後端 category 目前是開放字串（16 類，見交接文件三第一節），
 * 查無對應時一律回傳 defaultIcon，避免新分類值讓畫面壞掉。
 */
fun iconForCategory(category: String): ImageVector = when (category) {
    "工程" -> Icons.Outlined.Code
    "行銷" -> Icons.Outlined.Campaign
    "業務" -> Icons.Outlined.Handshake
    "客服" -> Icons.Outlined.SupportAgent
    "人資" -> Icons.Outlined.Groups
    "財會" -> Icons.Outlined.AccountBalance
    "採購" -> Icons.Outlined.ShoppingCart
    "物流" -> Icons.Outlined.LocalShipping
    "行政" -> Icons.Outlined.Assignment
    "營運" -> Icons.Outlined.Settings
    "服務" -> Icons.Outlined.RoomService
    "運輸" -> Icons.Outlined.DirectionsCar
    "技術" -> Icons.Outlined.Build
    "法務" -> Icons.Outlined.Gavel
    "公職" -> Icons.Outlined.AccountBalance
    "醫護" -> Icons.Outlined.LocalHospital
    // 舊的四類 mock 資料也涵蓋，避免現有畫面壞掉
    "數據" -> Icons.Outlined.Analytics
    "產品" -> Icons.Outlined.Description
    "設計" -> Icons.Outlined.Edit
    "學術" -> Icons.Outlined.Lightbulb
    else -> Icons.Outlined.Work   // ← 兜底：查無對應一律用這個預設圖示，畫面不會壞
}