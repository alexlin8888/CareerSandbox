package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Experience
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceNetworkScreen(navController: NavHostController) {
    val experiences = MockData.experiences

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var focusedId by remember { mutableStateOf<String?>(null) }
    var previewId by remember { mutableStateOf<String?>(null) }
    var modalId by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    val infinite = rememberInfiniteTransition(label = "flow")
    val flowPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
        ),
        label = "phase"
    )
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo"
    )

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("經歷關聯網", fontWeight = FontWeight.Bold, color = InkBlack) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = InkBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
            )
        },
    ) { pad ->
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .background(PaperWhite),
        ) {
            NetworkCanvas(
                experiences = experiences,
                canvasSize = canvasSize,
                focusedId = focusedId,
                flowPhase = flowPhase,
                haloAlpha = haloAlpha,
                scale = scale,
                pan = pan,
                onSizeChanged = { canvasSize = it },
                onSingleTap = { id -> previewId = id },
                onDoubleTap = { id -> focusedId = if (focusedId == id) null else id },
                onLongPress = { id -> modalId = id },
                onPanZoom = { dPan, dScale ->
                    pan += dPan
                    scale = (scale * dScale).coerceIn(0.5f, 2.5f)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (modalId != null) 8.dp else 0.dp),
            )

            // Hint card
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(GlowPurple.copy(alpha = 0.1f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = GlowPurple,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "點:預覽 · 雙點:聚焦 · 長按:詳情",
                        color = GlowPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        "雙指縮放 · 拖曳移動",
                        color = InkGray500,
                        fontSize = 10.sp,
                    )
                }
            }

            // Reset 按鈕
            if (focusedId != null || scale != 1f || pan != Offset.Zero) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(InkBlack.copy(alpha = 0.85f))
                        .pressScale {
                            focusedId = null
                            scale = 1f
                            pan = Offset.Zero
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.OpenInFull,
                        contentDescription = null,
                        tint = PaperWhite,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            previewId?.let { id ->
                val exp = experiences.firstOrNull { it.id == id } ?: return@let
                PreviewSheet(
                    experience = exp,
                    onDismiss = { previewId = null },
                    onSeeMore = {
                        previewId = null
                        modalId = id
                    },
                )
            }

            modalId?.let { id ->
                val exp = experiences.firstOrNull { it.id == id } ?: return@let
                DetailModal(
                    experience = exp,
                    onDismiss = { modalId = null },
                )
            }
        }
    }
}

@Composable
private fun NetworkCanvas(
    experiences: List<Experience>,
    canvasSize: IntSize,
    focusedId: String?,
    flowPhase: Float,
    haloAlpha: Float,
    scale: Float,
    pan: Offset,
    onSizeChanged: (IntSize) -> Unit,
    onSingleTap: (String) -> Unit,
    onDoubleTap: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onPanZoom: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    val nodePositions = remember(canvasSize, focusedId) {
        computeNodePositions(experiences, canvasSize, focusedId)
    }

    val animatedPositions = nodePositions.mapValues { (id, target) ->
        val x by animateFloatAsState(target.x, animationSpec = tween(500, easing = FastOutSlowInEasing), label = "x-$id")
        val y by animateFloatAsState(target.y, animationSpec = tween(500, easing = FastOutSlowInEasing), label = "y-$id")
        Offset(x, y)
    }

    val edges = remember(experiences) {
        val list = mutableListOf<Pair<String, String>>()
        for (i in experiences.indices) {
            for (j in i + 1 until experiences.size) {
                val a = experiences[i]
                val b = experiences[j]
                if (a.tags.intersect(b.tags.toSet()).isNotEmpty()) {
                    list.add(a.id to b.id)
                }
            }
        }
        list
    }

    Canvas(
        modifier = modifier
            .onSizeChanged(onSizeChanged)
            .pointerInput(Unit) {
                detectTransformGestures { _, panDelta, zoom, _ ->
                    onPanZoom(panDelta, zoom)
                }
            }
            .pointerInput(canvasSize, animatedPositions, scale, pan) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        findHitNode(tapOffset, animatedPositions, scale, pan)?.let { onSingleTap(it) }
                    },
                    onDoubleTap = { tapOffset ->
                        findHitNode(tapOffset, animatedPositions, scale, pan)?.let { onDoubleTap(it) }
                    },
                    onLongPress = { tapOffset ->
                        findHitNode(tapOffset, animatedPositions, scale, pan)?.let { onLongPress(it) }
                    },
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = pan.x
                translationY = pan.y
            },
    ) {
        drawStardust(size)
        drawOrbits(size)

        for ((aId, bId) in edges) {
            val pa = animatedPositions[aId] ?: continue
            val pb = animatedPositions[bId] ?: continue
            drawConnection(pa, pb, focusedId == aId || focusedId == bId)
            drawFlowDot(pa, pb, flowPhase)
        }

        drawCenterNode(size, haloAlpha)

        experiences.forEach { exp ->
            val pos = animatedPositions[exp.id] ?: return@forEach
            drawExperienceNode(pos, exp, focusedId == exp.id, density)
        }
    }
}

