package com.careersandbox.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.careersandbox.app.data.remote.RegisterRequest

private val OnbBg = Color(0xFFFFF8F3)
private val OnbCardBorder = Color(0xFFF0E6DC)
private val Espresso = Color(0xFF3D2419)

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    // #25 / #11:第一次進來先用教學卡介紹功能與「母版」概念,再進入填資料表單
    var showIntro by remember { mutableStateOf(true) }
    if (showIntro) {
        OnboardingIntro(onStart = { showIntro = false })
    } else {
        OnboardingForm(onDone = onDone)
    }
}

/* ===================== 共用元件 ===================== */

@Composable
private fun OnbTopBar(onSkip: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("職涯沙盒", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text("CAREER SANDBOX", color = InkGray400, fontWeight = FontWeight.SemiBold, fontSize = 9.sp)
        }
        Text(
            "跳過介紹",
            color = InkGray400,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.pressScale { onSkip() }.padding(6.dp),
        )
    }
}

@Composable
private fun OnbLabel(text: String) {
    Text(text, color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Black)
}

@Composable
private fun OnbHighlight(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(BrandAmber.copy(alpha = 0.5f))
            .padding(horizontal = 9.dp, vertical = 5.dp),
    ) {
        Text(text, color = InkBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun OnbPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(54.dp)
            .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = BrandOrange.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(BrandOrange, BrandDeepOrange)))
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp, maxLines = 1)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Outlined.ArrowForward, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun OnbChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) BrandDeepOrange else PaperWhite)
            .border(1.5.dp, if (selected) BrandDeepOrange else InkGray200, RoundedCornerShape(50))
            .pressScale(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 10.dp),
    ) {
        Text(label, color = if (selected) PaperWhite else InkGray700, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun OnbBottomNav(showBack: Boolean, onBack: () -> Unit, nextText: String, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp)) {
        if (showBack) {
            Text(
                "← 上一步",
                color = InkGray400,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.pressScale { onBack() }.padding(vertical = 6.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
        OnbPrimaryButton(text = nextText, onClick = onNext, modifier = Modifier.fillMaxWidth())
    }
}

/* ===================== 導覽(5 卡) ===================== */

@Composable
private fun OnboardingIntro(onStart: () -> Unit) {
    val totalCards = 5
    var card by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().background(OnbBg).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(52.dp))
        OnbTopBar(onSkip = onStart)

        AnimatedContent(
            targetState = card,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "introCard",
        ) { c ->
            when (c) {
                0 -> IntroCard(
                    "01 / 05", R.drawable.beaver_wave, "歡迎來到\nCareerSandbox", null,
                    "履歷、模擬面試、職涯探索、職場體驗,在同一個地方把求職一次準備好。",
                )
                1 -> IntroCard(
                    "02 / 05", R.drawable.beaver_resume, "先建一份「母版」", "母版 = 你最完整的綜合履歷",
                    "把所有經歷、技能、作品都放進去,先不為任何公司修飾。之後的客製版本,都從這份母版長出來。",
                )
                2 -> IntroTierCard("03 / 05")
                3 -> IntroCard(
                    "04 / 05", R.drawable.beaver_celebrate, "跟 AI 面試官練習", "想練幾分鐘,都可以",
                    "可以調面試官的強度、貼上 JD、選擇要給他看哪一份履歷版本。練完拿到分面向的回饋:內容、結構、表達。",
                )
                4 -> IntroCard(
                    "05 / 05", R.drawable.beaver_search, "用自己的話找方向", "重點是你「還缺什麼」",
                    "還不確定想要什麼工作?用一句話描述你在意的事,系統幫你收斂、清楚標出你還缺哪些技能。選定後,進職場沙盒提前體驗那份工作的一天。",
                )
            }
        }

        // 進度圓點(左對齊,選中拉長)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            repeat(totalCards) { i ->
                Box(
                    Modifier
                        .padding(end = 6.dp)
                        .height(7.dp)
                        .width(if (i == card) 22.dp else 7.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (i == card) BrandDeepOrange else InkGray200),
                )
            }
        }

        OnbBottomNav(
            showBack = card > 0,
            onBack = { card-- },
            nextText = if (card == totalCards - 1) "開始建立我的資料" else "下一步",
            onNext = { if (card < totalCards - 1) card++ else onStart() },
        )
    }
}

