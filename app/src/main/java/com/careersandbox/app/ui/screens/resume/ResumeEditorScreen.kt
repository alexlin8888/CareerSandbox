package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeEditorScreen(navController: NavHostController) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("區塊管理", "內容編輯", "即時預覽")

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("履歷編輯器", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Save, contentDescription = null, tint = InkBlack) }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = InkBlack) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = PaperOff,
                contentColor = InkBlack,
                indicator = { positions ->
                    if (tabIndex < positions.size) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(positions[tabIndex])
                                .height(3.dp)
                                .background(BrandOrange)
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { i, label ->
                    Tab(selected = i == tabIndex, onClick = { tabIndex = i },
                        text = { Text(label, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge) },
                        selectedContentColor = InkBlack,
                        unselectedContentColor = InkGray400)
                }
            }
            when (tabIndex) {
                0 -> SectionsTab()
                1 -> ContentEditTab()
                2 -> PreviewTab()
            }
        }
    }
}

@Composable
private fun SectionsTab() {
    val sections = remember {
        mutableStateListOf(
            "個人資訊" to true, "教育背景" to true, "工作經驗" to true,
            "專案經驗" to true, "技能" to true, "證照" to false, "自我介紹" to true,
        )
    }
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(sections.size) { idx ->
            val (name, enabled) = sections[idx]
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DragIndicator, contentDescription = null, tint = InkGray400)
                    Spacer(Modifier.width(12.dp))
                    Text(name, style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f), color = InkBlack)
                    Switch(checked = enabled, onCheckedChange = { sections[idx] = name to it },
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
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun ContentEditTab() {
    var summary by remember { mutableStateOf("資管系大三,做過社團行銷和電商實習,想往產品經理發展。") }
    var exp by remember { mutableStateOf(
        "電商公司資料分析實習 (2025.07 - 2025.09)\n" +
                "- 用 SQL 整理銷售資料,每週產出業務週報\n" +
                "- 從原本 6 小時的人工整理流程縮短至 1.5 小時"
    ) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        EditField(label = "自我介紹", value = summary, onChange = { summary = it })
        Spacer(Modifier.height(16.dp))
        EditField(label = "工作經驗", value = exp, onChange = { exp = it }, multiline = true)
    }
}

@Composable
private fun EditField(label: String, value: String, onChange: (String) -> Unit, multiline: Boolean = false) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleSmall,
                color = InkBlack, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandPeach)
                    .pressScale {}
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("AI 優化", style = MaterialTheme.typography.labelSmall,
                    color = BrandDeepOrange, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            minLines = if (multiline) 4 else 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            )
        )
    }
}

@Composable
private fun PreviewTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("簡約", "現代", "經典").forEachIndexed { i, name ->
                Box(
                    Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                        .background(if (i == 0) InkBlack else InkGray100)
                        .pressScale {}
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(name, style = MaterialTheme.typography.labelLarge,
                        color = if (i == 0) PaperWhite else InkGray500,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(PaperWhite).padding(24.dp),
        ) {
            Column {
                Text("Alex Chen", style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold, color = InkBlack)
                Text("國立政治大學 資訊管理學系",
                    style = MaterialTheme.typography.bodySmall, color = InkGray500)
                Spacer(Modifier.height(16.dp))
                Section("自我介紹", "資管系大三,做過社團行銷和電商實習,想往產品經理發展。")
                Section("工作經驗",
                    "電商公司資料分析實習 (2025.07 - 2025.09)\n" +
                            "・用 SQL 整理銷售資料,每週產出業務週報\n" +
                            "・從原本 6 小時的人工流程縮短至 1.5 小時")
                Section("教育背景", "國立政治大學 資訊管理學系 大三 (2023 - 至今)")
            }
        }
    }
}

@Composable
private fun Section(title: String, content: String) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall,
            color = BrandOrange, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(content, style = MaterialTheme.typography.bodySmall, color = InkBlack)
    }
}
