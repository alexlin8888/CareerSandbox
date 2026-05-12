package com.careersandbox.app.ui.screens.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.InterviewRecord
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewHistoryScreen(navController: NavHostController) {
    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("面試歷史", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(PaperWhite),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // 統計 hero
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandOrange.copy(alpha = 0.1f))
                        .padding(20.dp),
                ) {
                    StatBox("4", "完成", Modifier.weight(1f))
                    StatBox("71", "平均分", Modifier.weight(1f))
                    StatBox("78", "最高分", Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
            }
            // 標題
            item {
                Text(
                    "全部紀錄",
                    color = InkBlack,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                )
                Spacer(Modifier.height(12.dp))
            }
            // 紀錄列
            items(MockData.interviewHistory) { record ->
                HistoryRowFull(record) {
                    navController.navigate(Routes.INTERVIEW_REPORT)
                }
                SectionDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value,
            color = BrandDeepOrange,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp)
        Text(label,
            color = InkGray500,
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HistoryRowFull(r: InterviewRecord, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandYellow.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(r.type.label,
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(r.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray400)
            }
            Spacer(Modifier.height(4.dp))
            Text(r.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                color = InkBlack, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${r.score}",
                color = BrandOrange,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp)
            Spacer(Modifier.width(2.dp))
            Text("分",
                style = MaterialTheme.typography.labelSmall,
                color = InkGray500,
                modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}
