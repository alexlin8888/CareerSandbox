package com.careersandbox.app.ui.screens.competition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.careersandbox.app.data.model.CompetitionTeam
import com.careersandbox.app.data.model.TeamMate
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*

@Composable
fun CompetitionDetailScreen(navController: NavHostController, compId: String) {
    val comp = remember(compId) { MockData.competitionById(compId) }
    if (comp == null) {
        Box(Modifier.fillMaxSize().background(PaperWarm), contentAlignment = Alignment.Center) {
            Text("找不到這個競賽", color = InkGray500)
        }
        return
    }
    val accent = accentFor(comp.coverColor)
    val teammates = MockData.recommendedTeammates
    val teams = MockData.existingTeams

    // 加入隊伍狀態
    var joinedTeamId by remember { mutableStateOf<String?>(null) }
    var invitedIds = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize().background(PaperWarm)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // === Hero:封面圖 ===
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                if (comp.coverImageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = comp.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(accent.copy(alpha = 0.2f)))
                }
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.55f)),
                        ),
                    ),
                )
                Box(
                    Modifier.padding(16.dp).size(40.dp).clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Box(
                        modifier = Modifier.clip(CircleShape).background(PaperWhite)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(comp.category.label, color = accent, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(comp.title, color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 22.sp, lineHeight = 26.sp)
                    Spacer(Modifier.height(3.dp))
                    Text(comp.organizer, color = PaperWhite.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 20.dp, bottom = 40.dp)) {
                // === 關鍵資訊 3 格 ===
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoCell("截止", comp.deadline, Icons.Outlined.Schedule, Modifier.weight(1f))
                    InfoCell("隊伍", comp.teamSize, Icons.Outlined.Group, Modifier.weight(1f))
                    InfoCell("獎金", comp.prize, Icons.Outlined.EmojiEvents, Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                // tags
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    comp.tags.forEach { tag ->
                        Box(
                            modifier = Modifier.clip(CircleShape).background(PaperWhite)
                                .border(1.dp, InkGray200, CircleShape).padding(horizontal = 11.dp, vertical = 5.dp),
                        ) {
                            Text(tag, color = InkGray600, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                // === 為什麼推薦你 ===
                WhyRecommendCard(accent)
                Spacer(Modifier.height(28.dp))

                // === 推薦隊友(互補媒合)===
                Text("推薦隊友", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.height(2.dp))
                Text("依你的職能輪廓,推薦互補的夥伴", color = InkGray500, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    teammates.forEach { tm ->
                        TeammateCard(
                            tm = tm,
                            invited = tm.id in invitedIds,
                            onInvite = {
                                if (tm.id in invitedIds) invitedIds.remove(tm.id) else invitedIds.add(tm.id)
                            },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))

                // === 現有可加入隊伍 ===
                Text("找隊伍加入", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.height(2.dp))
                Text("這些隊伍正在找你這種背景", color = InkGray500, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    teams.forEach { team ->
                        TeamCard(
                            team = team,
                            joined = joinedTeamId == team.id,
                            onToggle = {
                                joinedTeamId = if (joinedTeamId == team.id) null else team.id
                            },
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))

                // === 底部主 CTA:建立隊伍 ===
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
                        .background(InkBlack)
                        .pressScale {
                            if (invitedIds.isEmpty()) invitedIds.add(MockData.recommendedTeammates.first().id)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.GroupAdd, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (invitedIds.isEmpty()) "建立我的隊伍" else "建立隊伍(已邀 ${invitedIds.size} 人)",
                            color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCell(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(PaperWhite).padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = InkGray400, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text(value, color = InkBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 15.sp)
    }
}

/** 為什麼推薦你 — 用 MockData.currentUser 的技能對齊(假邏輯,展示用)*/
@Composable
private fun WhyRecommendCard(accent: Color) {
    val matched = MockData.currentUser.skillsHave.take(3)
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(BrandPeach.copy(alpha = 0.5f), BrandPeach.copy(alpha = 0.2f))))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("為什麼推薦你", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "你的資料分析與使用者研究背景,正好對上這個競賽的核心需求。把這些技能放進隊伍,能負責洞察與決策的部分。",
            color = InkCharcoal, fontSize = 13.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            matched.forEach { skill ->
                Box(
                    modifier = Modifier.clip(CircleShape).background(PaperWhite).padding(horizontal = 11.dp, vertical = 5.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(skill, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TeammateCard(tm: TeamMate, invited: Boolean, onInvite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PaperWhite).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 頭像(首字)
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(BrandAmber, BrandDeepOrange))),
            contentAlignment = Alignment.Center,
        ) {
            Text(tm.name.take(1), color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tm.name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text("${tm.school} · ${tm.dept}", color = InkGray500, fontSize = 11.sp)
            }
            Spacer(Modifier.height(3.dp))
            Text(tm.matchReason, color = InkGray600, fontSize = 11.sp, lineHeight = 15.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                tm.skills.take(3).forEach { s ->
                    Box(
                        modifier = Modifier.clip(CircleShape).background(BrandPeach.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(s, color = BrandDeepOrange, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${tm.matchScore}%", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier.clip(CircleShape)
                    .background(if (invited) AccentGreen else InkBlack)
                    .pressScale(onClick = onInvite)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(if (invited) "已邀" else "邀請", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TeamCard(team: CompetitionTeam, joined: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PaperWhite).padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(InkGray100),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = null, tint = InkBlack, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.name, color = InkBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(2.dp))
                Text("${team.leaderName} 隊長 · ${team.currentSize}/${team.targetSize} 人",
                    color = InkGray500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier.clip(CircleShape)
                    .background(if (joined) AccentGreen else BrandPeach)
                    .pressScale(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(if (joined) "已申請" else "申請加入",
                    color = if (joined) PaperWhite else BrandDeepOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(team.note, color = InkGray600, fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("還缺:", color = InkGray400, fontSize = 11.sp)
            Spacer(Modifier.width(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                team.lookingFor.forEach { role ->
                    Box(
                        modifier = Modifier.clip(CircleShape).background(AccentGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(role, color = Color(0xFF0A7A52), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
