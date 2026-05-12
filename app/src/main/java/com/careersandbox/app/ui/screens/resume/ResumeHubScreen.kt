package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.MockData
import com.careersandbox.app.data.model.Resume
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.*
import com.careersandbox.app.ui.theme.*

@Composable
fun ResumeHubScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(PaperWhite)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            HeroSection()
            Spacer(Modifier.height(20.dp))
            QuickActions(navController)
            Spacer(Modifier.height(32.dp))
            ResumeList(navController)
            Spacer(Modifier.height(32.dp))
            ExperienceShortcut(navController)
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun HeroSection() {
    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        WaveHeroBackground(
            gradient = Brush.linearGradient(
                colors = listOf(BrandDeepOrange, BrandOrange, BrandAmber),
            ),
            heightDp = 240,
        )
        ScatteredDecorations(modifier = Modifier.fillMaxSize().alpha(0.6f))

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(0.6f),
        ) {
            Text("RESUME",
                color = PaperWhite.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("你的故事\n")
                    withStyle(SpanStyle(color = BrandYellow)) { append("值得") }
                    append("\n被看見。")
                },
                color = PaperWhite,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                lineHeight = 38.sp,
            )
        }

        // 插畫破框(右下)
        Image(
            painter = painterResource(R.drawable.undraw_online_resume_z4sp),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 16.dp)
                .size(170.dp)
                .alpha(0.95f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun QuickActions(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 主按鈕(實心橘色)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp),
                    spotColor = BrandOrange.copy(alpha = 0.5f))
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.linearGradient(
                    listOf(BrandDeepOrange, BrandOrange, BrandAmber))
                )
                .pressScale { navController.navigate(Routes.RESUME_EDITOR) },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Add, contentDescription = null,
                    tint = PaperWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(6.dp))
                Text("新增履歷",
                    color = PaperWhite,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium)
            }
        }
        // 次要按鈕
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BrandOrange.copy(alpha = 0.1f))
                .pressScale { navController.navigate(Routes.JD_CUSTOMIZE) },
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Tune, contentDescription = null,
                    tint = BrandDeepOrange, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("JD 客製化",
                    color = BrandDeepOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun ResumeList(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = buildAnnotatedString {
                    append("我的")
                    withStyle(SpanStyle(color = BrandOrange)) { append("履歷") }
                },
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                modifier = Modifier.weight(1f),
            )
            Text("${MockData.resumes.size} 份",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(16.dp))
        MockData.resumes.forEachIndexed { idx, r ->
            ResumeRow(
                index = idx,
                resume = r,
                onClick = { navController.navigate(Routes.RESUME_EDITOR) },
            )
            if (idx < MockData.resumes.size - 1) {
                SectionDivider(modifier = Modifier.padding(vertical = 14.dp))
            }
        }
    }
}

@Composable
private fun ResumeRow(index: Int, resume: Resume, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(String.format("%02d", index + 1),
            color = BrandOrange.copy(alpha = 0.4f),
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            modifier = Modifier.width(48.dp))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(resume.title,
                color = InkBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(resume.targetJob,
                    color = InkGray500,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(8.dp))
                Text("·",
                    color = InkGray400,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(8.dp))
                Text("已完成 ${resume.completion}%",
                    color = if (resume.completion >= 80) AccentGreen else BrandOrange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold)
            }
        }
        Box(
            Modifier.size(36.dp).clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                tint = BrandOrange, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ExperienceShortcut(navController: NavHostController) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = buildAnnotatedString {
                append("先整理你的")
                withStyle(SpanStyle(color = BrandOrange)) { append("經驗") }
            },
            color = InkBlack,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text("有了經驗,寫履歷會快很多",
            color = InkGray500,
            style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BrandPeach.copy(alpha = 0.4f))
                .pressScale { navController.navigate(Routes.EXPERIENCE_LIST) }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${MockData.experiences.size}",
                        color = BrandDeepOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp)
                    Spacer(Modifier.width(6.dp))
                    Text("筆經驗",
                        color = InkBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text("社團、實習、競賽、課程",
                    color = InkGray500,
                    style = MaterialTheme.typography.bodySmall)
            }
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(BrandDeepOrange),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                    tint = PaperWhite)
            }
        }
    }
}
