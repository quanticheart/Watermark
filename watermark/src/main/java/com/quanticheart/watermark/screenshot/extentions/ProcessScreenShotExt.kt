@file:Suppress("DEPRECATION")

package com.quanticheart.watermark.screenshot.extentions

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.quanticheart.watermark.screenshot.model.ScreenshotData
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.math.abs

private val PROJECTION = arrayOf(
    MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
    MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns._ID
)
private const val SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC"
private const val DEFAULT_DETECT_WINDOW_SECONDS: Long = 10
private const val FILE_POSTFIX = "FROM_ASS"
private const val tag = "ScreenShotContentObserver"
private var lastPath: String? = null

@SuppressLint("LongLogTag")
internal fun Any?.log() {
    this?.let { Log.e(tag, it.toString()) }
}

@Throws(Exception::class)
internal fun Uri?.process(
    mContext: Context,
    mContentResolver: ContentResolver,
    callback: (ScreenshotData) -> Unit
) {
    this?.let { uri ->

        uri.getLatestData(mContentResolver)?.let { result ->

            if (lastPath != null && lastPath == result.path) {
                "[Result] duplicate!!".log()
            } else {

                val currentTime = System.currentTimeMillis() / 1000

                if (matchPath(result.path) && matchTime(currentTime, result.dateAdded)) {

                    lastPath = result.path

                    "[Result] ${result.path}".log()

                    val screenUri = FileProvider.getUriForFile(
                        mContext,
                        mContext.packageName + ".profileimage.fileprovider",
                        File(result.path)
                    )

                    "[Result] This is screenshot!! : ${result.fileName} | dateAdded : ${result.dateAdded} / $currentTime".log()

                    val bitmap = screenUri.getBitmapFromUri(mContentResolver)

                    val copyBitmap = bitmap?.copy(bitmap.config, true)

                    bitmap?.recycle()

                    val temp = mContentResolver.delete(
                        screenUri,
                        null,
                        null
                    )
                    "Delete Result : $temp".log()

                    copyBitmap?.let {
                        callback(ScreenshotData(it, result.fileName, result.path, uri))
                    }
                }
            }
        } ?: "[Result] result is null".log()
    } ?: "[Result] No ScreenShot".log()
}

@Throws(FileNotFoundException::class)
private fun Uri?.getBitmapFromUri(mContentResolver: ContentResolver): Bitmap? {
    val parcelFileDescriptor = this?.let { mContentResolver.openFileDescriptor(it, "r", null) }
    return try {
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        image
    } catch (e: IOException) {
        "[Result catch] No ScreenShot : ${e.printStackTrace()}".log()
        null
    }
}

fun String.getBitmapFromPath(context: Context, mContentResolver: ContentResolver): Bitmap? {
    return try {
        val screenUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".profileimage.fileprovider",
            File(this)
        )

        return screenUri.getBitmapFromUri(mContentResolver)
    } catch (e: IOException) {
        "[Result catch] No ScreenShot : ${e.printStackTrace()}".log()
        null
    }
}

private fun Uri?.getLatestData(mContentResolver: ContentResolver): Data? {
    var data: Data? = null
    var rawCursor: Cursor? = null

    this?.let {
        try {
            rawCursor = mContentResolver.query(
                this,
                PROJECTION, null, null,
                SORT_ORDER
            )

            rawCursor?.let { cursor ->
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                    val fileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val dateAdded =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    val rawData = Data()
                    rawData.id = id
                    rawData.fileName = fileName
                    rawData.path = path
                    rawData.dateAdded = dateAdded
                    data = rawData

                    "[Recent File] Name : $fileName".log()
                }
            }

        } finally {
            rawCursor?.close()
        }
    }

    return data
}

internal class Data {
    var id: Long = 0
    var fileName: String = ""
    var path: String = ""
    var dateAdded: Long = 0
}

private fun matchPath(path: String?) =
    path?.toLowerCase(Locale.getDefault())?.contains("screenshots/") == true

private fun matchTime(currentTime: Long, dateAdded: Long): Boolean {
    return abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS
}