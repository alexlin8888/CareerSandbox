package com.careersandbox.app.ui.screens.workplace

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch

private enum class NodeKind { PLAY, PLANNED, CHEST }

private data class PathDay(
    val dayNo: Int,
    val title: String,
    val icon: ImageVector,
    val route: String?,
    val offsetX: Int,        // S 曲線水平偏移(dp,相對置中)
    val kind: NodeKind,
)

@Composable
fun WorkplaceSandboxScreen(navController: NavHostController) {
    var industry by remember { mutableStateOf("科技 / 網路") }

    val week = listOf(
        PathDay(1, "和主管 1on1", Icons.Outlined.SupervisorAccount, Routes.WORKPLACE_CHAT, -36, NodeKind.PLAY),
        PathDay(2, "Email 風暴日", Icons.Outlined.Email, Routes.WORKPLACE_EMAIL, 52, NodeKind.PLAY),
        PathDay(3, "跨部門會議", Icons.Outlined.Groups, null, 80, NodeKind.PLANNED),
        PathDay(4, "同事午餐", Icons.Outlined.Coffee, null, 16, NodeKind.PLANNED),
        PathDay(5, "週五回顧", Icons.Outlined.EmojiEvents, Routes.WORKPLACE_REVIEW, -56, NodeKind.CHEST),
    )

    Box(Modifier.fillMaxSize().background(PaperOff)) {
        SandboxBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
        Spacer(Modifier.height(24.dp))
        Text(
            "職場沙盒",
            style = MaterialTheme.typography.headlineLarge,
            color = InkBlack,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        )

        // === 單元橫幅(合併原 hero)===
        StaggeredAppear(delayMillis = 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandDeepOrange)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Text("第 1 週 ・ 試用期",
                    color = PaperWhite.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp)
                Spacer(Modifier.height(4.dp))
                Text("入職第一週",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp)
                Spacer(Modifier.height(6.dp))
                Text("在這裡踩雷,總比上班才踩好。",
                    color = PaperWhite.copy(alpha = 0.85f),
                    fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(30.dp))

        // === zigzag 路徑 ===
        week.forEachIndexed { idx, day ->
            StaggeredAppear(delayMillis = 120 + idx * 90) {
                PathNodeBlock(
                    day = day,
                    isCurrent = idx == 0,
                    onClick = day.route?.let { r -> { navController.navigate(r) } },
                )
            }
            if (idx != week.lastIndex) Spacer(Modifier.height(22.dp))
        }

        Spacer(Modifier.height(36.dp))

        // === 產業選擇(次要,移至頁尾)===
        IndustrySelector(selected = industry, onSelect = { industry = it })
        Spacer(Modifier.height(6.dp))
        Text(
            if (industry == "科技 / 網路") "目前的一週以「科技 / 網路」的日常為底。"
            else "這週先走通用版,「$industry」的一週之後會有。",
            color = InkGray400,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(40.dp))
    }
    }
}

/* ===================== 路徑節點(3D 圓鈕 + 狀態)===================== */

