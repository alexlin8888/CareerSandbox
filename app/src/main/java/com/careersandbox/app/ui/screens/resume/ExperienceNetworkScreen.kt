package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Force-directed graph 視覺化
 * - 節點互推(repulsion)
 * - 共同 tag 的節點互拉(attraction)
 * - 持續微震動,看起來像浮動的 3D 感
 */
private data class NodeState(
    val id: String,
    val experience: Experience,
    var pos: Offset,
    var velocity: Offset = Offset.Zero,
)

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)
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
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlowPurple.copy(alpha = 0.08f))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = GlowPurple,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "共同技能會連起來",
                        color = GlowPurple,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "點任一節點看相連的經歷,可拖拉節點重新排列",
                        color = InkGray700,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                    )
                }
            }

            // 圖
            ForceDirectedNetwork(
                experiences = experiences,
                selectedId = selectedId,
                onSelect = { id ->
                    selectedId = if (selectedId == id) null else id
                },
            )

            // 詳情卡
            selectedId?.let { id ->
                val exp = experiences.firstOrNull { it.id == id } ?: return@let
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandPeach.copy(alpha = 0.4f))
                        .padding(18.dp),
                ) {
                    Text(
                        exp.title,
                        color = InkBlack,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${exp.category} · ${exp.timeRange}",
                        color = Color(0xFF993C1D),
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
                    Spacer(Modifier.height(12.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        exp.tags.forEach { tag ->
                            Box(
                                Modifier
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

            Text(
                "全部經歷",
                color = InkGray500,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )

            experiences.forEach { exp ->
                ExperienceListRow(
                    exp = exp,
                    isSelected = selectedId == exp.id,
                    onClick = { selectedId = if (selectedId == exp.id) null else exp.id },
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ForceDirectedNetwork(
    experiences: List<Experience>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    val canvasHeight = 360.dp
    val nodeRadius = 32f
    val density = LocalDensity.current

    // 計算節點間共同 tag 連結強度
    val edges = remember(experiences) {
        val list = mutableListOf<Triple<Int, Int, Int>>()
        for (i in experiences.indices) {
            for (j in i + 1 until experiences.size) {
                val common = experiences[i].tags.intersect(experiences[j].tags.toSet()).size
                if (common > 0) list.add(Triple(i, j, common))
            }
        }
        list
    }

    // canvas size
    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    // 節點狀態(用 mutableStateListOf 觸發重繪)
    val nodes = remember(experiences, size) {
        if (size == androidx.compose.ui.geometry.Size.Zero) {
            mutableStateListOf<NodeState>()
        } else {
            val cx = size.width / 2
            val cy = size.height / 2
            val r = minOf(cx, cy) * 0.6f
            val random = Random(42)
            mutableStateListOf<NodeState>().apply {
                experiences.forEachIndexed { idx, exp ->
                    val angle = (2 * Math.PI * idx / experiences.size).toFloat() +
                            random.nextFloat() * 0.3f
                    add(
                        NodeState(
                            id = exp.id,
                            experience = exp,
                            pos = Offset(
                                cx + r * cos(angle),
                                cy + r * sin(angle),
                            ),
                        )
                    )
                }
            }
        }
    }

    // 動畫 tick(每 16ms 觸發 simulation step)
    var tick by remember { mutableIntStateOf(0) }
    LaunchedEffect(size, experiences) {
        if (size == androidx.compose.ui.geometry.Size.Zero) return@LaunchedEffect
        while (true) {
            delay(33L) // ~30fps
            tick++
        }
    }

    // 拖曳狀態
    var draggedIdx by remember { mutableStateOf<Int?>(null) }

    // Force simulation
    LaunchedEffect(tick) {
        if (nodes.isEmpty() || size == androidx.compose.ui.geometry.Size.Zero) return@LaunchedEffect

        val cx = size.width / 2
        val cy = size.height / 2

        // 累積每個節點的力
        val forces = Array(nodes.size) { Offset.Zero }

        // 1. 互推(repulsion)
        for (i in nodes.indices) {
            for (j in nodes.indices) {
                if (i == j) continue
                val dx = nodes[i].pos.x - nodes[j].pos.x
                val dy = nodes[i].pos.y - nodes[j].pos.y
                val distSq = (dx * dx + dy * dy).coerceAtLeast(100f)
                val dist = sqrt(distSq)
                val force = 8000f / distSq
                forces[i] = forces[i] + Offset(dx / dist * force, dy / dist * force)
            }
        }

        // 2. 連線拉近(attraction,根據共同 tag 數)
        edges.forEach { (i, j, common) ->
            val dx = nodes[j].pos.x - nodes[i].pos.x
            val dy = nodes[j].pos.y - nodes[i].pos.y
            val dist = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
            val k = common * 0.0008f
            forces[i] = forces[i] + Offset(dx * k, dy * k)
            forces[j] = forces[j] + Offset(-dx * k, -dy * k)
        }

        // 3. 拉回中心(中心引力)
        for (i in nodes.indices) {
            val dx = cx - nodes[i].pos.x
            val dy = cy - nodes[i].pos.y
            forces[i] = forces[i] + Offset(dx * 0.001f, dy * 0.001f)
        }

        // 4. 更新位置(速度 + damping)
        for (i in nodes.indices) {
            if (i == draggedIdx) continue
            val damping = 0.85f
            val newVel = (nodes[i].velocity + forces[i]) * damping
            val newPos = nodes[i].pos + newVel
            // 邊界限制
            val margin = nodeRadius + 8f
            val clampedX = newPos.x.coerceIn(margin, size.width - margin)
            val clampedY = newPos.y.coerceIn(margin, size.height - margin)
            nodes[i] = nodes[i].copy(
                pos = Offset(clampedX, clampedY),
                velocity = newVel,
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(InkGray100.copy(alpha = 0.4f))
            .onSizeChanged { intSize ->
                size = androidx.compose.ui.geometry.Size(
                    intSize.width.toFloat(),
                    intSize.height.toFloat(),
                )
            },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(experiences) {
                    detectTapGestures { tap ->
                        val hit = nodes.firstOrNull { node ->
                            val dx = tap.x - node.pos.x
                            val dy = tap.y - node.pos.y
                            dx * dx + dy * dy <= (nodeRadius + 8) * (nodeRadius + 8)
                        }
                        hit?.let { onSelect(it.id) }
                    }
                }
                .pointerInput(experiences) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            draggedIdx = nodes.indexOfFirst { node ->
                                val dx = offset.x - node.pos.x
                                val dy = offset.y - node.pos.y
                                dx * dx + dy * dy <= (nodeRadius + 8) * (nodeRadius + 8)
                            }.takeIf { it >= 0 }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            draggedIdx?.let { idx ->
                                val current = nodes[idx]
                                nodes[idx] = current.copy(
                                    pos = current.pos + dragAmount,
                                    velocity = Offset.Zero,
                                )
                            }
                        },
                        onDragEnd = { draggedIdx = null },
                        onDragCancel = { draggedIdx = null },
                    )
                },
        ) {
            if (nodes.isEmpty()) return@Canvas

            // 畫連線
            edges.forEach { (i, j, common) ->
                val a = nodes[i].pos
                val b = nodes[j].pos
                val highlighted = selectedId == nodes[i].id || selectedId == nodes[j].id
                drawLine(
                    color = if (highlighted) BrandDeepOrange
                    else InkGray300.copy(alpha = 0.5f),
                    start = a,
                    end = b,
                    strokeWidth = if (highlighted) 2.5f + common * 0.5f else 1f + common * 0.3f,
                )
            }

            // 畫節點(每個節點:外圈 halo + 主圓)
            nodes.forEach { node ->
                val color = categoryColor(node.experience.category)
                val isSelected = selectedId == node.id

                // 外圈
                drawCircle(
                    color = color.copy(alpha = if (isSelected) 0.3f else 0.18f),
                    radius = nodeRadius + (if (isSelected) 12f else 6f),
                    center = node.pos,
                )
                // 主節點
                drawCircle(
                    color = color,
                    radius = nodeRadius,
                    center = node.pos,
                )
                // 選中外環
                if (isSelected) {
                    drawCircle(
                        color = color,
                        radius = nodeRadius + 14f,
                        center = node.pos,
                        style = Stroke(width = 2.5f),
                    )
                }
            }
        }

        // 文字(Compose 而非 Canvas)
        nodes.forEach { node ->
            with(density) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (node.pos.x.toDp() - 40.dp),
                            y = (node.pos.y.toDp() - 8.dp),
                        )
                        .width(80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        node.experience.title.take(6),
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 11.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExperienceListRow(
    exp: Experience,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) BrandPeach.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            )
            .pressScale(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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

private operator fun Offset.times(scalar: Float): Offset = Offset(x * scalar, y * scalar)
