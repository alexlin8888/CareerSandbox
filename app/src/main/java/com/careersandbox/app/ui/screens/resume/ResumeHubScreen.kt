package com.careersandbox.app.ui.screens.resume

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeHubScreen(navController: NavHostController) {
    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("履歷", fontWeight = FontWeight.ExtraBold,
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
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // 統計
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip(label = "履歷數", value = "${MockData.resumes.size}", modifier = Modifier.weight(1f))
                StatChip(label = "經驗筆數", value = "${MockData.experiences.size}", modifier = Modifier.weight(1f))
                StatChip(label = "待處理建議", value = "3", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryDarkButton(text = "新增履歷", leadingIcon = Icons.Outlined.Add,
                    onClick = { navController.navigate(Routes.RESUME_EDITOR) },
                    modifier = Modifier.weight(1f))
                SecondaryButton(text = "整理經驗",
                    onClick = { navController.navigate(Routes.EXPERIENCE_LIST) },
                    modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))

            // JD 客製化 - 橘色 CTA banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(18.dp),
                        spotColor = BrandOrange.copy(alpha = 0.4f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(HeroGradient)
                    .pressScale { navController.navigate(Routes.JD_CUSTOMIZE) }
                    .padding(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                            .background(PaperWhite.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Tune, contentDescription = null, tint = PaperWhite)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("用 JD 客製化一份履歷",
                            color = PaperWhite, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium)
                        Text("AI 幫你分析職位需求 + 自動改寫",
                            color = PaperWhite.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = PaperWhite)
                }
            }

            SectionTitle(title = "歷史履歷")

            MockData.resumes.forEachIndexed { idx, r ->
                StaggeredAppear(delayMillis = idx * 80) {
                    ResumeCard(
                        title = r.title, targetJob = r.targetJob,
                        lastEdited = r.lastEdited, version = r.version,
                        completion = r.completion,
                        onClick = { navController.navigate(Routes.RESUME_EDITOR) }
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResumeCard(
    title: String, targetJob: String, lastEdited: String, version: String,
    completion: Int, onClick: () -> Unit,
) {
    WhiteCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = InkBlack)
                Spacer(Modifier.height(2.dp))
                Text("目標 ・ $targetJob",
                    style = MaterialTheme.typography.bodySmall, color = InkGray500)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(InkGray100)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(version, style = MaterialTheme.typography.labelSmall,
                    color = InkGray700, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$completion%", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold, color = InkBlack)
            Spacer(Modifier.width(6.dp))
            Text("完成度", style = MaterialTheme.typography.labelSmall, color = InkGray500)
            Spacer(Modifier.weight(1f))
            Text(lastEdited, style = MaterialTheme.typography.labelSmall, color = InkGray400)
        }
        Spacer(Modifier.height(8.dp))
        ThinProgress(progress = completion / 100f)
    }
}
