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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val sideTabs = listOf(
    TabItem(Routes.HOME, "首頁", Icons.Outlined.Home),
    TabItem(Routes.RESUME_HUB, "履歷", Icons.Outlined.Description),
    TabItem(Routes.EXPLORE_HUB, "探索", Icons.Outlined.Explore),
    TabItem(Routes.PROFILE, "我的", Icons.Outlined.Person),
)

private fun navigateToTab(navController: NavHostController, route: String, currentRoute: String?) {
    if (currentRoute == route) return
    navController.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
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
        // 1. 膠囊主體 + 4 個 tab(中間有大空隙給 FAB)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
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
                // 左 2 個 tab,佔 40% 寬
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
                // 中間 20% 的空白給 FAB(FAB 64dp + 兩側 padding)
                Spacer(modifier = Modifier.weight(0.2f))
                // 右 2 個 tab,佔 40% 寬
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
        // 2. 中央 FAB(在最上層 Z-order)
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    spotColor = BrandOrange.copy(alpha = 0.7f)
                )
                .clip(CircleShape)
                .background(HeroGradient)
                .pressScale {
                    navigateToTab(navController, Routes.INTERVIEW_HUB, currentRoute)
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Mic,
                contentDescription = "面試",
                tint = PaperWhite,
                modifier = Modifier.size(26.dp)
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

    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 44.dp)
            .clip(CircleShape)
            .background(bgColor)
            .pressScale(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            tab.icon,
            contentDescription = tab.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun shouldShowBottomNav(currentRoute: String?): Boolean {
    return currentRoute in listOf(
        Routes.HOME, Routes.RESUME_HUB, Routes.INTERVIEW_HUB,
        Routes.EXPLORE_HUB, Routes.PROFILE
    )
}
