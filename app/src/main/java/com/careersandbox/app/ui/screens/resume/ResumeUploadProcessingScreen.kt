package com.careersandbox.app.ui.screens.resume

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ResumeUploadProcessingScreen(navController: NavHostController) {
    val steps = listOf(
        "讀取檔案" to 600L,
        "分析履歷結構" to 900L,
        "萃取個人經歷" to 700L,
        "結構化為個人檔案" to 800L,
    )
    var currentStep by remember { mutableIntStateOf(-1) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        steps.forEachIndexed { idx, (_, duration) ->
            currentStep = idx
            delay(duration)
        }
        done = true
        delay(700)
        // 結束後自動跳到 profile
        navController.navigate(Routes.RESUME_PROFILE) {
            popUpTo(Routes.RESUME_HUB) { inclusive = false }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PaperWhite),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))

            // 上方插畫
            Image(
                painter = painterResource(R.drawable.undraw_feedback_ebmx),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp),
            )

            Spacer(Modifier.height(40.dp))

            Text(
                if (done) "完成!" else "AI 解析中",
                color = InkBlack,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (done) "正在打開你的個人檔案" else "稍等 3 秒,讓 AI 把你的履歷拆解結構",
                color = InkGray500,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )

            Spacer(Modifier.height(48.dp))

            // 進度步驟
            Column(modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { idx, (label, _) ->
                    val isCurrent = idx == currentStep && !done
                    val isComplete = idx < currentStep || done

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isComplete -> BrandOrange
                                        isCurrent -> BrandPeach
                                        else -> InkGray100
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            when {
                                isComplete -> Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = PaperWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                isCurrent -> Box(
                                    Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(BrandDeepOrange)
                                )
                                else -> Text(
                                    "${idx + 1}",
                                    color = InkGray400,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            label,
                            color = if (isComplete || isCurrent) InkBlack else InkGray400,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
