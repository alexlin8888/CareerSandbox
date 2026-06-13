package com.careersandbox.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val sideTabs = listOf(
    TabItem(Routes.HOME, "首頁", Icons.Outlined.Home),
    TabItem(Routes.RESUME_HUB, "履歷", Icons.Outlined.Description),
    TabItem(Routes.WORKPLACE_SANDBOX, "職場沙盒", Icons.Outlined.Apartment),
    TabItem(Routes.PROFILE, "我的", Icons.Outlined.Person),
)

/**
 * Tab 切換邏輯:
 * - 點目標 tab,popUpTo HOME(包含 inclusive=true 把 HOME 也清掉)再 navigate
 * - 結果:整個 nav stack 重置成 [TARGET],每次切換都乾淨
 * - 不用 saveState/restoreState,避免狀態紊亂
 */
private fun navigateToTab(navController: NavHostController, route: String, currentRoute: String?) {
    if (currentRoute == route) return
    navController.navigate(route) {
        // 清掉整個 hub stack(包含 HOME),重新建立
        popUpTo(Routes.HOME) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = InkBlack.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(50))
                .background(InkCharcoal),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(0.4f).padding(start = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    sideTabs.take(2).forEach { tab ->
                        PillTab(
                            tab = tab,
                            selected = currentRoute == tab.route,
                            onClick = { navigateToTab(navController, tab.route, currentRoute) }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(0.2f))
                Row(
                    modifier = Modifier.weight(0.4f).padding(end = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    sideTabs.drop(2).forEach { tab ->
                        PillTab(
                            tab = tab,
                            selected = currentRoute == tab.route,
                            onClick = { navigateToTab(navController, tab.route, currentRoute) }
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .size(width = 60.dp, height = 68.dp)
                .onGloballyPositioned {
                    TourAnchors.bounds[Routes.INTERVIEW_HUB] = it.boundsInRoot()
                }
                .pressScale {
                    navigateToTab(navController, Routes.INTERVIEW_HUB, currentRoute)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = BrandOrange.copy(alpha = 0.7f)
                    )
                    .clip(CircleShape)
                    .background(HeroGradient),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = "面試",
                    tint = PaperWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "面試",
                color = PaperWhite,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                fontSize = 9.sp,
            )
        }
    }
}

@Composable
private fun PillTab(
    tab: TabItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val anchorMod = Modifier.onGloballyPositioned {
        TourAnchors.bounds[tab.route] = it.boundsInRoot()
    }
    val iconColor by animateColorAsState(
        targetValue = if (selected) InkCharcoal else InkGray400,
        animationSpec = tween(240),
        label = "iconColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) BrandYellow else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = tween(240),
        label = "bgColor"
    )

    Column(
        modifier = anchorMod
            .width(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .pressScale(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            tab.icon,
            contentDescription = tab.label,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = tab.label,
            color = iconColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            fontSize = 9.sp,
        )
    }
}

@Composable
fun shouldShowBottomNav(currentRoute: String?): Boolean {
    return currentRoute in listOf(
        Routes.HOME, Routes.RESUME_HUB, Routes.INTERVIEW_HUB,
        Routes.WORKPLACE_SANDBOX, Routes.PROFILE
    )
}
