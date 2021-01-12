@file:Suppress("unused")

package com.quanticheart.watermark.extentions

import android.app.Activity
import android.graphics.Color
import com.quanticheart.watermark.screenshot.ScreenShotListener
import com.quanticheart.watermark.screenshot.ScreenShotWatch
import com.quanticheart.watermark.screenshot.model.ScreenshotData
import com.quanticheart.watermark.watermarkExt.*


internal var screenObserver: ScreenShotWatch? = null

fun Activity?.observerPrintScreen(callbackBitmap: (filePath: String) -> Unit) {
    this?.let {
        screenObserver = ScreenShotWatch(contentResolver, this, object : ScreenShotListener {
            override fun onScreenshotTaken(data: ScreenshotData) {
                destroyObserverPrintScreen()
                resultProcess(data, callbackBitmap)
            }
        })
        addRegister()
    }
}

private fun addRegister() {
    screenObserver?.register()
}

fun Activity?.destroyObserverPrintScreen() {
    screenObserver?.unregister()
}

private fun Activity.resultProcess(
    data: ScreenshotData,
    callbackBitmap: (filePath: String) -> Unit
) {
    val watermark = "Scott"
    try {

        var newBitmap =
            data.bitmap.addCornerWatermark(watermark, WatermarkCorner.BOTTOM_LEFT)

        data.bitmap.recycle()

        newBitmap =
            newBitmap?.addOverlayWatermark(
                watermark, WatermarkOptions(
                    textColor = Color.BLACK
                )
            )

        newBitmap =
            newBitmap?.addMessageWatermark(
                "Hello. Capture with Android Screenshot Service :)",
                WatermarkOptions(
                    textColor = Color.BLACK
                )
            )

        val path = newBitmap?.saveImageWatermark(this, data.fileName)
        newBitmap?.recycle()

        runOnUiThread {
            path?.let { callbackBitmap(it) }
        }

        addRegister()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}