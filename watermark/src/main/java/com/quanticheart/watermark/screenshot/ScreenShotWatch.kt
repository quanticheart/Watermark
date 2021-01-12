package com.quanticheart.watermark.screenshot

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore

class ScreenShotWatch(
    contentResolver: ContentResolver,
    context: Context?,
    listener: ScreenShotListener?
) {
    private val mHandlerThread: HandlerThread = HandlerThread("ScreenShotWatch")
    private val mHandler: Handler
    private val mContentResolver: ContentResolver
    private val mContentObserver: ContentObserver
    private val mListener: ScreenShotListener?

    fun register() {
        mContentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            mContentObserver
        )
    }

    fun unregister() {
        mContentResolver.unregisterContentObserver(mContentObserver)
    }

    init {
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)
        mContentResolver = contentResolver
        mContentObserver = ScreenShotContentObserver(mHandler, context!!, contentResolver, listener)
        mListener = listener
    }
}