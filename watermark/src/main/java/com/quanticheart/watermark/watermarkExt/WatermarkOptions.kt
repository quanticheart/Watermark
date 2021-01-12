package com.quanticheart.watermark.watermarkExt

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt

data class WatermarkOptions(
    val textSizeToWidthRatio: Int = 12,
    val paddingToWidthRatio: Int = 16,
    @ColorInt val textColor: Int = Color.WHITE,
    @ColorInt val shadowColor: Int = Color.TRANSPARENT,
    val typeface: Typeface? = null
)