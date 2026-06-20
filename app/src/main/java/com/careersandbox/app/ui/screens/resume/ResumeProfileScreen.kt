package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.LanguageProficiency
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.SectionDivider
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ResumeProfileScreen(navController: NavHostController) {
    val user = MockData.currentUser
    val ctxScreen = LocalContext.current
    // #15 直接分享「這個版本」— 系統分享選單已含 Nearby Share / 藍牙 / 訊息(AirDrop 式直傳)
    val onShare: () -> Unit = {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, "這是我用 CareerSandbox 製作的履歷")
        }
        ctxScreen.startActivity(android.content.Intent.createChooser(intent, "分享這個版本"))
    }
    // 「關於我」可編輯(local state,套用後即時顯示)
    var bioText by remember { mutableStateOf(user.bio) }
    var editingBio by remember { mutableStateOf(false) }
    // 「技能」可編輯(已有的技能,可增刪)
    val skillsHave = remember { mutableStateListOf(*user.skillsHave.toTypedArray()) }
    var editingSkills by remember { mutableStateOf(false) }
    // 「語言」可編輯(語言 + 程度,可增刪)
    val languages = remember { mutableStateListOf(*user.languages.toTypedArray()) }
    var editingLangs by remember { mutableStateOf(false) }
    // 「連結」可編輯
    var linkedinUrl by remember { mutableStateOf(user.linkedin) }
    var githubUrl by remember { mutableStateOf(user.github) }
    var portfolioUrl by remember { mutableStateOf(user.portfolio) }
    var editingLinks by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("個人檔案", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Outlined.Share, contentDescription = "分享這個版本", tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(PaperWhite).padding(20.dp)) {
                Column {
                    // 主 CTA — 針對 JD 客製化
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InkBlack)
                            .pressScale {
                                navController.navigate(Routes.JD_CUSTOMIZE)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null,
                                tint = BrandAmber, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("針對職缺客製化",
                                color = PaperWhite,
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    // 次 CTA — 健檢 + 直接分享這個版本
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(InkGray100)
                                .pressScale { navController.navigate(Routes.FIT_ANALYSIS) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Tune, contentDescription = null,
                                    tint = InkBlack, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("健檢這份履歷",
                                    color = InkBlack,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(BrandPeach)
                                .pressScale(onClick = onShare),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Share, contentDescription = null,
                                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("分享這個版本",
                                    color = BrandDeepOrange,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall)
                            }
                        }
                    }
                }
            }
        },
    ) { pad ->
        // 進場狀態
        var entered by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { entered = true }

        // 大頭照進場 scale
        val avatarScale by animateFloatAsState(
            targetValue = if (entered) 1f else 0.6f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow,
            ),
            label = "avatarScale",
        )
        val avatarAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = androidx.compose.animation.core.tween(400),
            label = "avatarAlpha",
        )

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // === 頭部:大頭照 + 名字 + headline ===
            Row(
                modifier = Modifier.graphicsLayer { alpha = avatarAlpha },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(72.dp)
                        .graphicsLayer {
                            scaleX = avatarScale
                            scaleY = avatarScale
                        }
                        .clip(CircleShape)
                        .background(BrandOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(user.name.firstOrNull()?.toString() ?: "?",
                        color = BrandDeepOrange,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name,
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 26.sp,
                        letterSpacing = (-0.5).sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${user.year} ${user.department.replace("學系", "")} · 想做 PM",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 興趣 chip(staggered 200ms)
            StaggeredAppear(delayMillis = 100) {
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                user.interests.forEach { interest ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandPeach.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(interest,
                            color = BrandDeepOrange,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
            }  // 關 StaggeredAppear(興趣 chip)

            // === 關於我 ===
            StaggeredAppear(delayMillis = 220) {
                Column {
                    SectionLabel("關於我", onEdit = { editingBio = true })
                    Text(
                        bioText,
                        color = InkBlack,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                    )
                }
            }

            // === 經歷 ===
            StaggeredAppear(delayMillis = 320) {
                Column { SectionLabel("經歷", onEdit = { navController.navigate(Routes.EXPERIENCE_LIST) }) }
            }
            user.activities.forEach { act ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(act.title,
                            color = InkBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f))
                        Text(act.period,
                            color = InkGray500,
                            style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(act.role,
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold)
                    if (act.highlight.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(act.highlight,
                            color = InkGray700,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp)
                    }
                }
                if (act != user.activities.last()) {
                    SectionDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            // === 技能 ===
            SectionLabel("技能", onEdit = { editingSkills = true })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(BrandOrange))
                Spacer(Modifier.width(8.dp))
                Text("已具備",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                skillsHave.forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandOrange.copy(alpha = 0.1f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(skill,
                            color = BrandDeepOrange,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(GlowPurple))
                Spacer(Modifier.width(8.dp))
                Text("學習中",
                    color = InkGray500,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                user.skillsWant.forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GlowPurple.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(skill,
                            color = GlowPurple,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // === 語言 ===
            SectionLabel("語言", onEdit = { editingLangs = true })
            languages.forEach { lang ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(lang.language,
                        color = InkBlack,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    Text(lang.level,
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium)
                }
                if (lang != languages.last()) {
                    SectionDivider(modifier = Modifier.padding(vertical = 2.dp))
                }
            }

            // === 連結 ===
            SectionLabel("連結", onEdit = { editingLinks = true })
            if (linkedinUrl.isNotEmpty()) LinkRow("LinkedIn", linkedinUrl)
            if (githubUrl.isNotEmpty()) LinkRow("GitHub", githubUrl)
            if (portfolioUrl.isNotEmpty()) LinkRow("作品集", portfolioUrl)

            Spacer(Modifier.height(40.dp))
        }
    }

    // 「關於我」編輯對話框(真的能改,local state)
    if (editingBio) {
        var draft by remember { mutableStateOf(bioText) }
        AlertDialog(
            onDismissRequest = { editingBio = false },
            title = { Text("編輯關於我", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("寫一段自我介紹,會顯示在履歷最前面", color = InkGray500, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = draft, onValueChange = { draft = it },
                        modifier = Modifier.fillMaxWidth(), minLines = 4,
                        shape = RoundedCornerShape(12.dp),
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                        .pressScale { bioText = draft; editingBio = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) { Text("儲存", color = PaperWhite, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Text("取消", color = InkGray500, modifier = Modifier.pressScale { editingBio = false }.padding(12.dp))
            },
        )
    }

    // 「技能」編輯對話框(可刪除現有、新增)
    if (editingSkills) {
        var newSkill by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { editingSkills = false },
            title = { Text("編輯技能", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("點 × 移除,或在下方新增", color = InkGray500, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        skillsHave.forEach { skill ->
                            Row(
                                modifier = Modifier.clip(CircleShape).background(BrandOrange.copy(alpha = 0.1f))
                                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(skill, color = BrandDeepOrange, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier.size(18.dp).clip(CircleShape).background(BrandOrange.copy(alpha = 0.2f))
                                        .pressScale { skillsHave.remove(skill) },
                                    contentAlignment = Alignment.Center,
                                ) { Text("×", color = BrandDeepOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newSkill, onValueChange = { newSkill = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("新增一項技能") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (newSkill.isNotBlank()) {
                                Box(
                                    modifier = Modifier.padding(end = 6.dp).clip(RoundedCornerShape(8.dp)).background(BrandOrange)
                                        .pressScale {
                                            if (newSkill.isNotBlank() && newSkill !in skillsHave) {
                                                skillsHave.add(newSkill.trim()); newSkill = ""
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                ) { Text("加入", color = PaperWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                            }
                        },
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                        .pressScale { editingSkills = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) { Text("完成", color = PaperWhite, fontWeight = FontWeight.Bold) }
            },
        )
    }

    // 「語言」編輯對話框(語言+程度,可增刪)
    if (editingLangs) {
        var newLang by remember { mutableStateOf("") }
        var newLevel by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { editingLangs = false },
            title = { Text("編輯語言", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text("點 × 移除,或在下方新增", color = InkGray500, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    languages.forEach { lang ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(lang.language, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(lang.level, color = InkGray500, fontSize = 12.sp)
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.size(20.dp).clip(CircleShape).background(InkGray100)
                                    .pressScale { languages.remove(lang) },
                                contentAlignment = Alignment.Center,
                            ) { Text("×", color = InkGray500, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newLang, onValueChange = { newLang = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("語言(例:法文)") },
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLevel, onValueChange = { newLevel = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("程度(例:DELF B2)") },
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                        trailingIcon = {
                            if (newLang.isNotBlank()) {
                                Box(
                                    modifier = Modifier.padding(end = 6.dp).clip(RoundedCornerShape(8.dp)).background(BrandOrange)
                                        .pressScale {
                                            if (newLang.isNotBlank()) {
                                                languages.add(LanguageProficiency(newLang.trim(), newLevel.trim().ifBlank { "—" }))
                                                newLang = ""; newLevel = ""
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                ) { Text("加入", color = PaperWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                            }
                        },
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                        .pressScale { editingLangs = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) { Text("完成", color = PaperWhite, fontWeight = FontWeight.Bold) }
            },
        )
    }

    // 「連結」編輯對話框
    if (editingLinks) {
        var li by remember { mutableStateOf(linkedinUrl) }
        var gh by remember { mutableStateOf(githubUrl) }
        var pf by remember { mutableStateOf(portfolioUrl) }
        AlertDialog(
            onDismissRequest = { editingLinks = false },
            title = { Text("編輯連結", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    OutlinedTextField(
                        value = li, onValueChange = { li = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("LinkedIn") }, placeholder = { Text("linkedin.com/in/…") },
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = gh, onValueChange = { gh = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("GitHub") }, placeholder = { Text("github.com/…") },
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pf, onValueChange = { pf = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("作品集") }, placeholder = { Text("你的作品集網址") },
                        shape = RoundedCornerShape(12.dp), singleLine = true,
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                        .pressScale {
                            linkedinUrl = li.trim(); githubUrl = gh.trim(); portfolioUrl = pf.trim()
                            editingLinks = false
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) { Text("儲存", color = PaperWhite, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Text("取消", color = InkGray500, modifier = Modifier.pressScale { editingLinks = false }.padding(12.dp))
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String, onEdit: (() -> Unit)? = null) {
    Spacer(Modifier.height(36.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text,
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp)
        if (onEdit != null) {
            Row(
                modifier = Modifier.clip(RoundedCornerShape(50)).pressScale(onClick = onEdit)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = "編輯", tint = BrandDeepOrange, modifier = Modifier.size(13.dp))
                Spacer(Modifier.width(3.dp))
                Text("編輯", color = BrandDeepOrange, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun LinkRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(80.dp))
        Text(value,
            color = BrandDeepOrange,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium)
    }
}