private fun computeNodePositions(
    experiences: List<Experience>,
    canvasSize: IntSize,
    focusedId: String?,
): Map<String, Offset> {
    if (canvasSize.width == 0 || canvasSize.height == 0) return emptyMap()

    val cx = canvasSize.width / 2f
    val cy = canvasSize.height / 2f
    val maxR = minOf(cx, cy) * 0.78f
    val result = mutableMapOf<String, Offset>()

    if (focusedId != null) {
        val focused = experiences.firstOrNull { it.id == focusedId }
        val others = experiences.filter { it.id != focusedId }

        focused?.let {
            result[it.id] = Offset(cx, cy - maxR * 0.35f)
        }

        val angleStep = 2 * PI / others.size
        others.forEachIndexed { idx, exp ->
            val angle = idx * angleStep - PI / 2
            val radius = maxR * 0.85f
            result[exp.id] = Offset(
                (cx + cos(angle) * radius).toFloat(),
                (cy + sin(angle) * radius).toFloat(),
            )
        }
    } else {
        val inner = experiences.take(3)
        val outer = experiences.drop(3)

        val innerR = maxR * 0.5f
        val outerR = maxR * 0.85f

        inner.forEachIndexed { idx, exp ->
            val angle = idx * (2 * PI / inner.size) - PI / 2
            result[exp.id] = Offset(
                (cx + cos(angle) * innerR).toFloat(),
                (cy + sin(angle) * innerR).toFloat(),
            )
        }
        outer.forEachIndexed { idx, exp ->
            val baseAngle = idx * (2 * PI / outer.size)
            val angle = baseAngle - PI / 2 + (PI / outer.size)
            result[exp.id] = Offset(
                (cx + cos(angle) * outerR).toFloat(),
                (cy + sin(angle) * outerR).toFloat(),
            )
        }
    }
    return result
}

private fun findHitNode(
    tapOffset: Offset,
    positions: Map<String, Offset>,
    scale: Float,
    pan: Offset,
): String? {
    val real = Offset(
        (tapOffset.x - pan.x) / scale,
        (tapOffset.y - pan.y) / scale,
    )
    val hitRadius = 36f
    return positions.entries
        .minByOrNull { (_, p) ->
            val dx = p.x - real.x
            val dy = p.y - real.y
            dx * dx + dy * dy
        }
        ?.takeIf { (_, p) ->
            val dx = p.x - real.x
            val dy = p.y - real.y
            kotlin.math.sqrt(dx * dx + dy * dy) < hitRadius
        }
        ?.key
}

private fun DrawScope.drawStardust(size: Size) {
    var s = 7
    repeat(24) {
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val x = (s % 1000) / 1000f * size.width
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val y = (s % 1000) / 1000f * size.height
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val r = ((s % 1000) / 1000f) * 1.5f + 0.5f
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val alpha = ((s % 1000) / 1000f) * 0.3f + 0.1f
        drawCircle(
            color = BrandOrange.copy(alpha = alpha),
            radius = r,
            center = Offset(x, y),
        )
    }
}

private fun DrawScope.drawOrbits(size: Size) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxR = minOf(cx, cy) * 0.78f
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

    listOf(0.5f, 0.85f).forEach { factor ->
        drawCircle(
            color = BrandOrange.copy(alpha = 0.12f),
            radius = maxR * factor,
            center = Offset(cx, cy),
            style = Stroke(width = 1f, pathEffect = dashEffect),
        )
    }
}

