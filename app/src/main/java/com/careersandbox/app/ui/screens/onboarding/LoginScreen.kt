package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.R
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Wave HERO 區 + 插畫
            Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                WaveHeroBackground(
                    gradient = Brush.linearGradient(
                        colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
                    ),
                    heightDp = 280,
                )
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
                    )
                }
                Image(
                    painter = painterResource(R.drawable.beaver_wave),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 12.dp)
                        .size(190.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                Text("登入帳號",
                    color = InkBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp)
                Spacer(Modifier.height(2.dp))
                Text("用 Email 或第三方帳號繼續",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(28.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Outlined.MailOutline, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = InkGray300,
                        focusedLabelColor = BrandOrange,
                    ),
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密碼") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null)
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOrange,
                        unfocusedBorderColor = InkGray300,
                        focusedLabelColor = BrandOrange,
                    ),
                )

                Spacer(Modifier.height(36.dp))

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
                        style = MaterialTheme.typography.titleMedium)
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
