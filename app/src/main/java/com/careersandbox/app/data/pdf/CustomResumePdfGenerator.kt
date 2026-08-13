package com.careersandbox.app.data.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

interface CustomResumePdfGenerator {
    fun generate(context: Context, fileName: String, data: CustomResumeData): File
}

object DeviceCustomResumePdfGenerator : CustomResumePdfGenerator {

    private const val PAGE_W = 595
    private const val PAGE_H = 842
    private const val SIDEBAR_W = 190f
    private const val SIDE_PAD = 26f
    private const val CONTENT_X = SIDEBAR_W + 34f
    private const val RIGHT = 555f

    override fun generate(context: Context, fileName: String, data: CustomResumeData): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create()
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

    // ===== 色票 =====
    private val cInk = Color.parseColor("#0B0E14")
    private val cBody = Color.parseColor("#374151")
    private val cMuted = Color.parseColor("#6B7280")
    private val cHairline = Color.parseColor("#E5E7EB")
    private val cAccent = Color.parseColor("#D84315")
    private val cWhite = Color.parseColor("#FFFFFF")
    private val cSidebar = Color.parseColor("#D84315")
    private val cSidebarSub = Color.parseColor("#FFD9C7")
    private val cSidebarLine = Color.argb(130, 255, 255, 255) // 側欄分隔線:半透明白

    private fun drawResume(canvas: Canvas, data: CustomResumeData) {
        canvas.drawColor(cWhite)
        canvas.drawRect(0f, 0f, SIDEBAR_W, PAGE_H.toFloat(), Paint().apply { color = cSidebar })

        drawSidebar(canvas, data)
        drawMainColumn(canvas, data)
    }

    // ===================== 左側色塊欄 =====================

    private fun drawSidebar(canvas: Canvas, data: CustomResumeData) {
        val x = SIDE_PAD
        val right = SIDEBAR_W - SIDE_PAD
        var y = 74f

        // 姓名
        val namePaint = textPaint(cWhite, 26f, Typeface.create("sans-serif-condensed", Typeface.BOLD))
        wrap(data.name, namePaint, right - x).forEach { line ->
            canvas.drawText(line, x, y, namePaint)
            y += 30f
        }
        y += 16f
        y = sidebarDivider(canvas, x, right, y)

        // 個人資料
        val contactFields = listOfNotNull(
            "Mail" to data.email,
            "Phone" to data.phone,
            "LinkedIn" to data.linkedin,
            "GitHub" to data.github,
            "Portfolio" to data.portfolio,
        ).filter { it.second.isNotBlank() }

        if (contactFields.isNotEmpty()) {
            y = sidebarHeader(canvas, "個人資料", x, y)
            val labelPaint = textPaint(cSidebarSub, 10.5f, Typeface.create("sans-serif-medium", Typeface.BOLD))
            val valuePaint = textPaint(cWhite, 11.5f, Typeface.DEFAULT)
            contactFields.forEach { (label, value) ->
                val displayValue = if (label == "Mail" || label == "Phone") value else shortenUrl(value)
                canvas.drawText(label, x, y, labelPaint)
                y += 14f
                wrap(displayValue, valuePaint, right - x).forEach { line ->
                    canvas.drawText(line, x, y, valuePaint)
                    y += 16f
                }
                y += 5f
            }
            y = sidebarDivider(canvas, x, right, y + 4f)
        }

        // 技能
        if (data.coveredSkills.isNotEmpty() || data.otherSkills.isNotEmpty()) {
            y = sidebarHeader(canvas, "技能", x, y)
            val skillPaint = textPaint(cWhite, 11.5f, Typeface.DEFAULT)
            val allSkills = data.coveredSkills + data.otherSkills
            allSkills.forEach { skill ->
                wrap(skill, skillPaint, right - x).forEach { line ->
                    canvas.drawText(line, x, y, skillPaint)
                    y += 16f
                }
            }
            y = sidebarDivider(canvas, x, right, y + 10f)
        }

        // 語言
        if (data.languages.isNotEmpty()) {
            y = sidebarHeader(canvas, "語言", x, y)
            val labelPaint = textPaint(cSidebarSub, 10.5f, Typeface.create("sans-serif-medium", Typeface.BOLD))
            val valuePaint = textPaint(cWhite, 11.5f, Typeface.DEFAULT)
            val grouped = data.languages.groupBy({ it.first }, { it.second })
            grouped.forEach { (language, certs) ->
                canvas.drawText(language, x, y, labelPaint)
                y += 14f
                wrap(certs.joinToString("、"), valuePaint, right - x).forEach { line ->
                    canvas.drawText(line, x, y, valuePaint)
                    y += 16f
                }
                y += 5f
            }
        }
    }

