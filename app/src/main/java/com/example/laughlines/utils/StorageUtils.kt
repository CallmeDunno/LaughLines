package com.example.laughlines.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.laughlines.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object StorageUtils {

    fun saveImage(context: Context, name: String, bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/${context.getString(R.string.app_name).replace(" ", "")}")
            } else {
                val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), context.getString(R.string.app_name))
                val filePath = File(directory, name).path
                put(MediaStore.Images.Media.DATA, filePath)
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val imageOutStream: OutputStream? = context.contentResolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream!!)
            imageOutStream.close()
            Toast.makeText(context, context.getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareImage(context: Context, name: String, bitmap: Bitmap) {
        CoroutineScope(Job() + Dispatchers.Main).launch {

            bitmapToUri(context, name, bitmap)?.let {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, it)
                }
                val chooser = Intent.createChooser(intent, "Share Images")
                chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(chooser)
            }

        }
    }

    private fun bitmapToUri(context: Context, name: String, bitmap: Bitmap): Uri? {
        val cachePath = context.cacheDir
        val bitmapFile = File(cachePath, name)

        try {
            val stream = FileOutputStream(bitmapFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", bitmapFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

}