package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import coil.compose.AsyncImage
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
    var showPw by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Wave hero + beaver
            Box(modifier = Modifier.fillMaxWidth().height(264.dp)) {
                WaveHeroBackground(
                    gradient = Brush.linearGradient(listOf(BrandDeepOrange, BrandOrange, BrandAmber)),
                    heightDp = 264,
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 48.dp)
                        .fillMaxWidth(0.66f),
                ) {
                    Text(
                        "WELCOME",
                        color = PaperWhite.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 3.sp,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("讓你的\n")
                            withStyle(SpanStyle(color = BrandYellow)) { append("試錯成本") }
                            append("\n降到最低。")
                        },
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 31.sp,
                        lineHeight = 37.sp,
                    )
                }
                Image(
                    painter = painterResource(R.drawable.beaver_wave),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                        .size(184.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Reveal(visible, 80) {
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
                    Spacer(Modifier.height(24.dp))
                    Text("登入帳號", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 23.sp)
                    Spacer(Modifier.height(3.dp))
                    Text("用 Email 或第三方帳號繼續", color = InkGray500, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(22.dp))

                    FieldLabel("Email")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("you@email.com", color = InkGray400) },
                        leadingIcon = { Icon(Icons.Outlined.MailOutline, contentDescription = null, tint = InkGray500) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = loginFieldColors(),
                    )

                    Spacer(Modifier.height(16.dp))

                    FieldLabel("密碼")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("輸入密碼", color = InkGray400) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = InkGray500) },
                        trailingIcon = {
                            Icon(
                                if (showPw) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = InkGray400,
                                modifier = Modifier.pressScale { showPw = !showPw },
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (showPw) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = loginFieldColors(),
                    )

                    Spacer(Modifier.height(10.dp))
                    Text(
                        "忘記密碼?",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End),
                    )

                    Spacer(Modifier.height(22.dp))

                    val canLogin = email.isNotBlank() && password.isNotBlank()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .then(
                                if (canLogin) Modifier.shadow(
                                    elevation = 12.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = BrandOrange.copy(alpha = 0.5f),
                                ) else Modifier,
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (canLogin) Brush.linearGradient(listOf(BrandDeepOrange, BrandOrange, BrandAmber))
                                else Brush.linearGradient(listOf(InkGray200, InkGray200)),
                            )
                            .pressScale { if (canLogin) onLogin() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "登入",
                                color = if (canLogin) PaperWhite else InkGray400,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(Modifier.width(7.dp))
                            Icon(
                                Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = if (canLogin) PaperWhite else InkGray400,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f).height(1.dp).background(InkGray200))
                        Text(
                            "或用以下方式繼續",
                            color = InkGray400,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        Box(Modifier.weight(1f).height(1.dp).background(InkGray200))
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SocialButton("Google", "google.com", Modifier.weight(1f))
                        SocialButton("Apple", "apple.com", Modifier.weight(1f))
                        SocialButton("LINE", "line.me", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(26.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("第一次來?", color = InkGray500, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "建立帳號",
                            color = BrandDeepOrange,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.pressScale(onClick = onSignup),
                        )
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun Reveal(visible: Boolean, delay: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(550, delay, FastOutSlowInEasing)) +
            slideInVertically(tween(550, delay, FastOutSlowInEasing)) { it / 8 },
    ) { content() }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        color = InkBlack,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 7.dp),
    )
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrandOrange,
    unfocusedBorderColor = InkGray300,
    cursorColor = BrandOrange,
    focusedContainerColor = PaperWhite,
    unfocusedContainerColor = PaperWhite,
    focusedLeadingIconColor = BrandOrange,
)

@Composable
private fun SocialButton(name: String, domain: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(15.dp))
            .border(1.5.dp, InkGray200, RoundedCornerShape(15.dp))
            .background(PaperWhite)
            .pressScale { },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = "https://www.google.com/s2/favicons?domain=$domain&sz=128",
            contentDescription = name,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.width(8.dp))
        Text(name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
