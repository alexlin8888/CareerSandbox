package com.careersandbox.app.data.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.careersandbox.app.data.mock.MockData
import java.io.File

/**
 * 履歷 PDF 產生器。
 *
 * === 後端接點 ===
 * 目前 DeviceResumePdfGenerator 在裝置端用 PdfDocument 產生基本版 PDF(可用、可分享)。
 * 之後若要更精緻的版型(或改伺服器渲染),實作 ResumePdfGenerator 回傳檔案即可,前端 UI/分享流程不用動。
 */
interface ResumePdfGenerator {
    /** 產生 PDF,寫入 app cache,回傳檔案。fileName 不含路徑;副檔名會自動補 .pdf。 */
    fun generate(context: Context, fileName: String): File
}

object DeviceResumePdfGenerator : ResumePdfGenerator {

    override fun generate(context: Context, fileName: String): File {
        val doc = PdfDocument()
        // A4 @ 72dpi ≈ 595 x 842
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        drawResume(page.canvas)
        doc.finishPage(page)

        val file = File(context.cacheDir, sanitize(fileName))
        file.outputStream().use { doc.writeTo(it) }
        doc.close()
        return file
    }

    private fun sanitize(name: String): String {
        val base = name.trim().ifEmpty { "履歷" }.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        return if (base.endsWith(".pdf", ignoreCase = true)) base else "$base.pdf"
    }

    private fun drawResume(canvas: Canvas) {
        val user = MockData.currentUser
        val left = 48f
        val rightEdge = 547f
        var y = 80f

        val title = paint("#1F1916", 26f, bold = true)
        val sub = paint("#6B7280", 13f)
        val h2 = paint("#B85C3A", 15f, bold = true)
        val item = paint("#1F1916", 12.5f, bold = true)
        val body = paint("#374151", 11.5f)
        val meta = paint("#9CA3AF", 10.5f)
        val rule = Paint().apply { color = Color.parseColor("#E5E7EB"); strokeWidth = 1f; isAntiAlias = true }

        canvas.drawText(user.name, left, y, title)
        y += 22f
        canvas.drawText("${user.school} · ${user.department} · ${user.year}", left, y, sub)
        y += 18f
        canvas.drawLine(left, y, rightEdge, y, rule)
        y += 26f

        canvas.drawText("經歷", left, y, h2)
        y += 20f
        MockData.experiences.forEach { e ->
            canvas.drawText(e.title, left, y, item)
            canvas.drawText(e.timeRange, rightEdge - meta.measureText(e.timeRange), y, meta)
            y += 16f
            wrap(e.description, body, rightEdge - left).forEach { line ->
                canvas.drawText(line, left, y, body)
                y += 15f
            }
            y += 10f
        }
    }

    private fun paint(hex: String, size: Float, bold: Boolean = false) = Paint().apply {
        color = Color.parseColor(hex)
        textSize = size
        isFakeBoldText = bold
        isAntiAlias = true
    }

    /** 極簡逐字斷行:依量測寬度把字串切成多行。 */
    private fun wrap(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        var cur = StringBuilder()
        text.forEach { ch ->
            cur.append(ch)
            if (paint.measureText(cur.toString()) > maxWidth) {
                val s = cur.toString()
                lines.add(s.substring(0, s.length - 1))
                cur = StringBuilder().append(ch)
            }
        }
        if (cur.isNotEmpty()) lines.add(cur.toString())
        return lines
    }
}
