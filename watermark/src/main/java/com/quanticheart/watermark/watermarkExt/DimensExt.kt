package com.quanticheart.watermark.watermarkExt

import android.content.res.Resources

internal val Int.toDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
