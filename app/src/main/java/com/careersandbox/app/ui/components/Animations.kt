package com.careersandbox.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/** 按下時縮小 95% 帶回彈,適用所有可點擊元件 */
fun Modifier.pressScale(
    enabled: Boolean = true,
    scaleDown: Float = 0.96f,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val targetScale = if (pressed && enabled) scaleDown else 1f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow),
        label = "pressScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick,
        )
}

/** 列表項依序淡入 + 上滑 */
@Composable
fun StaggeredAppear(
    visible: Boolean = true,
    delayMillis: Int = 0,
    durationMillis: Int = 320,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var hasAppeared by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(delayMillis.toLong())
            hasAppeared = true
        }
    }
    AnimatedVisibility(
        visible = hasAppeared,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(durationMillis)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(durationMillis)
            ),
    ) { content() }
}

/** 數字從 0 跑到 target 的 count-up,進場時自動觸發。用法:Text("${rememberCountUp(64)}") */
@Composable
fun rememberCountUp(target: Int, durationMillis: Int = 900, delayMillis: Int = 0): Int {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(target) {
        if (delayMillis > 0) kotlinx.coroutines.delay(delayMillis.toLong())
        started = true
    }
    val value by animateIntAsState(
        targetValue = if (started) target else 0,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "countUp",
    )
    return value
}
