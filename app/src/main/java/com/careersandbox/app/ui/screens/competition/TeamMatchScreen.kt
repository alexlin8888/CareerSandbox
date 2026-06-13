package com.careersandbox.app.ui.screens.competition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.TeamMate
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/* =====================================================================
   組隊媒合 —— 滑卡找隊友(市場標準:Tinder 式左右滑)
   頂部先講「這隊還缺什麼」(互補,不是找一樣的)→ 滑卡 → 互相想揪 → 配對
   ===================================================================== */

// 你隊伍已有的角色技能 vs 競賽需要的 → 缺口高亮
private val teamHas = listOf("資料分析", "簡報")
private val teamNeeds = listOf("後端", "設計")
// 哪些技能字樣算「補缺口」(高亮用)
private val gapKeywords = listOf("後端", "API", "設計", "UI", "Figma", "視覺")

private fun fillsGap(skill: String): Boolean =
    gapKeywords.any { skill.contains(it) }

@Composable
fun TeamMatchScreen(navController: NavHostController) {
    val pool = remember { MockData.recommendedTeammates }
    var index by remember { mutableIntStateOf(0) }
    var matched by remember { mutableStateOf<TeamMate?>(null) }
    var likedCount by remember { mutableIntStateOf(0) }
    val done = index >= pool.size

    Box(Modifier.fillMaxSize().background(PaperWarm)) {
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(16.dp))
            // 標頭
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(38.dp).clip(CircleShape).background(InkGray100)
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Close, contentDescription = null, tint = InkBlack, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(12.dp))
                Text("找隊友", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                if (likedCount > 0) {
                    Text("已想揪 $likedCount 人", color = BrandDeepOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(14.dp))

            // 這隊還缺什麼
            GapHeader()
            Spacer(Modifier.height(16.dp))

            // 卡片堆疊
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (done) {
                    EmptyState(likedCount) { navController.popBackStack() }
                } else {
                    // 後面一張(墊底,微縮)
                    if (index + 1 < pool.size) {
                        CandidateCard(
                            tm = pool[index + 1],
                            modifier = Modifier.graphicsLayer { scaleX = 0.94f; scaleY = 0.94f; alpha = 0.6f },
                        )
                    }
                    // 當前可滑卡
                    key(index) {
                        SwipeableCard(
                            tm = pool[index],
                            onPass = { index++ },
                            onLike = {
                                likedCount++
                                // mock:分數高者「互相想揪」→ 配對
                                if (pool[index].matchScore >= 85) matched = pool[index] else index++
                            },
                        )
                    }
                }
            }

            // 底部雙鈕
            if (!done) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    RoundAction(Icons.Outlined.Close, InkGray400) { if (index < pool.size) index++ }
                    RoundAction(Icons.Outlined.Favorite, BrandOrange) {
                        if (index < pool.size) {
                            likedCount++
                            if (pool[index].matchScore >= 85) matched = pool[index] else index++
                        }
                    }
                }
            }
        }

        // 配對成功
        matched?.let { tm ->
            MatchPopup(
                tm = tm,
                onChat = {
                    matched = null
                    navController.navigate(Routes.TEAM_CHAT)
                },
                onKeep = { matched = null; index++ },
            )
        }
    }
}