@Composable
private fun IntroCard(label: String, beaver: Int, title: String, highlight: String?, body: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(10.dp))
        OnbLabel(label)
        Spacer(Modifier.height(14.dp))
        Text(title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 27.sp, lineHeight = 34.sp)
        if (highlight != null) {
            Spacer(Modifier.height(14.dp))
            OnbHighlight(highlight)
        }
        Spacer(Modifier.height(16.dp))
        Text(body, color = InkGray500, fontSize = 14.sp, lineHeight = 23.sp)
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(beaver),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(175.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun IntroTierCard(label: String) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(10.dp))
        OnbLabel(label)
        Spacer(Modifier.height(14.dp))
        Text(
            buildAnnotatedString {
                append("一份")
                withStyle(SpanStyle(background = BrandAmber)) { append("母版") }
                append("\n長出多個版本")
            },
            color = InkBlack, fontWeight = FontWeight.Black, fontSize = 27.sp, lineHeight = 36.sp,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "同一份母版,針對不同公司強調不同經歷。每個版本可以單獨標記投遞狀態。",
            color = InkGray500, fontSize = 14.sp, lineHeight = 23.sp,
        )
        Spacer(Modifier.height(26.dp))

        // 深色 anchor 卡:母版(基底)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Espresso, Color(0xFF1F1209))))
                .padding(18.dp),
        ) {
            Column {
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp)).background(BrandAmber)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                ) {
                    Text("母版履歷", color = Espresso, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text("你的完整經歷庫", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("不為任何公司修飾,收錄所有經歷", color = PaperWhite.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
            HandDrawnArrow(modifier = Modifier.height(34.dp).width(54.dp), color = InkGray400, strokeWidth = 2.5f)
        }

        // 兩個衍生版本
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VersionMini(modifier = Modifier.weight(1f), company = "台積電 PM 版", accent = AccentGreen, statusLabel = "投遞中")
            VersionMini(modifier = Modifier.weight(1f), company = "新創 PM 版", accent = BrandOrange, statusLabel = "草稿")
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun VersionMini(modifier: Modifier = Modifier, company: String, accent: Color, statusLabel: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PaperWhite)
            .border(1.5.dp, OnbCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Column {
            Box(Modifier.size(width = 28.dp, height = 4.dp).clip(RoundedCornerShape(50)).background(accent))
            Spacer(Modifier.height(10.dp))
            Text(company, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier.clip(RoundedCornerShape(50)).background(accent.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(statusLabel, color = accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/* ===================== 註冊表單(4 步) ===================== */

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun OnboardingForm(onDone: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    val total = 4
    val interests = remember { mutableStateListOf<String>() }
    val skillsHave = remember { mutableStateListOf<String>() }
    val skillsWant = remember { mutableStateListOf<String>() }
    var name by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // NEW: ViewModel that calls POST /auth/register.
    // The lambda tells Compose how to build it (it has no zero-arg constructor).
    val registerViewModel: RegisterViewModel = viewModel { RegisterViewModel() }
    val registerState = registerViewModel.uiState
    val isLoading = registerState is RegisterUiState.Loading

    // NEW: minimal client-side validation for step 1
    var showStep1Hint by remember { mutableStateOf(false) }
    val step1Valid = name.isNotBlank() &&
            email.contains("@") &&
            password.isNotBlank() &&
            password == confirmPassword

    // NEW: navigate away only after the backend confirms (201)
    LaunchedEffect(registerState) {
        if (registerState is RegisterUiState.Success) onDone()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(OnbBg).padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(56.dp))
        // 標籤 + STEP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("建立你的資料", color = InkGray500, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("STEP $step / $total", color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(14.dp))
        // 單一進度條
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)).background(InkGray200)) {
            Box(
                Modifier.fillMaxWidth(step.toFloat() / total).height(8.dp).clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(BrandOrange, BrandDeepOrange))),
            )
        }

        Spacer(Modifier.height(26.dp))

        AnimatedContent(
            targetState = step,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 })
                    .togetherWith(fadeOut(tween(150)))
            },
            label = "step",
        ) { current ->
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (current) {
                    1 -> Step1(
                        name, { name = it }, school, { school = it },
                        dept, { dept = it }, year, { year = it },
                        email, { email = it },
                        password, { password = it },
                        confirmPassword, { confirmPassword = it },
                    )
                    2 -> Step2(interests)
                    3 -> Step3(skillsHave, skillsWant)
                    4 -> Step4()
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // NEW: inline messages shown just above the bottom button
        if (step == 1 && showStep1Hint && !step1Valid) {
            Text(
                "請確認姓名已填、Email 格式正確、兩次密碼一致",
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
        }
        if (step == total && registerState is RegisterUiState.Error) {
            Text(
                registerState.message,
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
        }

        OnbBottomNav(
            showBack = step > 1 && !isLoading,   // NEW: lock back-nav during the request
            onBack = { step-- },
            nextText = when {                     // NEW: button text reflects request state
                step < total -> "下一步"
                isLoading -> "建立帳號中..."
                else -> "開始使用"
            },
            onNext = {                            // NEW: replaces the old mock line
                when {
                    isLoading -> Unit             // ignore taps while a request is in flight
                    step == 1 && !step1Valid -> showStep1Hint = true
                    step < total -> { showStep1Hint = false; step++ }
                    else -> registerViewModel.register(
                        RegisterRequest(
                            email = email.trim(),
                            password = password,
                            name = name.trim(),
                            school = school.trim(),
                            department = dept.trim(),
                            year = year.trim(),
                            interests = interests.toList(),
                            skillsHave = skillsHave.toList(),
                            skillsWant = skillsWant.toList(),
                        )
                    )
                }
            },
        )
    }
}

@Composable
private fun FormHeadline(title: String, subtitle: String) {
    Text(title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 26.sp, lineHeight = 32.sp)
    Spacer(Modifier.height(10.dp))
    Text(subtitle, color = InkGray500, fontSize = 14.sp, lineHeight = 22.sp)
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun Step1(
    name: String, onName: (String) -> Unit,
    school: String, onSchool: (String) -> Unit,
    dept: String, onDept: (String) -> Unit,
    year: String, onYear: (String) -> Unit,
    email: String, onEmail: (String) -> Unit,
    password: String, onPassword: (String) -> Unit,
    confirmPassword: String, onConfirmPassword: (String) -> Unit,
) {
    // 眼睛切換用的本地狀態(純畫面用,不需要往上層傳)
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    // 防呆:確認密碼有輸入、且和密碼不一致時 → true
    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

    FormHeadline("基本資料設定", "這些只用來推薦合適內容,不會公開。")
    OnboardField("姓名", name, onName)
    OnboardField("學校", school, onSchool)
    OnboardField("系所", dept, onDept)
    OnboardField("年級", year, onYear)

    // ===== 以下三個為新加入的欄位 =====

    // 1. Email(信封圖示)
    OnboardField(
        label = "Email",
        value = email,
        onChange = onEmail,
        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = InkGray400) },
        keyboardType = KeyboardType.Email,
    )

    // 2. 密碼(眼睛切換顯示/隱藏 + 隱私輸入)
    OnboardField(
        label = "密碼",
        value = password,
        onChange = onPassword,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (passwordVisible) "隱藏密碼" else "顯示密碼",
                    tint = InkGray400,
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardType = KeyboardType.Password,
    )

    // 3. 確認密碼(款式與密碼相同;不一致時邊框轉紅)
    OnboardField(
        label = "確認密碼",
        value = confirmPassword,
        onChange = onConfirmPassword,
        trailingIcon = {
            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                Icon(
                    imageVector = if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (confirmVisible) "隱藏密碼" else "顯示密碼",
                    tint = InkGray400,
                )
            }
        },
        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardType = KeyboardType.Password,
        isError = passwordMismatch,
    )

    // 防呆紅字:兩次密碼不一致時才顯示
    if (passwordMismatch) {
        Text(
            "兩次輸入的密碼不一致",
            color = Color(0xFFEF4444),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp),
        )
    }
}

