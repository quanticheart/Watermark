package com.quanticheart.watermark

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.quanticheart.watermark.extentions.destroyObserverPrintScreen
import com.quanticheart.watermark.extentions.observerPrintScreen
import com.quanticheart.watermark.screenshot.extentions.getBitmapFromPath
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private val READ_EXTERNAL_STORAGE_REQUEST = 0x1045

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }

    private fun ImageView.setImage(path: String) {
        val imgFile = File(path)
        if (imgFile.exists()) {
            val myBitmap = path.getBitmapFromPath(context, contentResolver)
            this.setImageBitmap(myBitmap)
        }
    }

    override fun onResume() {
        super.onResume()
        observerPrintScreen {
            imgView.setImage(it)
        }
    }

    override fun onPause() {
        super.onPause()
        destroyObserverPrintScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyObserverPrintScreen()
    }

    /**
     * Permissons
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    detectScreenshots()
                }
                return
            }
        }
    }

    private fun haveStoragePermission() = hasPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun detectScreenshots() {
        if (!haveStoragePermission()) {
            requestPermission()
        }
    }
}