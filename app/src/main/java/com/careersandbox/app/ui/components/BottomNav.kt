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

/** 統一跳頁邏輯,給所有 tab 用 */
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
        // 膠囊主體
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                sideTabs.take(2).forEach { tab ->
                    PillTab(
                        tab = tab,
                        selected = currentRoute == tab.route,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToTab(navController, tab.route, currentRoute) }
                    )
                }
                Spacer(Modifier.width(64.dp))
                sideTabs.drop(2).forEach { tab ->
                    PillTab(
                        tab = tab,
                        selected = currentRoute == tab.route,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToTab(navController, tab.route, currentRoute) }
                    )
                }
            }
        }
        // 中央 FAB 麥克風(去 InterviewHub)
        Box(
            modifier = Modifier
                .size(64.dp)
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
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PillTab(
    tab: TabItem,
    selected: Boolean,
    modifier: Modifier,
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
        modifier = modifier
            .height(44.dp)
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
