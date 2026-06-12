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
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.StaggeredAppear
import com.careersandbox.app.ui.components.StickyNote
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.components.rememberCountUp
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
    val ctx = LocalContext.current
    var joinedTeamId by remember { mutableStateOf<String?>(null) }
    var invitedIds = remember { mutableStateListOf<String>() }
    var appliedTeamIds = remember { mutableStateListOf<String>() }
    // 邀請對話框:存當前要邀請的隊友(null = 不顯示)
    var inviteTarget by remember { mutableStateOf<TeamMate?>(null) }
    // 申請對話框:存當前要申請的隊伍
    var applyTarget by remember { mutableStateOf<CompetitionTeam?>(null) }

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
                    InfoCell("截止", comp.deadline, Icons.Outlined.Timer, Modifier.weight(1f))
                    InfoCell("隊伍", comp.teamSize, Icons.Outlined.Groups, Modifier.weight(1f))
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
                            Text(tag, color = InkGray700, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
                    teammates.forEachIndexed { idx, tm ->
                        StaggeredAppear(delayMillis = idx * 80) {
                            if (idx == 0) {
                                Box {
                                    TeammateCard(
                                        tm = tm,
                                        invited = tm.id in invitedIds,
                                        onInvite = {
                                            if (tm.id in invitedIds) invitedIds.remove(tm.id) else inviteTarget = tm
                                        },
                                    )
                                    StickyNote(
                                        text = "你缺的他剛好有",
                                        rotation = -4f,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .offset(x = 24.dp, y = (-12).dp),
                                    )
                                }
                            } else {
                                TeammateCard(
                                    tm = tm,
                                    invited = tm.id in invitedIds,
                                    onInvite = {
                                        if (tm.id in invitedIds) invitedIds.remove(tm.id) else inviteTarget = tm
                                    },
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                // === 現有可加入隊伍 ===
                Text("找隊伍加入", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.height(2.dp))
                Text("這些隊伍正在找你這種背景", color = InkGray500, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    teams.forEachIndexed { idx, team ->
                        StaggeredAppear(delayMillis = idx * 80) {
                            TeamCard(
                                team = team,
                                joined = team.id in appliedTeamIds,
                                onToggle = {
                                    if (team.id in appliedTeamIds) appliedTeamIds.remove(team.id) else applyTarget = team
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))

                // === 底部主 CTA:建立隊伍(有確認回饋)===
                var teamCreated by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp))
                        .background(if (teamCreated) AccentGreen else InkBlack)
                        .pressScale {
                            teamCreated = true
                            val n = invitedIds.size
                            Toast.makeText(
                                ctx,
                                if (n > 0) "隊伍已建立,已邀請 $n 位夥伴" else "隊伍已建立,快去邀請夥伴",
                                Toast.LENGTH_SHORT,
                            ).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (teamCreated) Icons.Outlined.Check else Icons.Outlined.Groups,
                            contentDescription = null, tint = PaperWhite, modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when {
                                teamCreated -> "隊伍已建立"
                                invitedIds.isEmpty() -> "建立我的隊伍"
                                else -> "建立隊伍(已邀 ${invitedIds.size} 人)"
                            },
                            color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp,
                        )
                    }
                }

                // 建立後:展開「我的隊伍」狀態卡(真實產出,不只 Toast)
                if (teamCreated) {
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(AccentGreen.copy(alpha = 0.1f)).padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Groups, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("我的隊伍", color = InkBlack, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(comp.title, color = InkGray500, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        // 隊長(自己)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(BrandOrange),
                                contentAlignment = Alignment.Center,
                            ) { Text("我", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("你(隊長)", color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("已就位", color = AccentGreen, fontSize = 11.sp)
                            }
                        }
                        // 已邀請的成員
                        invitedIds.forEach { id ->
                            val tm = MockData.recommendedTeammates.find { it.id == id }
                            if (tm != null) {
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(InkGray100),
                                        contentAlignment = Alignment.Center,
                                    ) { Text(tm.name.first().toString(), color = InkGray500, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                                    Spacer(Modifier.width(10.dp))
                                    Column {
                                        Text(tm.name, color = InkBlack, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("邀請已送出 · 等待回覆", color = BrandDeepOrange, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        if (invitedIds.isEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("還沒邀請任何人,往上滑邀請推薦隊友", color = InkGray400, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(14.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(BrandOrange).pressScale { navController.navigate(Routes.TEAM_CHAT) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("進入隊伍聊天室", color = PaperWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // === 邀請隊友對話框 ===
        inviteTarget?.let { target ->
            var msg by remember(target.id) { mutableStateOf("嗨 ${target.name},看到你的背景跟我們競賽很搭,想邀你一起組隊。") }
            AlertDialog(
                onDismissRequest = { inviteTarget = null },
                title = { Text("邀請 ${target.name}", fontWeight = FontWeight.Black) },
                text = {
                    Column {
                        Text("送出一段邀請訊息給對方", color = InkGray500, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = msg, onValueChange = { msg = it },
                            modifier = Modifier.fillMaxWidth(), minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                            .pressScale {
                                invitedIds.add(target.id)
                                MockData.addNotification("邀請已送出", "已邀請 ${target.name} 加入隊伍,等待對方回覆")
                                Toast.makeText(ctx, "已送出邀請給 ${target.name}", Toast.LENGTH_SHORT).show()
                                inviteTarget = null
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                    ) { Text("送出邀請", color = PaperWhite, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    Text("取消", color = InkGray500, modifier = Modifier.pressScale { inviteTarget = null }.padding(12.dp))
                },
            )
        }

        // === 申請加入隊伍對話框 ===
        applyTarget?.let { target ->
            var msg by remember(target.id) { mutableStateOf("你好,我想加入「${target.name}」,我的背景應該能補上你們需要的部分。") }
            AlertDialog(
                onDismissRequest = { applyTarget = null },
                title = { Text("申請加入 ${target.name}", fontWeight = FontWeight.Black) },
                text = {
                    Column {
                        Text("隊長 ${target.leaderName} 會收到你的申請", color = InkGray500, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = msg, onValueChange = { msg = it },
                            modifier = Modifier.fillMaxWidth(), minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                },
                confirmButton = {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(BrandOrange)
                            .pressScale {
                                appliedTeamIds.add(target.id)
                                MockData.addNotification("申請已送出", "已申請加入「${target.name}」,等待隊長 ${target.leaderName} 回覆")
                                Toast.makeText(ctx, "已送出申請", Toast.LENGTH_SHORT).show()
                                applyTarget = null
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                    ) { Text("送出申請", color = PaperWhite, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    Text("取消", color = InkGray500, modifier = Modifier.pressScale { applyTarget = null }.padding(12.dp))
                },
            )
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
            "你的資料分析和使用者研究背景,剛好是這個競賽要的。進了隊,可以扛數據和定方向的活。",
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
            Text(tm.matchReason, color = InkGray700, fontSize = 11.sp, lineHeight = 15.sp)
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
            Text("${rememberCountUp(tm.matchScore)}%", color = BrandDeepOrange, fontWeight = FontWeight.Black, fontSize = 16.sp)
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
        Text(team.note, color = InkGray700, fontSize = 12.sp, lineHeight = 17.sp)
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
