package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun InterviewSetupScreen(navController: NavHostController) {
    var targetJob by remember { mutableStateOf("Junior PM") }
    var type by remember { mutableStateOf("行為面試") }
    var difficulty by remember { mutableStateOf("中等") }
    var style by remember { mutableStateOf("標準") }
    var duration by remember { mutableStateOf("30 分鐘") }
    var language by remember { mutableStateOf("中文") }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("個人面試設定", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(PaperOff).padding(20.dp)) {
                PrimaryOrangeButton("開始面試",
                    { navController.navigate(Routes.INTERVIEW_LIVE_INDIVIDUAL) })
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandPeach.copy(alpha = 0.6f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("這是模擬,真實面試會更難。",
                    style = MaterialTheme.typography.bodySmall, color = BrandDeepOrange)
            }
            Spacer(Modifier.height(20.dp))

            Field("應徵職位") {
                OutlinedTextField(value = targetJob, onValueChange = { targetJob = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp), singleLine = true,
                    colors = textFieldColors())
            }
            Field("面試類型") { ChipRow(listOf("行為面試", "技術面試", "情境面試", "壓力面試"), type) { type = it } }
            Field("難度") { ChipRow(listOf("新手", "中等", "困難", "變態"), difficulty) { difficulty = it } }
            Field("面試官風格") { ChipRow(listOf("親切", "標準", "嚴厲", "隨機"), style) { style = it } }
            Field("時長") { ChipRow(listOf("15 分鐘", "30 分鐘", "45 分鐘", "60 分鐘"), duration) { duration = it } }
            Field("語言") { ChipRow(listOf("中文", "英文", "中英混合"), language) { language = it } }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Field(label: String, content: @Composable () -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall,
            color = InkBlack, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(opts: List<String>, selected: String, onSel: (String) -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        opts.forEach { opt ->
            PillChip(opt, selected = opt == selected) { onSel(opt) }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = InkBlack, unfocusedBorderColor = InkGray200,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
)