@Composable
private fun GapHeader() {
    Column(
        Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)).background(PaperWhite).padding(14.dp),
    ) {
        Text("這隊還缺", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            teamHas.forEach { s ->
                ChipTag(s, has = true)
                Spacer(Modifier.width(6.dp))
            }
            teamNeeds.forEach { s ->
                ChipTag(s, has = false)
                Spacer(Modifier.width(6.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("綠色是你隊上有的,橘色是還缺的。往右滑補缺口的人。",
            color = InkGray400, fontSize = 11.sp)
    }
}

@Composable
private fun ChipTag(label: String, has: Boolean) {
    Row(
        Modifier.clip(RoundedCornerShape(50))
            .background(if (has) AccentGreen.copy(alpha = 0.14f) else BrandPeach.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(if (has) "✓ $label" else "◌ $label",
            color = if (has) AccentGreen else BrandDeepOrange,
            fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SwipeableCard(tm: TeamMate, onPass: () -> Unit, onLike: () -> Unit) {
    val density = LocalDensity.current
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = with(density) { 110.dp.toPx() }

    CandidateCard(
        tm = tm,
        swipeFrac = (offsetX.value / threshold).coerceIn(-1f, 1f),
        modifier = Modifier
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = (offsetX.value / threshold) * 8f
            }
            .pointerInput(tm.id) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            when {
                                offsetX.value > threshold -> {
                                    offsetX.animateTo(threshold * 4, tween(260)); onLike()
                                }
                                offsetX.value < -threshold -> {
                                    offsetX.animateTo(-threshold * 4, tween(260)); onPass()
                                }
                                else -> offsetX.animateTo(0f, tween(220))
                            }
                        }
                    },
                ) { change, drag ->
                    change.consume()
                    scope.launch { offsetX.snapTo(offsetX.value + drag.x) }
                }
            },
    )
}

@Composable
private fun CandidateCard(
    tm: TeamMate,
    swipeFrac: Float = 0f,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().fillMaxHeight(0.92f)
            .clip(RoundedCornerShape(24.dp)).background(PaperWhite),
    ) {
        Column(Modifier.fillMaxSize().padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(BrandPeach.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) { Text(tm.name.first().toString(), color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 26.sp) }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(tm.name, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Text("${tm.dept} · ${tm.school}", color = InkGray500, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("技能", color = InkGray400, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(8.dp))
            // 技能 chips,補缺口的高亮
            FlowChips(tm.skills)
            Spacer(Modifier.height(20.dp))
            Text("為什麼適合你們", color = InkGray400, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Spacer(Modifier.height(6.dp))
            Text(tm.matchReason, color = InkGray700, fontSize = 14.sp, lineHeight = 21.sp)
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(InkBlack)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) { Text("適配 ${tm.matchScore}%", color = BrandAmber, fontWeight = FontWeight.Black, fontSize = 13.sp) }
            }
        }

        // 滑動方向標籤
        if (swipeFrac > 0.15f) {
            StampLabel("想揪", AccentGreen, Alignment.TopStart, swipeFrac)
        } else if (swipeFrac < -0.15f) {
            StampLabel("跳過", InkGray400, Alignment.TopEnd, -swipeFrac)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.StampLabel(
    text: String, color: androidx.compose.ui.graphics.Color, align: Alignment, strength: Float,
) {
    Box(
        Modifier.align(align).padding(20.dp)
            .alpha(strength.coerceIn(0f, 1f))
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) { Text(text, color = color, fontWeight = FontWeight.Black, fontSize = 20.sp) }
}

@Composable
private fun FlowChips(skills: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        skills.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { s ->
                    val gap = fillsGap(s)
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .background(if (gap) BrandOrange.copy(alpha = 0.16f) else InkGray100)
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                    ) {
                        Text(
                            if (gap) "★ $s" else s,
                            color = if (gap) BrandDeepOrange else InkGray700,
                            fontSize = 13.sp, fontWeight = if (gap) FontWeight.Black else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        Modifier.size(60.dp).clip(CircleShape).background(PaperWhite)
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp)) }
}

@Composable
private fun EmptyState(likedCount: Int, onBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("這場競賽的人看完了", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            if (likedCount > 0) "你想揪了 $likedCount 個人,對方回應後會通知你。"
            else "這次都跳過了。新的人加入競賽時會再出現。",
            color = InkGray500, fontSize = 13.sp, lineHeight = 20.sp,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.clip(RoundedCornerShape(14.dp)).background(InkBlack)
                .pressScale(onClick = onBack).padding(horizontal = 28.dp, vertical = 12.dp),
        ) { Text("回競賽頁", color = PaperWhite, fontWeight = FontWeight.Black) }
    }
}

@Composable
private fun MatchPopup(tm: TeamMate, onChat: () -> Unit, onKeep: () -> Unit) {
    AnimatedVisibility(visible = true, enter = fadeIn(tween(250)) + scaleIn(tween(300), initialScale = 0.85f), exit = fadeOut()) {
        Box(
            Modifier.fillMaxSize().background(InkBlack.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.padding(32.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)).background(PaperWhite).padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("配對成功", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(Modifier.height(6.dp))
                Text("你和 ${tm.name} 都想一起組隊", color = InkGray700, fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier.size(80.dp).clip(CircleShape).background(BrandPeach.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) { Text(tm.name.first().toString(), color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 34.sp) }
                Spacer(Modifier.height(20.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(BrandOrange)
                        .pressScale(onClick = onChat).padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("開始聊聊", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp) }
                Spacer(Modifier.height(8.dp))
                Text("繼續看其他人", color = InkGray500, fontSize = 13.sp,
                    modifier = Modifier.pressScale(onClick = onKeep).padding(8.dp))
            }
        }
    }
}
