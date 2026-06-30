package com.careersandbox.app.ui.screens.interview

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.careersandbox.app.R
import com.careersandbox.app.data.mock.FaceMetrics
import com.careersandbox.app.data.mock.InterviewConfig
import com.careersandbox.app.data.mock.MockFaceMetricsProvider
import com.careersandbox.app.data.mock.PaceState
import com.careersandbox.app.data.mock.videoInterviewQuestions
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.delay

/* =====================================================================
   影像面試 —— 前端完整外殼
   上半:河狸面試官(立繪 + 說話動畫 + 字幕)
   下半:使用者鏡頭(CameraX 真預覽,權限沒給則優雅降級)+ 即時指標
   指標數據:MediaPipe 接點(目前 mock),全程標「練習參考,非評分」
   ===================================================================== */

@Composable
fun VideoInterviewScreen(navController: NavHostController) {
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamPermission = granted }

    // 進畫面就請求權限(若還沒給),並標記這是影像面試(報告據此顯示影像維度)
    LaunchedEffect(Unit) {
        InterviewConfig.lastWasVideo = true
        if (!hasCamPermission) permLauncher.launch(Manifest.permission.CAMERA)
    }

    var qIndex by remember { mutableIntStateOf(0) }
    var paused by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var interviewerSpeaking by remember { mutableStateOf(true) }
    val question = videoInterviewQuestions[qIndex]

    // 錄製計時
    LaunchedEffect(paused) {
        while (!paused) { delay(1000); seconds++ }
    }
    // 面試官「說話」3 秒後停(模擬問完題)
    LaunchedEffect(qIndex) {
        interviewerSpeaking = true
        delay(3200)
        interviewerSpeaking = false
    }

    // MediaPipe 接點:mock 指標,每 600ms 更新一次模擬即時分析
    val provider = remember { MockFaceMetricsProvider() }
    var metrics by remember { mutableStateOf(provider.next()) }
    LaunchedEffect(paused, hasCamPermission) {
        while (!paused && hasCamPermission) {
            delay(600)
            metrics = provider.next()
        }
    }

    fun nextQuestion() {
        if (qIndex < videoInterviewQuestions.lastIndex) {
            qIndex++; seconds = 0
        } else {
            navController.navigate(Routes.INTERVIEW_REPORT) {
                popUpTo(Routes.INTERVIEW_HUB)
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF14100B))) {
        // 場景背景(沿用個人面試/沙盒的一對一場景)+ 暖黑漸層暈影,與個人面試一致
        Image(
            painter = painterResource(R.drawable.bg_scene_1on1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x59000000), 0.5f to Color(0xB3160F09), 1f to Color(0xF0140F0A),
                ),
            ),
        )
        Column(Modifier.fillMaxSize()) {
            // ── 頂部列
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(38.dp).clip(CircleShape).background(PaperWhite.copy(alpha = 0.1f))
                        .pressScale { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Close, contentDescription = null, tint = PaperWhite, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Outlined.Videocam, contentDescription = null, tint = BrandOrange, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(5.dp))
                Text("影像面試", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(50)).background(AccentGreen.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) { Text("練習模式", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.weight(1f))
                // 錄製計時
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RecordingDot(active = !paused)
                    Spacer(Modifier.width(6.dp))
                    Text(formatTime(seconds), color = PaperWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── 上半:河狸面試官
            Box(
                Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                InterviewerStage(speaking = interviewerSpeaking)
            }

            // 問題字幕
            QuestionSubtitle(text = question.text, focus = question.focus, speaking = interviewerSpeaking)

            Spacer(Modifier.height(12.dp))

            // ── 下半:使用者鏡頭 + 指標
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1.05f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(InkBlack),
            ) {
                if (hasCamPermission) {
                    CameraPreview(modifier = Modifier.fillMaxSize())
                } else {
                    CameraPlaceholder { permLauncher.launch(Manifest.permission.CAMERA) }
                }

                // 鏡頭上疊「你」標籤
                Box(
                    Modifier.align(Alignment.TopStart).padding(12.dp)
                        .clip(RoundedCornerShape(50)).background(InkBlack.copy(alpha = 0.55f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) { Text("你", color = PaperWhite, fontSize = 11.sp, fontWeight = FontWeight.Black) }

                // 即時指標(右側浮層,只在有鏡頭時顯示)
                if (hasCamPermission) {
                    LiveMetricsOverlay(
                        metrics = metrics,
                        paused = paused,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }

            // ── 底部控制
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 暫停/繼續
                Box(
                    Modifier.weight(1f).height(52.dp).clip(RoundedCornerShape(16.dp))
                        .background(PaperWhite.copy(alpha = 0.1f))
                        .pressScale { paused = !paused },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (paused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                            contentDescription = null, tint = PaperWhite, modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (paused) "繼續" else "暫停", color = PaperWhite, fontWeight = FontWeight.Bold)
                    }
                }
                // 下一題/結束
                Box(
                    Modifier.weight(1.4f).height(52.dp).clip(RoundedCornerShape(16.dp))
                        .background(BrandOrange).pressScale { nextQuestion() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (qIndex < videoInterviewQuestions.lastIndex) "下一題" else "結束,看報告",
                        color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

/* ── 河狸面試官舞台(立繪 + 說話 bob 動畫 + 光暈)────────────────── */
@Composable
private fun InterviewerStage(speaking: Boolean) {
    val trans = rememberInfiniteTransition(label = "interviewer")
    // 說話時上下小幅 bob
    val bob by trans.animateFloat(
        initialValue = 0f, targetValue = if (speaking) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse), label = "bob",
    )
    val glow by trans.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "glow",
    )
    Box(contentAlignment = Alignment.Center) {
        // 背後光暈
        Box(
            Modifier.size(220.dp).clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.10f * glow)),
        )
        Box(
            Modifier.size(170.dp).clip(CircleShape)
                .background(BrandOrange.copy(alpha = 0.14f * glow)),
        )
        Image(
            painter = painterResource(R.drawable.interviewer_lead),
            contentDescription = "面試官",
            modifier = Modifier
                .size(190.dp)
                .graphicsLayer { translationY = -bob * 6f },
        )
        // 說話指示(底部三點)
        if (speaking) {
            Box(Modifier.align(Alignment.BottomCenter).offset(y = 8.dp)) {
                SpeakingDots()
            }
        }
    }
}

@Composable
private fun SpeakingDots() {
    val t = rememberInfiniteTransition(label = "dots")
    Row(
        Modifier.clip(RoundedCornerShape(50)).background(InkBlack.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { i ->
            val a by t.animateFloat(
                initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(500, delayMillis = i * 160), RepeatMode.Reverse),
                label = "dot$i",
            )
            Box(Modifier.size(6.dp).clip(CircleShape).background(BrandAmber.copy(alpha = a)))
        }
    }
}

/* ── 問題字幕 ─────────────────────────────────────────────────── */
@Composable
private fun QuestionSubtitle(text: String, focus: String, speaking: Boolean) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xDB241B12))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("面試官", color = BrandAmber, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            if (speaking) {
                Spacer(Modifier.width(8.dp))
                Text("正在說…", color = PaperWhite.copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(text, color = PaperWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
        Spacer(Modifier.height(6.dp))
        Text("（$focus）", color = PaperWhite.copy(alpha = 0.4f), fontSize = 11.sp)
    }
}

/* ── CameraX 真鏡頭預覽 ──────────────────────────────────────── */
@Composable
private fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    // 前鏡頭(自拍鏡頭,面試練習用)
                    val selector = CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview)
                } catch (e: Exception) {
                    // 相機初始化失敗時靜默降級(預覽留空,不崩潰)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}

/* ── 沒相機權限時的佔位 ──────────────────────────────────────── */
@Composable
private fun CameraPlaceholder(onRequest: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(64.dp).clip(CircleShape).background(PaperWhite.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Videocam, contentDescription = null, tint = PaperWhite.copy(alpha = 0.6f), modifier = Modifier.size(30.dp)) }
        Spacer(Modifier.height(16.dp))
        Text("開啟鏡頭來練習", color = PaperWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "影像分析全部在你的手機上完成,畫面不會上傳、不會儲存。這只是幫你練習的自我覺察工具,不打分。",
            color = PaperWhite.copy(alpha = 0.55f), fontSize = 12.sp, lineHeight = 19.sp,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier.clip(RoundedCornerShape(14.dp)).background(BrandOrange)
                .pressScale(onClick = onRequest).padding(horizontal = 24.dp, vertical = 12.dp),
        ) { Text("開啟鏡頭", color = PaperWhite, fontWeight = FontWeight.Black) }
    }
}

