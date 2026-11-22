package com.careersandbox.app.ui.screens.interview

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.careersandbox.app.data.mock.InterviewAiReport
import com.careersandbox.app.data.remote.InterviewSessionSummaryDto
import com.careersandbox.app.data.repository.RemoteInterviewSessionRepository
import com.careersandbox.app.navigation.Routes
import com.careersandbox.app.ui.components.pressScale
import com.careersandbox.app.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal fun modeLabel(mode: String?): String = when (mode) {
    "single" -> "個人面試"
    "panel" -> "三對一"
    "group" -> "團體面試"
    else -> "面試"
}

// createdAt arrives as UTC ISO text; show it in the device time zone.
// SimpleDateFormat instead of java.time so it works on every API level.
internal fun formatSavedAt(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = parser.parse(iso.take(19)) ?: return ""
        java.text.SimpleDateFormat("M/d HH:mm", java.util.Locale.getDefault()).format(date)
    } catch (e: Exception) {
        ""
    }
}

private var openingReport = false

// Loads one stored interview and opens its report in view-only mode.
internal fun openSavedReport(
    scope: CoroutineScope,
    context: Context,
    navController: NavHostController,
    sessionId: String,
) {
    if (openingReport) return
    openingReport = true
    scope.launch {
        try {
            RemoteInterviewSessionRepository().get(sessionId)
                .onSuccess { detail ->
                    val report = detail.report
                    if (report == null) {
                        Toast.makeText(context, "這筆紀錄沒有報告內容", Toast.LENGTH_SHORT).show()
                    } else {
                        InterviewAiReport.response = report
                        ReportViewer.userSays = detail.groupSays.orEmpty()
                            .filter { it.isUser }
                            .map { it.content }
                        ReportViewer.viewingHistory = true
                        navController.navigate(Routes.INTERVIEW_REPORT)
                    }
                }
                .onFailure {
                    Toast.makeText(context, it.message ?: "讀取失敗，請稍後再試", Toast.LENGTH_SHORT).show()
                }
        } finally {
            openingReport = false
        }
    }
}

// UTC ISO text from the server -> epoch millis; null when malformed.
internal fun parseSavedAtMillis(iso: String?): Long? {
    if (iso.isNullOrBlank()) return null
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        parser.parse(iso.take(19))?.time
    } catch (e: Exception) {
        null
    }
}

@Composable
internal fun SavedInterviewRow(r: InterviewSessionSummaryDto, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(BrandYellow.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        modeLabel(r.mode),
                        color = BrandDeepOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    formatSavedAt(r.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray400,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                r.jobTitle?.takeIf { it.isNotBlank() } ?: "未命名職缺",
                style = MaterialTheme.typography.titleMedium,
                color = InkBlack,
                fontWeight = FontWeight.SemiBold,
            )
        }
        val score = r.score
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                if (score != null) "${score.roundToInt()}" else "—",
                color = BrandOrange,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
            )
            if (score != null) {
                Spacer(Modifier.width(2.dp))
                Text(
                    "分",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkGray500,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
        }
    }
}