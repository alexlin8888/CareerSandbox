package com.careersandbox.app.data.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

/**
 * 客製化履歷 PDF 產生器介面，只依賴 CustomResumeData，
 * 不管資料是從 MockData 還是真實 API 組出來的。
 */
interface CustomResumePdfGenerator {
    fun generate(context: Context, fileName: String, data: CustomResumeData): File
}

object DeviceCustomResumePdfGenerator : CustomResumePdfGenerator {

    override fun generate(context: Context, fileName: String, data: CustomResumeData): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        drawResume(page.canvas, data)
        doc.finishPage(page)

        val file = File(context.cacheDir, sanitize(fileName))
        file.outputStream().use { doc.writeTo(it) }
        doc.close()
        return file
    }

    private fun sanitize(name: String): String {
        val base = name.trim().ifEmpty { "客製化履歷" }.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        return if (base.endsWith(".pdf", ignoreCase = true)) base else "$base.pdf"
    }

    private fun drawResume(canvas: Canvas, data: CustomResumeData) {
        val left = 48f
        val rightEdge = 547f
        var y = 80f

        val title = paint("#1F1916", 26f, bold = true)
        val sub = paint("#6B7280", 13f)
        val h2 = paint("#B85C3A", 15f, bold = true)
        val item = paint("#1F1916", 12.5f, bold = true)
        val body = paint("#374151", 11.5f)
        val meta = paint("#9CA3AF", 10.5f)
        val small = paint("#6B7280", 10.5f)
        val rule = Paint().apply { color = Color.parseColor("#E5E7EB"); strokeWidth = 1f; isAntiAlias = true }

        // === 標題區 ===
        canvas.drawText(data.name, left, y, title)
        y += 22f
        canvas.drawText(data.schoolLine, left, y, sub)
        y += 20f

        if (data.bio.isNotBlank()) {
            wrap(data.bio, body, rightEdge - left).forEach { line ->
                canvas.drawText(line, left, y, body)
                y += 15f
            }
            y += 6f
        }

        if (data.keywords.isNotEmpty()) {
            canvas.drawText(data.keywords.joinToString("  ·  "), left, y, small)
            y += 20f
        }

        canvas.drawLine(left, y, rightEdge, y, rule)
        y += 24f

        // === 聯絡區（空欄不印，定案二有寫）===
        val contactLine = listOfNotNull(
            data.email.takeIf { it.isNotBlank() },
            data.phone.takeIf { it.isNotBlank() },
            data.linkedin.takeIf { it.isNotBlank() },
            data.github.takeIf { it.isNotBlank() },
            data.portfolio.takeIf { it.isNotBlank() },
        ).joinToString("   ")
        if (contactLine.isNotBlank()) {
            canvas.drawText(contactLine, left, y, small)
            y += 24f
        }

        // === 技能區：coveredSkills 排最前 ===
        canvas.drawText("技能", left, y, h2)
        y += 20f
        if (data.coveredSkills.isNotEmpty()) {
            canvas.drawText("依此職缺：${data.coveredSkills.joinToString("、")}", left, y, body)
            y += 18f
        }
        if (data.otherSkills.isNotEmpty()) {
            canvas.drawText(data.otherSkills.joinToString("、"), left, y, body)
            y += 18f
        }
        if (data.languages.isNotEmpty()) {
            val langLine = data.languages.joinToString("、") { "${it.first} ${it.second}" }
            canvas.drawText(langLine, left, y, small)
            y += 18f
        }
        y += 10f

        // === 經歷區 ===
        canvas.drawText("經歷", left, y, h2)
        y += 20f
        data.experienceItems.forEach { exp ->
            canvas.drawText(exp.title, left, y, item)
            canvas.drawText(exp.timeRange, rightEdge - meta.measureText(exp.timeRange), y, meta)
            y += 16f
            wrap(exp.text, body, rightEdge - left).forEach { line ->
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

/**
 * 把 PDF 檔案的第一頁渲染成一張 Bitmap，給預覽對話框顯示用。
 * *2 是為了在手機螢幕上看起來夠清楚（PDF 原始尺寸是 72dpi，偏小偏糊）。
 */
fun renderFirstPageAsBitmap(file: File): Bitmap {
    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
        PdfRenderer(pfd).use { renderer ->
            val page = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(
                page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            return bitmap
        }
    }
}