package com.careersandbox.app.ui.screens.resume

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val CATEGORIES = listOf("社團", "工作", "競賽", "學業")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceNetworkScreen(navController: NavHostController) {
    val experiences = MockData.experiences

    val byCategory = remember(experiences) {
        CATEGORIES.associateWith { cat ->
            experiences.filter { it.category == cat }
        }
    }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var previewId by remember { mutableStateOf<String?>(null) }
    var modalId by remember { mutableStateOf<String?>(null) }

    val infinite = rememberInfiniteTransition(label = "halo")
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo-anim",
    )

    Scaffold(
        containerColor = PaperWhite,
        topBar = {
            TopAppBar(
                title = { Text("經歷網", fontWeight = FontWeight.Bold, color = InkBlack) },
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
            CategoryNetworkCanvas(
                experiences = experiences,
                byCategory = byCategory,
                canvasSize = canvasSize,
                haloAlpha = haloAlpha,
                onSizeChanged = { canvasSize = it },
                onSingleTap = { id -> previewId = id },
                onLongPress = { id -> modalId = id },
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (modalId != null) 8.dp else 0.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandDeepOrange.copy(alpha = 0.08f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = BrandDeepOrange,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val activeCats = CATEGORIES.count { byCategory[it]?.isNotEmpty() == true }
                    Text(
                        "你的 ${experiences.size} 段經歷,分 $activeCats 大類",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        "點:預覽 · 長按:詳情",
                        color = InkGray500,
                        fontSize = 10.sp,
                    )
                }
            }

            previewId?.let { id ->
                val exp = experiences.firstOrNull { it.id == id } ?: return@let
                NetworkPreviewSheet(
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
                NetworkDetailModal(
                    experience = exp,
                    onDismiss = { modalId = null },
                )
            }
        }
    }
}

@Composable
private fun CategoryNetworkCanvas(
    experiences: List<Experience>,
    byCategory: Map<String, List<Experience>>,
    canvasSize: IntSize,
    haloAlpha: Float,
    onSizeChanged: (IntSize) -> Unit,
    onSingleTap: (String) -> Unit,
    onLongPress: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    val positions = remember(canvasSize, byCategory) {
        computeLayout(canvasSize, byCategory)
    }
    val expPositions = positions.expPositions

    Canvas(
        modifier = modifier
            .onSizeChanged(onSizeChanged)
            .pointerInput(canvasSize, expPositions) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        findHitExperience(tapOffset, expPositions)?.let { onSingleTap(it) }
                    },
                    onLongPress = { tapOffset ->
                        findHitExperience(tapOffset, expPositions)?.let { onLongPress(it) }
                    },
                )
            },
    ) {
        drawStardust(size)

        positions.categoryPositions.forEach { (_, catPos) ->
            drawConnection(positions.center, catPos)
        }

        byCategory.forEach { (cat, exps) ->
            val catPos = positions.categoryPositions[cat] ?: return@forEach
            exps.forEach { exp ->
                val expPos = expPositions[exp.id] ?: return@forEach
                drawConnection(catPos, expPos)
            }
        }

        drawCenterNode(positions.center, haloAlpha)

        positions.categoryPositions.forEach { (cat, pos) ->
            drawCategoryNode(pos, cat, byCategory[cat]?.size ?: 0, density)
        }

        experiences.forEach { exp ->
            val pos = expPositions[exp.id] ?: return@forEach
            drawExperienceLeaf(pos, exp, density)
        }
    }
}

private data class NetworkLayout(
    val center: Offset,
    val categoryPositions: Map<String, Offset>,
    val expPositions: Map<String, Offset>,
)

private fun computeLayout(
    canvasSize: IntSize,
    byCategory: Map<String, List<Experience>>,
): NetworkLayout {
    if (canvasSize.width == 0 || canvasSize.height == 0) {
        return NetworkLayout(Offset.Zero, emptyMap(), emptyMap())
    }

    val cx = canvasSize.width / 2f
    val cy = canvasSize.height / 2f
    val center = Offset(cx, cy)
    val maxR = minOf(cx, cy) * 0.85f

    val catRadius = maxR * 0.45f
    val catAngles = mapOf(
        "社團" to (-PI / 2),
        "工作" to 0.0,
        "競賽" to (PI / 2),
        "學業" to PI,
    )
    val categoryPositions = CATEGORIES.associateWith { cat ->
        val angle = catAngles[cat] ?: 0.0
        Offset(
            (cx + cos(angle) * catRadius).toFloat(),
            (cy + sin(angle) * catRadius).toFloat(),
        )
    }

    val expPositions = mutableMapOf<String, Offset>()
    byCategory.forEach { (cat, exps) ->
        if (exps.isEmpty()) return@forEach
        val catAngle = catAngles[cat] ?: 0.0
        val expRadius = maxR * 0.85f

        if (exps.size == 1) {
            expPositions[exps[0].id] = Offset(
                (cx + cos(catAngle) * expRadius).toFloat(),
                (cy + sin(catAngle) * expRadius).toFloat(),
            )
        } else {
            val spread = (PI / 3).toFloat()
            val step = if (exps.size > 1) spread * 2f / (exps.size - 1) else 0f
            exps.forEachIndexed { idx, exp ->
                val expAngle = catAngle - spread + step * idx
                expPositions[exp.id] = Offset(
                    (cx + cos(expAngle) * expRadius).toFloat(),
                    (cy + sin(expAngle) * expRadius).toFloat(),
                )
            }
        }
    }

    return NetworkLayout(center, categoryPositions, expPositions)
}

