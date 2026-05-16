package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Experience
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceNetworkScreen(navController: NavHostController) {
    val experiences = MockData.experiences
    var selectedId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text("經歷關聯網", fontWeight = FontWeight.Bold, color = InkBlack)
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
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // 介紹卡
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlowPurple.copy(alpha = 0.08f))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = GlowPurple,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "共同技能連起來",
                        color = GlowPurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "點任一段經驗可高亮相關連結。可探索:這些經驗連起來通往哪個方向。",
                        color = InkGray700,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                    )
                }
            }

            // 網路圖
            ExperienceNetworkCanvas(
                experiences = experiences,
                selectedId = selectedId,
                onSelect = { id -> selectedId = if (selectedId == id) null else id },
            )

            // 下方:被選中經驗的詳情
            selectedId?.let { id ->
                val exp = experiences.firstOrNull { it.id == id }
                if (exp != null) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandPeach.copy(alpha = 0.4f))
                            .padding(18.dp),
                    ) {
                        Text(
                            exp.title,
                            color = InkBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "${exp.category} · ${exp.timeRange}",
                            color = InkGray500,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            exp.description,
                            color = InkGray700,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                        )
                        Spacer(Modifier.height(10.dp))
                        // 標籤
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            exp.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(BrandDeepOrange)
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        tag,
                                        color = PaperWhite,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 經驗列表(底部)
            Text(
                "全部經驗",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
            experiences.forEach { exp ->
                ExperienceRow(
                    exp = exp,
                    isSelected = selectedId == exp.id,
                    onClick = { selectedId = if (selectedId == exp.id) null else exp.id },
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ExperienceRow(
    exp: Experience,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BrandPeach.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)
            .pressScale(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 節點 dot
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(categoryColor(exp.category)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                exp.title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "${exp.category} · ${exp.timeRange}",
                color = InkGray500,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Text(
            "${exp.tags.size} 標籤",
            color = InkGray400,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private fun categoryColor(category: String): Color = when (category) {
    "工作" -> BrandDeepOrange
    "競賽" -> AccentGreen
    "社團" -> GlowPurple
    "學業" -> BrandAmber
    else -> InkGray500
}

@Composable
private fun ExperienceNetworkCanvas(
    experiences: List<Experience>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    // 計算節點位置(圓形佈局)
    val nodeRadius = 36f
    val canvasHeightDp = 360.dp

    // pulse 動畫(讓圖有生命感)
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    // 計算節點位置(根據經驗數量,均勻分布在圓上)
    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    val nodePositions: List<Pair<String, Offset>> = remember(experiences, canvasSize) {
        if (canvasSize == androidx.compose.ui.geometry.Size.Zero) {
            emptyList()
        } else {
            val cx = canvasSize.width / 2
            val cy = canvasSize.height / 2
            val r = minOf(cx, cy) - 60f
            experiences.mapIndexed { idx, exp ->
                val angle = (2 * Math.PI * idx / experiences.size) - Math.PI / 2
                exp.id to Offset(
                    x = cx + r * cos(angle).toFloat(),
                    y = cy + r * sin(angle).toFloat(),
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeightDp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(InkGray100.copy(alpha = 0.4f))
            .onSizeChanged { intSize ->
                canvasSize = androidx.compose.ui.geometry.Size(
                    intSize.width.toFloat(),
                    intSize.height.toFloat(),
                )
            },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(experiences, nodePositions) {
                    detectTapGestures { tap ->
                        val hit = nodePositions.firstOrNull { (_, pos) ->
                            val dx = tap.x - pos.x
                            val dy = tap.y - pos.y
                            dx * dx + dy * dy <= (nodeRadius + 10) * (nodeRadius + 10)
                        }
                        hit?.let { onSelect(it.first) }
                    }
                },
        ) {
            if (nodePositions.isEmpty()) return@Canvas

            // 先畫連線(共同 tag 的經驗連起來)
            for (i in experiences.indices) {
                for (j in i + 1 until experiences.size) {
                    val expA = experiences[i]
                    val expB = experiences[j]
                    val commonTags = expA.tags.intersect(expB.tags.toSet())
                    if (commonTags.isNotEmpty()) {
                        val posA = nodePositions[i].second
                        val posB = nodePositions[j].second
                        val hilight = selectedId == expA.id || selectedId == expB.id
                        drawLine(
                            color = if (hilight) BrandDeepOrange
                            else InkGray300.copy(alpha = 0.6f),
                            start = posA,
                            end = posB,
                            strokeWidth = if (hilight) 2.5f else 1.2f,
                        )
                    }
                }
            }

            // 再畫節點
            experiences.forEachIndexed { idx, exp ->
                val pos = nodePositions[idx].second
                val isSelected = selectedId == exp.id
                val nodeColor = when (exp.category) {
                    "工作" -> BrandDeepOrange
                    "競賽" -> AccentGreen
                    "社團" -> GlowPurple
                    "學業" -> BrandAmber
                    else -> InkGray500
                }
                val r = if (isSelected) nodeRadius * pulse else nodeRadius

                // 外圈(透明 halo)
                drawCircle(
                    color = nodeColor.copy(alpha = 0.2f),
                    radius = r + 8f,
                    center = pos,
                )
                // 主節點
                drawCircle(
                    color = nodeColor,
                    radius = r,
                    center = pos,
                )
                // 選中外環
                if (isSelected) {
                    drawCircle(
                        color = nodeColor,
                        radius = r + 12f,
                        center = pos,
                        style = Stroke(width = 2f),
                    )
                }
            }
        }

        // 節點上覆蓋一個 Compose Text(因為 Canvas 畫中文字較麻煩)
        if (nodePositions.isNotEmpty()) {
            experiences.forEachIndexed { idx, exp ->
                val pos = nodePositions[idx].second
                with(LocalDensity.current) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (pos.x.toDp() - 50.dp),
                                y = (pos.y.toDp() - 8.dp),
                            )
                            .width(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            exp.title.take(8),
                            color = PaperWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
