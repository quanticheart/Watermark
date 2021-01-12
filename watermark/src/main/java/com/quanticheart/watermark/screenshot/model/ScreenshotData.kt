package com.quanticheart.watermark.screenshot.model

import android.graphics.Bitmap
import android.net.Uri

data class ScreenshotData(
    val bitmap: Bitmap,
    val fileName: String,
    val path: String,
    val rawPath: Uri
)