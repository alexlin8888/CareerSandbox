package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    DarkGlowBackdrop(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(72.dp))

            // 小 logo + 標籤
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp)
                        .shadow(12.dp, RoundedCornerShape(14.dp),
                            spotColor = BrandOrange.copy(alpha = 0.6f))
                        .clip(RoundedCornerShape(14.dp))
                        .background(HeroGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("CS", color = PaperWhite,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(12.dp))
                Text("CareerSandbox",
                    color = PaperWhite,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(40.dp))

            // Hero 大字
            Text(
                text = buildAnnotatedString {
                    append("讓你的職涯")
                    withStyle(SpanStyle(color = PaperWhite)) { append("\n") }
                    withStyle(SpanStyle(color = BrandYellow)) { append("試錯成本") }
                    append("\n降到最低。")
                },
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 44.sp,
                lineHeight = 50.sp,
                letterSpacing = (-1).sp,
            )

            Spacer(Modifier.height(16.dp))

            // 三個 chip 列出特性
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeatureChip("AI 面試")
                FeatureChip("履歷優化")
                FeatureChip("職場預習")
            }

            Spacer(Modifier.height(48.dp))

            // 玻璃表單卡
            GlassDarkCard(cornerRadius = 28.dp) {
                Text(
                    "歡迎回來",
                    color = PaperWhite,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(20.dp))

                DarkTextField(value = email, onValueChange = { email = it },
                    placeholder = "Email")
                Spacer(Modifier.height(12.dp))
                DarkTextField(value = password, onValueChange = { password = it },
                    placeholder = "密碼", isPassword = true)

                Spacer(Modifier.height(20.dp))

                YellowButton("登入", onClick = onLogin)
            }

            Spacer(Modifier.height(24.dp))

            // 分隔
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f),
                    color = InkGray700.copy(alpha = 0.6f))
                Text("  或繼續使用  ",
                    style = MaterialTheme.typography.labelMedium,
                    color = InkGray400)
                HorizontalDivider(modifier = Modifier.weight(1f),
                    color = InkGray700.copy(alpha = 0.6f))
            }
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SocialButton("Google", Icons.Outlined.Search,
                    modifier = Modifier.weight(1f), onClick = onLogin)
                SocialButton("Apple", Icons.Outlined.PhoneIphone,
                    modifier = Modifier.weight(1f), onClick = onLogin)
            }

            Spacer(Modifier.height(28.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text("第一次來?", style = MaterialTheme.typography.bodyMedium,
                    color = InkGray400)
                Spacer(Modifier.width(6.dp))
                Text("建立帳號", style = MaterialTheme.typography.bodyMedium,
                    color = BrandYellow, fontWeight = FontWeight.Bold,
                    modifier = Modifier.pressScale(onClick = onSignup))
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FeatureChip(label: String) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(Color(0x1AFFFFFF))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(label, color = PaperWhite,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DarkTextField(
    value: String, onValueChange: (String) -> Unit,
    placeholder: String, isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = InkGray500) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        textStyle = androidx.compose.ui.text.TextStyle(color = PaperWhite),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedBorderColor = InkGray700,
            focusedContainerColor = Color(0x14FFFFFF),
            unfocusedContainerColor = Color(0x14FFFFFF),
            cursorColor = BrandYellow,
            focusedTextColor = PaperWhite,
            unfocusedTextColor = PaperWhite,
        ),
        visualTransformation = if (isPassword)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
    )
}

@Composable
private fun YellowButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = BrandYellow.copy(alpha = 0.6f))
            .clip(RoundedCornerShape(16.dp))
            .background(BrandYellow)
            .pressScale { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = InkCharcoal,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SocialButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x1AFFFFFF))
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = PaperWhite,
                modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, color = PaperWhite,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge)
        }
    }
}