    /** 側欄用的區塊標題(白字、比內文大一點),回傳下一行內容該從的 y。*/
    private fun sidebarHeader(canvas: Canvas, label: String, x: Float, y: Float): Float {
        val paint = textPaint(cWhite, 15f, Typeface.create("sans-serif-medium", Typeface.BOLD))
        canvas.drawText(label, x, y, paint)
        return y + 22f
    }

    /** 側欄的白色分隔線,回傳下一個區塊該從的 y。*/
    private fun sidebarDivider(canvas: Canvas, x: Float, right: Float, y: Float): Float {
        canvas.drawLine(x, y, right, y, Paint().apply {
            color = cSidebarLine; strokeWidth = 1f; isAntiAlias = true
        })
        return y + 22f
    }

    // ===================== 右側主欄 =====================

    private fun drawMainColumn(canvas: Canvas, data: CustomResumeData) {
        var y = 74f

        // 摘要
        if (data.bio.isNotBlank()) {
            y = mainHeader(canvas, "摘要", y)
            val bioPaint = textPaint(cBody, 12.5f, Typeface.DEFAULT)
            wrap(data.bio, bioPaint, RIGHT - CONTENT_X).forEach { line ->
                canvas.drawText(line, CONTENT_X, y, bioPaint)
                y += 17.5f
            }
            y = mainDivider(canvas, y + 8f)
        }

        // 學歷
        if (data.schoolLine.isNotBlank()) {
            y = mainHeader(canvas, "學歷", y)
            val paint = textPaint(cBody, 12f, Typeface.DEFAULT)
            wrap(data.schoolLine, paint, RIGHT - CONTENT_X).forEach { line ->
                canvas.drawText(line, CONTENT_X, y, paint)
                y += 17f
            }
            y = mainDivider(canvas, y + 6f)
        }

        // 經歷
        if (data.experienceItems.isNotEmpty()) {
            y = mainHeader(canvas, "經歷", y)
            val dateColW = 76f
            val dateContentGap = 24f
            val itemContentX = CONTENT_X + dateColW + dateContentGap
            val datePaint = textPaint(cMuted, 11f, Typeface.DEFAULT)
            val titlePaint = textPaint(cInk, 13.5f, Typeface.create("sans-serif-medium", Typeface.BOLD))
            val bodyPaint = textPaint(cBody, 12f, Typeface.DEFAULT)

            data.experienceItems.forEachIndexed { idx, exp ->
                val blockStartY = y
                canvas.drawText(exp.timeRange, CONTENT_X, blockStartY, datePaint)

                var cy = blockStartY
                canvas.drawText(exp.title, itemContentX, cy, titlePaint)
                cy += 18.5f
                wrap(exp.text, bodyPaint, RIGHT - itemContentX).forEach { line ->
                    canvas.drawText(line, itemContentX, cy, bodyPaint)
                    cy += 16.5f
                }

                y = cy
                if (idx != data.experienceItems.lastIndex) y += 16f
            }
        }
    }

    /** 主欄區塊標題:橘色粗體,回傳下一行內容該從的 y。*/
    private fun mainHeader(canvas: Canvas, label: String, y: Float): Float {
        val paint = textPaint(cAccent, 15f, Typeface.create("sans-serif-medium", Typeface.BOLD))
        canvas.drawText(label, CONTENT_X, y, paint)
        return y + 22f
    }

    private fun mainDivider(canvas: Canvas, y: Float): Float {
        canvas.drawLine(CONTENT_X, y, RIGHT, y, Paint().apply {
            color = cHairline; strokeWidth = 1f; isAntiAlias = true
        })
        return y + 22f
    }

    // ===== 小工具函式 =====

    private fun textPaint(colorInt: Int, size: Float, typeface: Typeface) = Paint().apply {
        color = colorInt
        textSize = size
        this.typeface = typeface
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

    /** 把網址簡化成只顯示最後一段,通用處理任何網址格式。*/
    private fun shortenUrl(raw: String): String {
        val noProtocol = raw.trim().removePrefix("https://").removePrefix("http://")
        val noTrailingSlash = noProtocol.trimEnd('/')
        return noTrailingSlash.substringAfterLast('/')
    }

    /** 把 PDF 第一頁渲染成 Bitmap,給預覽對話框顯示用。*/
    fun renderFirstPageAsBitmap(file: File): Bitmap {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                val page = renderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                return bitmap
            }
        }
    }
}