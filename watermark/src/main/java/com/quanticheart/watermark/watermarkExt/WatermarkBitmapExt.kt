@file:Suppress("DEPRECATION")

package com.quanticheart.watermark.watermarkExt

import android.app.Activity
import android.graphics.*
import android.graphics.Paint.Align
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.format.DateFormat
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.util.*


fun Bitmap?.addCornerWatermark(
    watermarkText: String,
    watermarkCorner: WatermarkCorner,
    options: WatermarkOptions = WatermarkOptions()
): Bitmap? {
    return this?.let { bitmap ->
        val result = bitmap.copy(bitmap.config, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

        paint.textAlign = when (watermarkCorner) {
            WatermarkCorner.TOP_LEFT,
            WatermarkCorner.BOTTOM_LEFT -> Align.LEFT
            WatermarkCorner.TOP_RIGHT,
            WatermarkCorner.BOTTOM_RIGHT -> Align.RIGHT
        }

        val textSize = options.textSizeToWidthRatio.toDp.toFloat()
        paint.textSize = textSize
        paint.color = options.textColor

        if (options.shadowColor != Color.TRANSPARENT)
            paint.setShadowLayer(textSize / 2, 0f, 0f, options.shadowColor)

        if (options.typeface != null)
            paint.typeface = options.typeface

        val padding = options.paddingToWidthRatio.toDp
        val x = when (watermarkCorner) {
            WatermarkCorner.TOP_LEFT,
            WatermarkCorner.BOTTOM_LEFT -> {
                padding
            }
            WatermarkCorner.TOP_RIGHT,
            WatermarkCorner.BOTTOM_RIGHT -> {
                width - padding
            }
        }

        val y = when (watermarkCorner) {
            WatermarkCorner.BOTTOM_LEFT,
            WatermarkCorner.BOTTOM_RIGHT -> {
                height - padding
            }
            WatermarkCorner.TOP_LEFT,
            WatermarkCorner.TOP_RIGHT -> {
                val bounds = Rect()
                paint.getTextBounds(watermarkText, 0, watermarkText.length, bounds)
                val textHeight = bounds.height()
                textHeight + padding

            }
        }
        canvas.drawText(watermarkText, x.toFloat(), y.toFloat(), paint)
        result
    }
}

fun Bitmap.addOverlayWatermark(
    watermarkText: String,
    options: WatermarkOptions = WatermarkOptions()
): Bitmap {
    val result = copy(config, true)
    val canvas = Canvas(result)

    var text = watermarkText

    for (i in 0..699) {
        text += " $watermarkText"
    }

    val mTextPaint = TextPaint()
    mTextPaint.textSize = options.textSizeToWidthRatio.toDp.toFloat()
    mTextPaint.color = options.textColor
    mTextPaint.alpha = 45

    val mTextLayout = StaticLayout(
        text,
        mTextPaint,
        canvas.width + 700,
        Layout.Alignment.ALIGN_CENTER,
        1.1f,
        0.3f,
        true
    )

    canvas.save()

    val textX = -200f
    val textY = -10f

    canvas.translate(textX, textY)
    mTextLayout.draw(canvas)
    canvas.restore()
    return result
}

fun Bitmap.addMessageWatermark(
    watermarkText: String,
    options: WatermarkOptions = WatermarkOptions()
): Bitmap {

    val result = copy(config, true)
    val canvas = Canvas(result)

    //text
    val mText = Paint()

    mText.textAlign = Align.CENTER
    mText.color = options.textColor
    mText.style = Paint.Style.FILL
    mText.textSize = options.textSizeToWidthRatio.toDp.toFloat()
    mText.alpha = 80
    mText.isAntiAlias = true

    if (options.typeface != null)
        mText.typeface = options.typeface

    //text outline
    val mTextBackground = Paint()
    val fm: Paint.FontMetrics = mText.fontMetrics
    mTextBackground.color = Color.GRAY
    mTextBackground.style = Paint.Style.FILL
    mTextBackground.alpha = 50
    mText.getFontMetrics(fm)

    val margin = 16.toDp
    val marginBottom = 150.toDp
    val cornersRadius = 50f

    val textX = (canvas.width / 2).toFloat()
    val textY = (canvas.height - marginBottom).toFloat()

    val tS: Float = mTextBackground.measureText(watermarkText)

    val rect = RectF(
        (textX - tS - margin),
        textY + fm.top - margin,
        (textX) + tS + margin,
        textY + fm.bottom + margin
    )

    canvas.drawRoundRect(
        rect, // rect
        cornersRadius, // rx
        cornersRadius, // ry
        mTextBackground // Paint
    )

    canvas.drawText(watermarkText, textX, textY, mText)
    return result
}

fun Activity?.takeFullScreenShot() {
    this?.let {
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath: String = Environment.getExternalStorageDirectory().toString()
                .toString() + "/" + now + ".jpg"

            // create bitmap screen capture
            val v1: View = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            val bitmap: Bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false
            val imageFile = File(mPath)
            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()
//            openScreenshot(imageFile)
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
    }
}
