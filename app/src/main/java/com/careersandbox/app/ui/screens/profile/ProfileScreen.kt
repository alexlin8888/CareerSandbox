package com.careersandbox.app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun ProfileScreen(navController: NavHostController) {
    val user = MockData.currentUser
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            HeroSection(userName = user.name, school = user.school,
                dept = user.department, year = user.year)
            Spacer(Modifier.height(20.dp))
            StatsSection()
            Spacer(Modifier.height(32.dp))
            MenuSection(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeroSection(userName: String, school: String, dept: String, year: String) {
    Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 260,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(0.6f),
        ) {
            Text("MY PROFILE",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Text(userName,
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 36.sp,
                lineHeight = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text("$school $dept",
                color = PaperWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(BrandYellow)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(year,
                    color = InkCharcoal,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelMedium)
            }
        }

        // 插畫破框(右下)
        Image(
            painter = painterResource(R.drawable.beaver_thumbsup),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 0.dp, y = 8.dp)
                .size(180.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatBlock(value = "4", label = "完成面試")
        VerticalDivider()
        StatBlock(value = "${MockData.experiences.size}", label = "經驗筆數")
        VerticalDivider()
        StatBlock(value = "${MockData.jobApplications.size}", label = "履歷數")
    }
}

@Composable
private fun StatBlock(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value,
            color = BrandOrange,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp)
        Spacer(Modifier.height(2.dp))
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(InkGray200)
    )
}

@Composable
private fun MenuSection(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = buildAnnotatedString {
                append("帳號")
                withStyle(SpanStyle(color = BrandOrange)) { append("設定") }
            },
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(20.dp))

        MenuRow("01", "個人資料", Icons.Outlined.Person, BrandOrange) {
            navController.navigate(Routes.SETTINGS_PROFILE)
        }
        SectionDivider(modifier = Modifier.padding(vertical = 14.dp))
        MenuRow("02", "通知設定", Icons.Outlined.NotificationsNone, BrandDeepOrange) {
            navController.navigate(Routes.SETTINGS_NOTIFICATIONS)
        }
        SectionDivider(modifier = Modifier.padding(vertical = 14.dp))
        MenuRow("03", "隱私與資料", Icons.Outlined.Lock, GlowPurple) {
            navController.navigate(Routes.SETTINGS_PRIVACY)
        }
        SectionDivider(modifier = Modifier.padding(vertical = 14.dp))
        MenuRow("04", "幫助與支援", Icons.Outlined.HelpOutline, AccentGreen) {
            navController.navigate(Routes.SETTINGS_HELP)
        }
        SectionDivider(modifier = Modifier.padding(vertical = 14.dp))
        MenuRow("05", "登出", Icons.Outlined.Logout, AccentRed) {
            navController.navigate(Routes.SETTINGS_LOGOUT)
        }
    }
}

@Composable
private fun MenuRow(number: String, title: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(number,
            color = accent.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            modifier = Modifier.width(40.dp))
        Spacer(Modifier.width(8.dp))
        Text(title,
            color = InkBlack,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f))
        Box(
            Modifier.size(36.dp).clip(CircleShape)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null,
                tint = accent, modifier = Modifier.size(18.dp))
        }
    }
}