private fun DrawScope.drawCenterNode(size: Size, haloAlpha: Float) {
    val center = Offset(size.width / 2f, size.height / 2f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                BrandDeepOrange.copy(alpha = haloAlpha),
                BrandDeepOrange.copy(alpha = 0f),
            ),
            center = center,
            radius = 50f,
        ),
        radius = 50f,
        center = center,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                BrandAmber,
                BrandDeepOrange,
                Color(0xFF993C1D),
            ),
            center = Offset(center.x - 5, center.y - 5),
            radius = 28f,
        ),
        radius = 28f,
        center = center,
    )
    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.WHITE)
            textSize = 28f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("你", center.x, center.y + 10f, paint)
    }
}

private fun DrawScope.drawExperienceNode(
    pos: Offset,
    experience: Experience,
    isFocused: Boolean,
    density: Float,
) {
    val color = colorForCategory(experience.category)
    val baseRadius = if (isFocused) 32f else 24f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = if (isFocused) 0.35f else 0.18f),
                color.copy(alpha = 0f),
            ),
            center = pos,
            radius = baseRadius * 1.8f,
        ),
        radius = baseRadius * 1.8f,
        center = pos,
    )
    drawCircle(color = color, radius = baseRadius, center = pos)
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = baseRadius * 0.45f,
        center = Offset(pos.x - baseRadius * 0.35f, pos.y - baseRadius * 0.35f),
    )

    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.parseColor("#2A2A2A"))
            textSize = if (isFocused) 12f * density else 10f * density
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            experience.category,
            pos.x,
            pos.y + baseRadius + 18f,
            paint,
        )
    }
}

private fun DrawScope.drawConnection(a: Offset, b: Offset, isFocused: Boolean) {
    drawLine(
        color = if (isFocused) BrandDeepOrange.copy(alpha = 0.65f) else BrandOrange.copy(alpha = 0.3f),
        start = a,
        end = b,
        strokeWidth = if (isFocused) 2.5f else 1.5f,
    )
}

private fun DrawScope.drawFlowDot(a: Offset, b: Offset, phase: Float) {
    listOf(0f, 0.33f, 0.66f).forEach { offset ->
        val t = (phase + offset) % 1f
        val x = a.x + (b.x - a.x) * t
        val y = a.y + (b.y - a.y) * t
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrandAmber.copy(alpha = 0.4f),
                    BrandAmber.copy(alpha = 0f),
                ),
                center = Offset(x, y),
                radius = 6f,
            ),
            radius = 6f,
            center = Offset(x, y),
        )
        drawCircle(color = BrandAmber, radius = 2f, center = Offset(x, y))
    }
}

private fun colorForCategory(category: String): Color = when (category) {
    "社團" -> GlowPurple
    "工作" -> BrandDeepOrange
    "競賽" -> AccentGreen
    "學術" -> BrandAmber
    else -> InkGray500
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewSheet(
    experience: Experience,
    onDismiss: () -> Unit,
    onSeeMore: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val color = colorForCategory(experience.category)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PaperWhite,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(color)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    experience.category,
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.height(12.dp))

            Text(
                experience.title,
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                lineHeight = 26.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                experience.timeRange,
                color = InkGray500,
                fontSize = 12.sp,
            )

            Spacer(Modifier.height(14.dp))

            Text(
                experience.description,
                color = InkGray700,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                maxLines = 3,
            )

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                experience.tags.take(4).forEach { tag ->
                    Box(
                        Modifier
                            .padding(end = 6.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            tag,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandDeepOrange)
                    .pressScale(onClick = onSeeMore)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    "看完整內容",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun DetailModal(
    experience: Experience,
    onDismiss: () -> Unit,
) {
    val color = colorForCategory(experience.category)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack.copy(alpha = 0.55f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PaperWhite)
                .padding(24.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { })
                },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(color)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        experience.category,
                        color = PaperWhite,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(InkGray100)
                        .pressScale(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = null,
                        tint = InkBlack,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                experience.title,
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                lineHeight = 30.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                experience.timeRange,
                color = InkGray500,
                fontSize = 13.sp,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "經歷描述",
                color = InkGray500,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                experience.description,
                color = InkBlack,
                fontSize = 15.sp,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "相關技能",
                color = InkGray500,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                experience.tags.forEach { tag ->
                    Box(
                        Modifier
                            .padding(end = 6.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text(
                            tag,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
