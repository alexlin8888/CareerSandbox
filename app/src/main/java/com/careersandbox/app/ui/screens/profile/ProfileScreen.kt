package com.careersandbox.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val user = MockData.currentUser
    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("我的", fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineMedium, color = InkBlack) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))
            // 個人區
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Avatar(name = user.name, size = 76.dp, background = InkBlack)
                Spacer(Modifier.height(14.dp))
                Text(user.name, style = MaterialTheme.typography.titleLarge,
                    color = InkBlack, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${user.school} ・ ${user.department} ・ ${user.year}",
                    style = MaterialTheme.typography.bodyMedium, color = InkGray500)
            }

            Spacer(Modifier.height(20.dp))

            // 統計卡
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ProfileStat("4", "經驗", Modifier.weight(1f))
                ProfileStat("3", "履歷", Modifier.weight(1f))
                ProfileStat("4", "面試", Modifier.weight(1f))
                ProfileStat("12", "探索職位", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProfileItem("編輯個人檔案", Icons.Outlined.AccountCircle)
                ProfileItem("我的收藏", Icons.Outlined.Favorite)
                ProfileItem("申請紀錄", Icons.Outlined.Assignment)
                ProfileItem("通知設定", Icons.Outlined.Notifications)
                ProfileItem("幫助與意見", Icons.Outlined.HelpOutline)
                ProfileItem("隱私與條款", Icons.Outlined.Lock)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = InkGray200)
                Spacer(Modifier.height(8.dp))

                ProfileItem("登出", Icons.Outlined.Logout, color = AccentRed)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall,
            color = BrandOrange, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkGray500)
    }
}

@Composable
private fun ProfileItem(
    label: String, icon: ImageVector,
    color: androidx.compose.ui.graphics.Color = InkBlack,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .pressScale {}
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = color, fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400)
    }
}
