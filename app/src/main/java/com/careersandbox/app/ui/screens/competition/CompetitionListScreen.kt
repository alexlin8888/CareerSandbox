package com.careersandbox.app.ui.screens.competition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.careersandbox.app.R
import com.careersandbox.app.data.model.Competition
import com.careersandbox.app.data.model.CompetitionCategory
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.ScatteredDecorations
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.WaveHeroBackground
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

internal fun accentFor(coverColor: String): Color = when (coverColor) {
    "orange" -> BrandOrange
    "green" -> AccentGreen
    "purple" -> GlowPurple
    "pink" -> Color(0xFFE26B8C)
    "teal" -> Color(0xFF1D9E75)
    else -> BrandOrange
}

@Composable
fun CompetitionListScreen(navController: NavHostController) {
    val categories = listOf("全部") + CompetitionCategory.values().map { it.label }
    var activeCat by remember { mutableStateOf("全部") }
    val chipScroll = rememberScrollState()

    val visible = MockData.competitions.filter {
        activeCat == "全部" || it.category.label == activeCat
    }

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Hero
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                WaveHeroBackground(
                    gradient = Brush.linearGradient(listOf(AccentGreen, Color(0xFF1D9E75), Color(0xFF0E7C5A))),
                    heightDp = 200,
                )
                ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.5f))
                Box(
                    Modifier.padding(16.dp).size(40.dp).clip(CircleShape)
                        .background(PaperWhite.copy(alpha = 0.2f))
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 60.dp)) {
                    Text("COMPETITIONS", color = Color(0xFF0A5A40),
                        fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("競賽組隊媒合", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 26.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("找對的競賽,配互補的夥伴", color = PaperWhite.copy(alpha = 0.95f), fontSize = 12.sp, maxLines = 1)
                }
                Image(
                    painter = painterResource(R.drawable.beaver_trophy),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp)
                        .size(118.dp)
                        .alpha(0.96f),
                    contentScale = ContentScale.Fit,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 18.dp, bottom = 40.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(chipScroll),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    categories.forEach { c ->
                        CatPill(c, active = activeCat == c, onClick = { activeCat = c })
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (visible.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.beaver_sleep),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("這個分類目前沒有競賽", color = InkGray500,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("換個分類看看,或晚點再回來", color = InkGray400, fontSize = 12.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        visible.forEachIndexed { i, comp ->
                            StaggeredAppear(delayMillis = i * 80) {
                                CompetitionRow(comp, onClick = {
                                    navController.navigate(Routes.competitionDetail(comp.id))
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatPill(text: String, active: Boolean, onClick: () -> Unit) {
    val base = if (active) Modifier.clip(CircleShape).background(InkBlack)
    else Modifier.clip(CircleShape).background(PaperWhite).border(1.dp, InkGray200, CircleShape)
    Box(modifier = base.pressScale(onClick = onClick).padding(horizontal = 13.dp, vertical = 6.dp)) {
        Text(text, color = if (active) PaperWhite else InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
private fun CompetitionRow(comp: Competition, onClick: () -> Unit) {
    val accent = accentFor(comp.coverColor)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PaperWhite)
            .pressScale(onClick = onClick),
    ) {
        // 左:封面圖
        Box(modifier = Modifier.width(96.dp).height(112.dp).background(accent.copy(alpha = 0.14f))) {
            if (comp.coverImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = comp.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        // 右:資訊
        Column(modifier = Modifier.weight(1f).padding(14.dp)) {
            Box(
                modifier = Modifier.clip(CircleShape).background(accent.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(comp.category.label, color = accent, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(comp.title, color = InkBlack, fontWeight = FontWeight.Black, fontSize = 14.sp, lineHeight = 18.sp, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(comp.organizer, color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Timer, contentDescription = null, tint = InkGray400, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text(comp.deadline, color = InkGray500, fontSize = 10.sp)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Outlined.Groups, contentDescription = null, tint = InkGray400, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(3.dp))
                Text(comp.teamSize, color = InkGray500, fontSize = 10.sp)
            }
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = InkGray400,
            modifier = Modifier.align(Alignment.CenterVertically).padding(end = 10.dp).size(18.dp))
    }
}
