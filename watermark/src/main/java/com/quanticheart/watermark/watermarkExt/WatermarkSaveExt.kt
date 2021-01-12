package com.quanticheart.watermark.watermarkExt

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.quanticheart.watermark.screenshot.extentions.log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Throws(Exception::class)
fun Bitmap.saveImageWatermark(context: Context, originalTitle: String): String {
    var title = originalTitle
    val fOut: OutputStream?
    title = title.replace(" ".toRegex(), "+")
    var index = title.lastIndexOf(".png")
    if (index == -1) {
        index = title.lastIndexOf(".jpg")
    }
    val rawFileName = title.substring(0, index)
    val fileName = "$rawFileName-edit.png"
    val appDirectoryName = "Screenshots"
    val imageRoot = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        appDirectoryName
    )
    imageRoot.mkdirs()
    val file = File(imageRoot, fileName)
    fOut = FileOutputStream(file)
    compress(Bitmap.CompressFormat.PNG, 100, fOut)
    fOut.flush()
    fOut.close()
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "XXXXX")
    values.put(MediaStore.Images.Media.DESCRIPTION, "description here")
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.hashCode())
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.name)
    }
    values.put("_data", file.absolutePath)
    val cr = context.contentResolver
    val newUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri))
    file.absolutePath.log()
    return file.absolutePath
}