private fun findHitExperience(
    tap: Offset,
    positions: Map<String, Offset>,
): String? {
    val hitR = 36f
    return positions.entries
        .minByOrNull { (_, p) ->
            val dx = p.x - tap.x
            val dy = p.y - tap.y
            dx * dx + dy * dy
        }
        ?.takeIf { (_, p) ->
            val dx = p.x - tap.x
            val dy = p.y - tap.y
            kotlin.math.sqrt(dx * dx + dy * dy) < hitR
        }
        ?.key
}

private fun DrawScope.drawStardust(size: Size) {
    var s = 17
    repeat(20) {
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val x = (s % 1000) / 1000f * size.width
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val y = (s % 1000) / 1000f * size.height
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val r = ((s % 1000) / 1000f) * 1.5f + 0.5f
        s = (s * 1103515245 + 12345) and 0x7fffffff
        val alpha = ((s % 1000) / 1000f) * 0.25f + 0.08f
        drawCircle(
            color = BrandOrange.copy(alpha = alpha),
            radius = r,
            center = Offset(x, y),
        )
    }
}

private fun DrawScope.drawConnection(a: Offset, b: Offset) {
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                BrandDeepOrange.copy(alpha = 0.45f),
                BrandAmber.copy(alpha = 0.25f),
            ),
            start = a,
            end = b,
        ),
        start = a,
        end = b,
        strokeWidth = 1.8f,
    )
}

private fun DrawScope.drawCenterNode(center: Offset, haloAlpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                BrandDeepOrange.copy(alpha = haloAlpha),
                BrandDeepOrange.copy(alpha = 0f),
            ),
            center = center,
            radius = 55f,
        ),
        radius = 55f,
        center = center,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(BrandAmber, BrandDeepOrange, Color(0xFF993C1D)),
            center = Offset(center.x - 6, center.y - 6),
            radius = 32f,
        ),
        radius = 30f,
        center = center,
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = 12f,
        center = Offset(center.x - 8, center.y - 8),
    )
    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.WHITE)
            textSize = 30f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("你", center.x, center.y + 11f, paint)
    }
}

private fun DrawScope.drawCategoryNode(
    pos: Offset,
    category: String,
    count: Int,
    density: Float,
) {
    val isActive = count > 0
    val color = if (isActive) BrandDeepOrange else InkGray400
    val radius = 22f

    if (isActive) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.3f),
                    color.copy(alpha = 0f),
                ),
                center = pos,
                radius = radius * 1.9f,
            ),
            radius = radius * 1.9f,
            center = pos,
        )
    }
    drawCircle(color = color, radius = radius, center = pos)
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = radius * 0.4f,
        center = Offset(pos.x - radius * 0.35f, pos.y - radius * 0.35f),
    )
    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.WHITE)
            textSize = 13f * density
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(category, pos.x, pos.y + 5f, paint)
    }
    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.parseColor("#6B7280"))
            textSize = 10f * density
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("$count 段", pos.x, pos.y + radius + 18f, paint)
    }
}

private fun DrawScope.drawExperienceLeaf(
    pos: Offset,
    experience: Experience,
    density: Float,
) {
    val radius = 18f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                BrandAmber.copy(alpha = 0.35f),
                BrandAmber.copy(alpha = 0f),
            ),
            center = pos,
            radius = radius * 2f,
        ),
        radius = radius * 2f,
        center = pos,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(BrandAmber, BrandDeepOrange),
            center = Offset(pos.x - 4, pos.y - 4),
            radius = radius * 1.2f,
        ),
        radius = radius,
        center = pos,
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = radius * 0.4f,
        center = Offset(pos.x - radius * 0.35f, pos.y - radius * 0.35f),
    )
    val shortTitle = if (experience.title.length > 7) {
        experience.title.take(7) + "…"
    } else {
        experience.title
    }
    drawContext.canvas.nativeCanvas.let { canvas ->
        val paint = android.graphics.Paint().apply {
            setColor(android.graphics.Color.parseColor("#0B0E14"))
            textSize = 10f * density
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(shortTitle, pos.x, pos.y + radius + 18f, paint)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkPreviewSheet(
    experience: Experience,
    onDismiss: () -> Unit,
    onSeeMore: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

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
                    .background(BrandDeepOrange)
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
                            .background(BrandDeepOrange.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            tag,
                            color = BrandDeepOrange,
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
private fun NetworkDetailModal(
    experience: Experience,
    onDismiss: () -> Unit,
) {
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
                        .background(BrandDeepOrange)
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
                            .background(BrandDeepOrange.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text(
                            tag,
                            color = BrandDeepOrange,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
