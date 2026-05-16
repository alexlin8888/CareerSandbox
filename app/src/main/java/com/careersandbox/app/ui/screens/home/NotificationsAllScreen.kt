package com.careersandbox.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.careersandbox.app.data.model.NotificationItem
import com.careersandbox.app.ui.components.SectionDivider
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsAllScreen(navController: NavHostController) {
    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text("全部提醒", fontWeight = FontWeight.Bold, color = InkBlack)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(PaperWhite),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        ) {
            item {
                // Hero 卡
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    BrandOrange.copy(alpha = 0.12f),
                                    BrandPeach.copy(alpha = 0.45f),
                                )
                            )
                        ),
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(
                            id = com.careersandbox.app.R.drawable.undraw_time_management_4ss6
                        ),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(130.dp),
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 20.dp, end = 140.dp),
                    ) {
                        Text("提醒中心",
                            color = BrandDeepOrange,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.labelLarge,
                            letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("近期的待辦與行程",
                            color = InkBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            lineHeight = 24.sp)
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text("最新", color = InkGray500,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp)
                Spacer(Modifier.height(12.dp))
            }
            // 重複列表展示
            val notiList = MockData.notifications + MockData.notifications
            itemsIndexed(notiList) { index, n ->
                StaggeredAppear(delayMillis = 80 + index * 50, durationMillis = 280) {
                    Column {
                        NotificationRowFull(n)
                        SectionDivider(modifier = Modifier.padding(vertical = 10.dp))
                    }
                }
            }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun NotificationRowFull(n: NotificationItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier.size(8.dp).clip(CircleShape)
                .background(BrandOrange)
                .padding(top = 8.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(n.title,
                color = InkBlack,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(n.body,
                color = InkGray500,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(n.time,
            color = InkGray400,
            style = MaterialTheme.typography.labelSmall)
    }
}
