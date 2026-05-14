package com.careersandbox.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

// ============= 共用 TopBar =============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
        content = content,
    )
}

// ============= 01 個人資料 =============
@Composable
fun SettingsProfileScreen(navController: NavHostController) {
    val user = MockData.currentUser
    var name by remember { mutableStateOf(user.name) }
    var school by remember { mutableStateOf(user.school) }
    var dept by remember { mutableStateOf(user.department) }
    var year by remember { mutableStateOf(user.year) }

    SettingsScaffold("個人資料", { navController.popBackStack() }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            // 大頭照區
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(user.name.firstOrNull()?.toString() ?: "?",
                        color = BrandDeepOrange,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("更換大頭照",
                    color = BrandOrange,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.pressScale {})
            }
            Spacer(Modifier.height(32.dp))

            ProfileField("姓名", name, { name = it })
            Spacer(Modifier.height(20.dp))
            ProfileField("學校", school, { school = it })
            Spacer(Modifier.height(20.dp))
            ProfileField("系所", dept, { dept = it })
            Spacer(Modifier.height(20.dp))
            ProfileField("年級", year, { year = it })
            Spacer(Modifier.height(40.dp))

            // 儲存按鈕(內聯,不用共用元件)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandOrange)
                    .pressScale { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("儲存變更",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, onChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = InkGray300,
            ),
        )
    }
}

// ============= 02 通知設定 =============
@Composable
fun SettingsNotificationsScreen(navController: NavHostController) {
    var pushEnabled by remember { mutableStateOf(true) }
    var dailyDigest by remember { mutableStateOf(true) }
    var interviewReminder by remember { mutableStateOf(true) }
    var newJobMatch by remember { mutableStateOf(false) }
    var weeklyReport by remember { mutableStateOf(true) }

    SettingsScaffold("通知設定", { navController.popBackStack() }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            SettingsGroupTitle("主要")
            ToggleRow("推播通知", "接收所有 app 推播", pushEnabled) { pushEnabled = it }
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))

            SettingsGroupTitle("內容類型")
            ToggleRow("每日精選", "AI 為你挑選的每日重點", dailyDigest) { dailyDigest = it }
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ToggleRow("面試提醒", "預約的模擬面試開始前通知", interviewReminder) { interviewReminder = it }
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ToggleRow("新職缺媒合", "符合你條件的職缺出現時", newJobMatch) { newJobMatch = it }
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ToggleRow("每週成長報告", "每週日早上 9:00 寄送", weeklyReport) { weeklyReport = it }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ToggleRow(title: String, subtitle: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title,
                color = InkBlack,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp)
            Spacer(Modifier.height(2.dp))
            Text(subtitle,
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = value, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PaperWhite,
                checkedTrackColor = BrandOrange,
                checkedBorderColor = BrandOrange,
                uncheckedThumbColor = InkGray500,
                uncheckedTrackColor = InkGray200,
                uncheckedBorderColor = InkGray300,
            ))
    }
}

@Composable
private fun SettingsGroupTitle(text: String) {
    Text(text,
        color = InkGray500,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 12.dp))
}

// ============= 03 隱私與資料 =============
@Composable
fun SettingsPrivacyScreen(navController: NavHostController) {
    var profilePublic by remember { mutableStateOf(false) }
    var allowSearch by remember { mutableStateOf(true) }
    var aiTraining by remember { mutableStateOf(false) }

    SettingsScaffold("隱私與資料", { navController.popBackStack() }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            SettingsGroupTitle("帳號可見性")
            ToggleRow("公開個人檔案", "其他用戶可看見你的履歷摘要",
                profilePublic) { profilePublic = it }
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ToggleRow("允許企業搜尋", "讓徵才公司主動聯絡你",
                allowSearch) { allowSearch = it }

            Spacer(Modifier.height(28.dp))
            SettingsGroupTitle("資料使用")
            ToggleRow("匿名資料用於模型訓練",
                "你的資料會去除個資後協助改善 AI 推薦",
                aiTraining) { aiTraining = it }

            Spacer(Modifier.height(28.dp))
            SettingsGroupTitle("資料操作")
            ActionRow("下載我的所有資料", Icons.Outlined.Download, BrandOrange) {}
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ActionRow("刪除我的帳號", Icons.Outlined.DeleteOutline, AccentRed) {}

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ActionRow(title: String, icon: ImageVector, accent: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title,
            color = accent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400)
    }
}

// ============= 04 幫助與支援 =============
@Composable
fun SettingsHelpScreen(navController: NavHostController) {
    SettingsScaffold("幫助與支援", { navController.popBackStack() }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            SettingsGroupTitle("常見問題")
            FaqItem("AI 面試評分準確嗎?",
                "我們用業界專家設計的 12 項指標,結合你的履歷與職缺特性綜合評分,建議與真實面試後使用做比對。")
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            FaqItem("我的履歷會被別人看到嗎?",
                "預設不公開,只有開啟「公開個人檔案」後才會出現在企業搜尋結果。")
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            FaqItem("可以匯出履歷成 PDF 嗎?",
                "履歷編輯器右上角有匯出選項,支援 PDF、Word、純文字三種格式。")

            Spacer(Modifier.height(28.dp))
            SettingsGroupTitle("聯絡我們")
            ActionRow("Email 客服", Icons.Outlined.MailOutline, BrandOrange) {}
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ActionRow("回報問題", Icons.Outlined.Flag, BrandDeepOrange) {}
            SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            ActionRow("功能建議", Icons.Outlined.Lightbulb, GlowPurple) {}

            Spacer(Modifier.height(28.dp))
            SettingsGroupTitle("關於")
            InfoRow("版本", "0.2.0-mvp")
            InfoRow("使用條款", "查看")
            InfoRow("隱私政策", "查看")

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().pressScale { expanded = !expanded }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(question,
                color = InkBlack,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = InkGray500,
            )
        }
        if (expanded) {
            Spacer(Modifier.height(6.dp))
            Text(answer,
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label,
            color = InkBlack,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f))
        Text(value,
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)
    }
}

// ============= 05 登出 =============
@Composable
fun SettingsLogoutScreen(navController: NavHostController) {
    SettingsScaffold("登出帳號", { navController.popBackStack() }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(40.dp))
            Box(
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = null,
                    tint = AccentRed, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("確定要登出嗎?",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text("登出後你的離線資料將被清除,\n下次登入需要重新同步",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp)
            Spacer(Modifier.weight(1f))

            // 登出按鈕(內聯)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AccentRed)
                    .pressScale {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("確認登出",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(12.dp))
            // 取消(內聯)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InkGray100)
                    .pressScale { navController.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("取消",
                    color = InkBlack,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
