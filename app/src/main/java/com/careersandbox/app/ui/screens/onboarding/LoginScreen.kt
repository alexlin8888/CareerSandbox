package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        // 線稿裝飾鋪滿
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // === Wave HERO 區 + 插畫 ===
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                WaveHeroBackground(
                    gradient = Brush.linearGradient(
                        colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
                    ),
                    heightDp = 280,
                )
                // 文字內容
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 48.dp)
                        .fillMaxWidth(0.65f),
                ) {
                    Text("Welcome",
                        color = PaperWhite.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 3.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("讓你的\n")
                            withStyle(SpanStyle(color = BrandYellow)) {
                                append("試錯成本")
                            }
                            append("\n降到最低。")
                        },
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        lineHeight = 38.sp,
                        letterSpacing = (-0.8).sp,
                    )
                }
                // 插畫破框
                Image(
                    painter = painterResource(R.drawable.undraw_interview_yz52),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 16.dp, y = 32.dp)
                        .size(200.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(Modifier.height(32.dp))

            // === 無框表單 ===
            Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                Text("登入帳號",
                    color = InkBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = (-0.5).sp)
                Spacer(Modifier.height(2.dp))
                Text("用 Email 或第三方帳號繼續",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(28.dp))

                // 無框 Email
                BorderlessTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    icon = Icons.Outlined.MailOutline,
                )
                Spacer(Modifier.height(20.dp))
                BorderlessTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "密碼",
                    icon = Icons.Outlined.Lock,
                    isPassword = true,
                )

                Spacer(Modifier.height(36.dp))

                // 主按鈕(這裡用實心橘色,因為按鈕本來就該有 affordance)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(elevation = 14.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = BrandOrange.copy(alpha = 0.6f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(
                            listOf(BrandDeepOrange, BrandOrange, BrandAmber))
                        )
                        .pressScale { onLogin() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("登入 →",
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                        letterSpacing = 0.5.sp)
                }

                Spacer(Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Text("第一次來?",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(6.dp))
                    Text("建立帳號",
                        color = BrandOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.pressScale(onClick = onSignup))
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

/** 無框輸入框:只有底部一條線,聚焦時變橘色 */
@Composable
private fun BorderlessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
) {
    val focused = remember { mutableStateOf(false) }
    val accentColor = if (focused.value) BrandOrange else InkGray400

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label,
            color = accentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextFieldWithFocusTracker(
                value = value,
                onValueChange = onValueChange,
                isPassword = isPassword,
                onFocusChange = { focused.value = it },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(8.dp))
        // 底線
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (focused.value) 2.dp else 1.dp)
                .background(accentColor)
        )
    }
}

@Composable
private fun BasicTextFieldWithFocusTracker(
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier,
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onFocusChanged { onFocusChange(it.isFocused) },
        textStyle = androidx.compose.ui.text.TextStyle(
            color = InkBlack,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        ),
        singleLine = true,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(BrandOrange),
        visualTransformation = if (isPassword)
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
    )
}