@Composable
private fun PathNodeBlock(
    day: PathDay,
    isCurrent: Boolean,
    onClick: (() -> Unit)?,
) {
    val scope = rememberCoroutineScope()
    val wiggle = remember { Animatable(0f) }

    // 當前節點呼吸
    val pulse = rememberInfiniteTransition(label = "pulse-${day.dayNo}")
    val breath by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent) 1.06f else 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "breath",
    )
    // 河狸漂浮
    val floatY by pulse.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )

    val mainColor = when (day.kind) {
        NodeKind.PLAY -> BrandDeepOrange
        NodeKind.PLANNED -> InkGray200
        NodeKind.CHEST -> BrandYellow
    }
    val rimColor = when (day.kind) {
        NodeKind.PLAY -> Color.Black.copy(alpha = 0.30f).compositeOver(BrandDeepOrange)
        NodeKind.PLANNED -> InkGray300
        NodeKind.CHEST -> BrandAmber
    }
    val iconTint = when (day.kind) {
        NodeKind.PLAY -> PaperWhite
        NodeKind.PLANNED -> InkGray400
        NodeKind.CHEST -> InkCharcoal
    }

    Box(Modifier.fillMaxWidth()) {
        // 節點 + 標籤(S 曲線偏移)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(x = day.offsetX.dp)
                .graphicsLayer { rotationZ = wiggle.value },
        ) {
            // 3D 圓鈕:底圓(厚度)+ 主圓
            Box(
                modifier = Modifier
                    .width(68.dp)
                    .height(73.dp)
                    .scale(breath)
                    .then(
                        if (onClick != null) Modifier.pressScale(onClick = onClick)
                        else Modifier.pressScale {
                            scope.launch {
                                wiggle.animateTo(0f, keyframes {
                                    durationMillis = 340
                                    -4f at 70
                                    4f at 160
                                    -2f at 250
                                    0f at 340
                                })
                            }
                        }
                    ),
            ) {
                Box(
                    Modifier
                        .size(68.dp)
                        .align(Alignment.BottomCenter)
                        .clip(CircleShape)
                        .background(rimColor),
                )
                Box(
                    Modifier
                        .size(68.dp)
                        .align(Alignment.TopCenter)
                        .clip(CircleShape)
                        .background(mainColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(day.icon, contentDescription = null,
                        tint = iconTint, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            // 標籤膠囊
            Box(
                Modifier
                    .shadow(2.dp, RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(PaperWhite)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    "Day ${day.dayNo} ・ ${day.title}",
                    color = if (day.kind == NodeKind.PLANNED) InkGray400 else InkBlack,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // 當前節點:爬山河狸坐旁邊 + 提示泡泡
        if (isCurrent) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (day.offsetX + 104).dp, y = floatY.dp - 14.dp),
            ) {
                Box(
                    Modifier
                        .shadow(3.dp, RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .background(PaperWhite)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text("從這裡開始",
                        color = BrandDeepOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(4.dp))
                Image(
                    painter = painterResource(R.drawable.beaver_climb),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(66.dp),
                )
            }
        }
    }
}

/* ===================== 產業選擇(可擴充)===================== */

@Composable
private fun IndustrySelector(selected: String, onSelect: (String) -> Unit) {
    val scroll = rememberScrollState()
    val industries = listOf(
        "科技 / 網路" to true,
        "金融" to false,
        "行銷 / 廣告" to false,
        "製造 / 工程" to false,
        "醫療" to false,
        "公部門" to false,
    )
    Column {
        Text(
            "選一個產業",
            color = InkGray500,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            industries.forEach { (name, ready) ->
                val isSel = name == selected
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSel) BrandDeepOrange else MaterialTheme.colorScheme.surface)
                        .pressScale { onSelect(name) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        name,
                        color = if (isSel) PaperWhite else InkGray700,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!ready) {
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "未解鎖",
                            color = if (isSel) PaperWhite.copy(alpha = 0.8f) else InkGray400,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

// 路徑頁背景:暖色天空 + 太陽 + 遠山 + 星塵(固定層,內容滾動時自帶微視差)
@Composable
private fun SandboxBackdrop() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(BrandPeach.copy(alpha = 0.35f), PaperOff, BrandAmber.copy(alpha = 0.12f)),
                startY = 0f, endY = h,
            ),
        )
        // 太陽光暈(右上)
        drawCircle(BrandAmber.copy(alpha = 0.20f), radius = w * 0.20f, center = Offset(w * 0.85f, h * 0.10f))
        drawCircle(BrandAmber.copy(alpha = 0.12f), radius = w * 0.28f, center = Offset(w * 0.85f, h * 0.10f))
        // 遠山(兩座大圓弧,只露山脊)
        drawCircle(BrandPeach.copy(alpha = 0.30f), radius = w * 0.55f, center = Offset(w * 0.15f, h * 0.30f + w * 0.55f))
        drawCircle(BrandOrange.copy(alpha = 0.10f), radius = w * 0.65f, center = Offset(w * 0.92f, h * 0.34f + w * 0.65f))
        // 星塵點綴
        val sparks = listOf(
            Offset(w * 0.10f, h * 0.16f), Offset(w * 0.30f, h * 0.07f),
            Offset(w * 0.62f, h * 0.20f), Offset(w * 0.20f, h * 0.42f),
            Offset(w * 0.85f, h * 0.48f), Offset(w * 0.12f, h * 0.66f),
            Offset(w * 0.88f, h * 0.72f), Offset(w * 0.45f, h * 0.88f),
        )
        sparks.forEachIndexed { i, o ->
            drawCircle(
                color = if (i % 2 == 0) BrandOrange.copy(alpha = 0.18f) else BrandAmber.copy(alpha = 0.22f),
                radius = if (i % 3 == 0) 7f else 4.5f,
                center = o,
            )
        }
    }
}