/* ── 即時指標浮層（MediaPipe 接點,標「練習參考」）──────────────── */
@Composable
private fun LiveMetricsOverlay(metrics: FaceMetrics, paused: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(12.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(InkBlack.copy(alpha = 0.62f)).padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MetricBar("眼神接觸", metrics.eyeContact, BrandAmber, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            MetricBar("穩定度", metrics.stability, AccentGreen, Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            // 語速:列舉,用文字
            Column(Modifier.weight(1f)) {
                Text("語速", color = PaperWhite.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (paused) "—" else metrics.pace.label,
                    color = when (metrics.pace) {
                        PaceState.GOOD -> AccentGreen
                        else -> BrandAmber
                    },
                    fontSize = 15.sp, fontWeight = FontWeight.Black,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "練習參考,非評分 · 分析在裝置端完成,畫面不上傳",
            color = PaperWhite.copy(alpha = 0.4f), fontSize = 9.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun MetricBar(label: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    val anim by animateFloatAsState(targetValue = value / 100f, animationSpec = tween(500), label = "metric")
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = PaperWhite.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("$value", color = color, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(50))
                .background(PaperWhite.copy(alpha = 0.12f)),
        ) {
            Box(
                Modifier.fillMaxWidth(anim.coerceIn(0.02f, 1f)).fillMaxHeight()
                    .clip(RoundedCornerShape(50)).background(color),
            )
        }
    }
}

/* ── 錄製紅點(脈動)──────────────────────────────────────────── */
@Composable
private fun RecordingDot(active: Boolean) {
    if (!active) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(PaperWhite.copy(alpha = 0.3f)))
        return
    }
    val t = rememberInfiniteTransition(label = "rec")
    val a by t.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "recA",
    )
    Box(Modifier.size(10.dp).clip(CircleShape).background(AccentRed.copy(alpha = a)))
}

private fun formatTime(sec: Int): String {
    val m = sec / 60; val s = sec % 60
    return "%02d:%02d".format(m, s)
}
