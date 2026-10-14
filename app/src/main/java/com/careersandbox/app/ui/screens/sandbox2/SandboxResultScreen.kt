package com.careersandbox.app.ui.screens.sandbox2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.BrandAmber
import com.careersandbox.app.ui.theme.BrandDeepOrange
import com.careersandbox.app.ui.theme.BrandOrange
import com.careersandbox.app.ui.theme.InkGray500
import com.careersandbox.app.ui.theme.InkSlate
import com.careersandbox.app.ui.theme.PaperWarm
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import com.careersandbox.app.ui.screens.workplace.SoundManager
private val LineColor = Color(0xFFEADFD6)
private val TrackColor = Color(0xFFF3EBE4)

@Composable
fun SandboxResultScreen(result: SandboxResult, onGapClick: () -> Unit, onFinish: () -> Unit) {
    // The result screen is silent, and back returns to the select screen
    LaunchedEffect(Unit) { SoundManager.stopBgm() }
    BackHandler { onFinish() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperWarm)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(22.dp))
        Text("這一天的紀錄", color = InkSlate, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(
            "${result.occupation}　·　${result.company}　·　只描述你做了什麼，不評分",
            color = InkGray500, fontSize = 12.sp,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "這一天做完，我們從三個角度整理你的紀錄。以下只描述你做了什麼，對照的是職缺實際的要求。",
            color = InkSlate, fontSize = 13.sp, lineHeight = 21.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        // 1. Entry feasibility
        val have = result.skills.count { it.status == SkillStatus.HAVE }
        val partial = result.skills.count { it.status == SkillStatus.PARTIAL }
        val missing = result.skills.count { it.status == SkillStatus.MISSING }
        Block(
            title = "入門可行性",
            desc = "你現有的能力，對上這份工作入門要求的程度。${result.skills.size} 項核心技能中，" +
                    "$have 項已具備、$partial 項部分、$missing 項尚缺。",
        ) {
            result.skills.forEach { SkillLine(it) }
            Note(result.skillNote)
        }

        // 2a. Potential challenge points: timeline
        Block(
            title = "潛在挑戰點｜時間點",
            desc = "今天有 ${result.challenges.size} 個時間點你花比較多力氣或卡住，這類情況在實際工作中也可能遇到。",
        ) {
            result.challenges.forEach { ChallengeLine(it) }
            Note(result.challengeNote)
        }

        // 2b. Potential challenge points: working style
        Block(
            title = "潛在挑戰點｜你的做事方式",
            desc = "這些是上面時間點的背景：你在今天通常怎麼反應。",
        ) {
            result.styleStats.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowItems.forEach { StatBox(it, Modifier.weight(1f)) }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PaperWarm)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
            ) {
                Text(result.friendQuote, color = InkSlate, fontSize = 13.sp, lineHeight = 20.sp)
                Text(result.friendNote, color = InkGray500, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }

        // 3. Skill transferability
        Block(
            title = "技能轉移性",
            desc = "你現有的技能，還能用在哪些相近職務。數字是與${result.occupation}重疊的核心技能項數。",
        ) {
            result.transfers.forEach { TransferLine(it) }
            Note(result.transferNote.substringAfter("，", result.transferNote))
        }

        // Gap (set difference) with entry to the learning path
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3EA))
                .border(1.5.dp, BrandOrange, RoundedCornerShape(12.dp))
                .padding(12.dp),
        ) {
            Text("你目前還缺", color = InkSlate, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(result.gaps, color = InkSlate, fontSize = 13.sp, lineHeight = 21.sp, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandOrange)
                    .pressScale { onGapClick() },
                contentAlignment = Alignment.Center,
            ) {
                Text("看補強路徑", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.5.dp, LineColor, RoundedCornerShape(12.dp))
                .pressScale { onFinish() },
            contentAlignment = Alignment.Center,
        ) {
            Text("結束這一天", color = InkSlate, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Block(title: String, desc: String, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.5.dp, LineColor, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Column(Modifier.width(IntrinsicSize.Max)) {
            Text(title, color = InkSlate, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(
                Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BrandAmber),
            )
        }
        Text(desc, color = InkGray500, fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(top = 8.dp, bottom = 10.dp))
        content()
    }
}

@Composable
private fun Note(text: String) {
    Text(text, color = InkGray500, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun Bar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    Box(modifier.height(9.dp).clip(RoundedCornerShape(5.dp)).background(TrackColor)) {
        if (fraction > 0f) {
            Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().background(color))
        }
    }
}

@Composable
private fun SkillLine(row: SkillRow) {
    val (fraction, color, label) = when (row.status) {
        SkillStatus.HAVE -> Triple(1f, BrandOrange, "已具備")
        SkillStatus.PARTIAL -> Triple(0.55f, BrandAmber, "部分")
        SkillStatus.MISSING -> Triple(0f, BrandOrange, "尚缺")
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(row.name, color = InkSlate, fontSize = 13.sp, modifier = Modifier.width(80.dp))
        Bar(fraction, color, Modifier.weight(1f))
        Text(
            label,
            color = if (row.status == SkillStatus.MISSING) BrandDeepOrange else InkGray500,
            fontSize = 11.sp,
            fontWeight = if (row.status == SkillStatus.MISSING) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End,
            modifier = Modifier.width(52.dp),
        )
    }
}

@Composable
private fun ChallengeLine(c: Challenge) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(bottom = 10.dp)) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(BrandAmber))
        Spacer(Modifier.width(10.dp))
        Column {
            Text("${c.time}　${c.stage}", color = BrandDeepOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(c.text, color = InkSlate, fontSize = 13.sp, lineHeight = 21.sp, modifier = Modifier.padding(vertical = 2.dp))
            Text(c.basis, color = InkGray500, fontSize = 11.sp)
        }
    }
}

@Composable
private fun StatBox(stat: StyleStat, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PaperWarm)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Text(stat.label, color = InkGray500, fontSize = 11.sp)
        Text(stat.value, color = InkSlate, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun TransferLine(t: TransferRow) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(t.job, color = InkSlate, fontSize = 13.sp, modifier = Modifier.width(92.dp))
        Bar(t.overlap.toFloat() / t.total, BrandOrange, Modifier.weight(1f))
        Text("${t.overlap}/${t.total}", color = InkGray500, fontSize = 11.sp, textAlign = TextAlign.End, modifier = Modifier.width(40.dp))
    }
}