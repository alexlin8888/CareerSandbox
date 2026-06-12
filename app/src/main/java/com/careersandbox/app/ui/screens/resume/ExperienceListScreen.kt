package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Experience
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ExperienceListScreen(navController: NavHostController) {
    val categories = listOf("全部", "學業", "工作", "社團", "競賽", "其他")
    var selectedCategory by remember { mutableStateOf("全部") }

    Scaffold(
        containerColor = PaperOff,
        topBar = {
            TopAppBar(
                title = { Text("經驗收集", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.EXPERIENCE_EDIT) }) {
                        Icon(Icons.Outlined.Add, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperOff),
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories) { c ->
                    PillChip(label = c, selected = c == selectedCategory,
                        onClick = { selectedCategory = c })
                }
            }
            Spacer(Modifier.height(12.dp))

            val filtered = if (selectedCategory == "全部") MockData.experiences
            else MockData.experiences.filter { it.category == selectedCategory }

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(40.dp))
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = com.careersandbox.app.R.drawable.beaver_peek
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        if (selectedCategory == "全部") "還沒有任何經歷" else "這個分類還沒有經歷",
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "用右上角的 + 新增第一筆,它們會組成你的母版",
                        color = InkGray500,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(filtered) { e -> ExperienceCard(e) { navController.navigate(Routes.EXPERIENCE_EDIT) } }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ExperienceCard(e: Experience, onClick: () -> Unit = {}) {
    WhiteCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkBlack)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(e.category, style = MaterialTheme.typography.labelSmall,
                    color = PaperWhite, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(8.dp))
            Text(e.timeRange, style = MaterialTheme.typography.labelSmall, color = InkGray500)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.MoreHoriz, contentDescription = null,
                tint = InkGray400, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(e.title, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = InkBlack)
        Spacer(Modifier.height(4.dp))
        Text(e.description, style = MaterialTheme.typography.bodyMedium,
            color = InkGray500, maxLines = 2)
        Spacer(Modifier.height(12.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            e.tags.forEach { tag ->
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandPeach)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(tag, style = MaterialTheme.typography.labelSmall,
                        color = BrandDeepOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