@Composable
private fun OnboardField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
) {
    Column(Modifier.padding(bottom = 14.dp)) {
        Text(label, color = InkGray700, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = InkGray200,
                focusedContainerColor = PaperWhite,
                unfocusedContainerColor = PaperWhite,
            ),
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step2(selected: MutableList<String>) {
    FormHeadline("你想探索的方向", "選 3-5 個,可以隨時改 ・ 已選 ${selected.size}/5")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MockData.jobInterests.forEach { item ->
            OnbChip(label = item, selected = item in selected, onClick = {
                if (item in selected) selected.remove(item) else if (selected.size < 5) selected.add(item)
            })
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun Step3(skillsHave: MutableList<String>, skillsWant: MutableList<String>) {
    FormHeadline("你會什麼", "先盤點手上有的,再勾想學的。")
    Text("我擅長", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(Modifier.height(12.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MockData.skills.forEach { s ->
            OnbChip(s, selected = s in skillsHave) {
                if (s in skillsHave) skillsHave.remove(s) else skillsHave.add(s)
            }
        }
    }
    Spacer(Modifier.height(24.dp))
    Text("我想學", color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(Modifier.height(12.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MockData.skills.forEach { s ->
            OnbChip(s, selected = s in skillsWant) {
                if (s in skillsWant) skillsWant.remove(s) else skillsWant.add(s)
            }
        }
    }
}

@Composable
private fun Step4() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(R.drawable.beaver_thumbsup), contentDescription = null, modifier = Modifier.size(72.dp))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("準備好了", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text("根據你的選擇,以下幾件事可以先做", color = InkGray500, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
    Spacer(Modifier.height(24.dp))

    val items = listOf(
        "01" to ("先試試模擬一場面試" to "用團體面試體驗看看會怎樣"),
        "02" to ("12 個適合你的職位" to "可以從職場沙盒裡看真實樣貌"),
        "03" to ("先寫一份履歷草稿" to "從你過去的經驗開始"),
    )
    items.forEachIndexed { idx, (no, content) ->
        StaggeredAppear(delayMillis = idx * 100) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PaperWhite)
                    .border(1.5.dp, OnbCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(no, color = BrandOrange, fontWeight = FontWeight.Black, fontSize = 30.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(content.first, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(content.second, color = InkGray500, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}