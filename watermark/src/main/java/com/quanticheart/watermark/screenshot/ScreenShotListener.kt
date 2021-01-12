package com.quanticheart.watermark.screenshot

import com.quanticheart.watermark.screenshot.model.ScreenshotData

interface ScreenShotListener {
    fun onScreenshotTaken(data: ScreenshotData)
}