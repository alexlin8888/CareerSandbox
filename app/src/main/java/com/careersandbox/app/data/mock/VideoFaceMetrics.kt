package com.careersandbox.app.data.mock

import kotlin.math.sin
import kotlin.random.Random

/* =====================================================================
   影像面試 —— 臉部指標資料層（MediaPipe 接點）

   【後端/裝置端接入說明】
   真實數據來自 Google MediaPipe Face Landmarker（免費、Android 原生、
   資料不出裝置 → 隱私安全、避開歐盟情緒辨識法規）。
   MediaPipe 每幀回傳 52 個 blendshape + 478 個臉部 landmark + 頭部姿態,
   由此換算下列四個指標。目前前端用 mock 數據佔位,接點已留好:

   - eyeContact：由 eyeLookIn/Out/Up/Down blendshape + 虹膜 landmark 估視線是否朝鏡頭
   - pace：由語音活動偵測(VAD)+ 每分鐘音節數,屬語音側(MediaPipe Audio 或 STT)
   - stability：由頭部姿態(pitch/yaw/roll)的變動量,過度晃動扣分
   - expression：由 mouthSmile / browDown / jawOpen 等 blendshape 估表情自然度

   真接入時:把 MediaPipe 的 LandmarkerResult 在 callback 裡換算成 FaceMetrics,
   呼叫 FaceMetricsProvider 的 onResult,即可替換下方 mock。
   ===================================================================== */

/** 一次量測的臉部指標(0–100,pace 用列舉) */
data class FaceMetrics(
    val eyeContact: Int,      // 眼神接觸 0–100
    val stability: Int,       // 穩定度 0–100
    val expression: Int,      // 表情自然度 0–100
    val pace: PaceState,      // 語速
    val faceDetected: Boolean // 是否偵測到臉(MediaPipe 沒抓到臉時 false)
)

enum class PaceState(val label: String) {
    SLOW("偏慢"), GOOD("適中"), FAST("偏快")
}

/**
 * Mock 指標產生器:用平滑的正弦波 + 小雜訊模擬「即時分析」的數據漂移,
 * 讓 demo 看起來像真的在分析。真接 MediaPipe 時整個換掉。
 */
class MockFaceMetricsProvider {
    private var tick = 0

    fun next(): FaceMetrics {
        tick++
        val t = tick / 10f
        // 平滑漂移 + 小幅雜訊,維持在「不錯」的區間(練習工具偏正向)
        val eye = (72 + 18 * sin(t * 0.7) + Random.nextInt(-4, 5)).toInt().coerceIn(40, 98)
        val stab = (78 + 12 * sin(t * 0.5 + 1.2) + Random.nextInt(-3, 4)).toInt().coerceIn(45, 97)
        val expr = (70 + 15 * sin(t * 0.9 + 2.0) + Random.nextInt(-4, 5)).toInt().coerceIn(40, 95)
        val pace = when (((sin(t * 0.3) + 1) * 1.5).toInt()) {
            0 -> PaceState.SLOW
            2 -> PaceState.FAST
            else -> PaceState.GOOD
        }
        return FaceMetrics(eye, stab, expr, pace, faceDetected = true)
    }

    fun reset() { tick = 0 }
}

/** 影像面試的題庫(河狸面試官會問的) */
data class VideoQuestion(val text: String, val focus: String)

val videoInterviewQuestions = listOf(
    VideoQuestion("先用一分鐘介紹你自己吧。", "看你開場的眼神與穩定度"),
    VideoQuestion("說說一個你最有成就感的經驗。", "看你講述時的表情與投入"),
    VideoQuestion("遇到壓力或挫折時,你通常怎麼處理?", "看你面對難題時的鎮定"),
    VideoQuestion("為什麼是你,而不是其他人?", "看你表達自信的方式"),
    VideoQuestion("你還有什麼想問我的嗎?", "看你收尾的從容")
)
