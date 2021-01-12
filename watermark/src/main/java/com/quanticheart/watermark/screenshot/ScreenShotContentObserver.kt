package com.quanticheart.watermark.screenshot

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import com.quanticheart.watermark.screenshot.extentions.log
import com.quanticheart.watermark.screenshot.extentions.process

class ScreenShotContentObserver(
    handler: Handler?,
    private val mContext: Context,
    private val mContentResolver: ContentResolver,
    private val mListener: ScreenShotListener?
) : ContentObserver(handler) {

    @Synchronized
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        try {
//            process(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        } catch (e: Exception) {
            "[Finish] error : $e".log()
        }
    }

    @Synchronized
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
            try {
                countdown {
                    uri.process(mContext, mContentResolver) {
                        mListener?.onScreenshotTaken(it)
                    }
                }
            } catch (e: Exception) {
                "[Finish] error : $e".log()
            }
        } else {
            "[Finish] not EXTERNAL_CONTENT_URI ".log()
        }
    }

    private fun countdown(finishCountDown: () -> Unit) {
        object : CountDownTimer(1 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                finishCountDown()
            }
        }.start()
    }
}