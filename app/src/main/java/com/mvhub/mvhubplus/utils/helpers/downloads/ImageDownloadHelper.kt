package com.mvhub.mvhubplus.utils.helpers.downloads

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap


class ImageDownloadHelper(context: Context, seriesId: String?, param: com.mvhub.mvhubplus.callbacks.commonCallbacks.CommonApiCallBack) : AsyncTask<String, Int, Bitmap>() {
    private var context = context
    private var seriesId = seriesId
    private var commonApiCallBack = param
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String?): Bitmap? {
        val imageURL = params[0]

        var bitmap: Bitmap? = null
        try {
            // Download Image from URL
            val input = java.net.URL(imageURL).openStream()
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        com.mvhub.mvhubplus.utils.cropImage.helpers.Logger.e("ImageDownloadHelper", values.toString())
    }

    @SuppressLint("WrongThread")
    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {
            val destination = File(Environment.getExternalStorageDirectory().absolutePath,
                    "Android/data/" + context.packageName + "/files/Download/$seriesId.jpg")
            try {
                val fos = FileOutputStream(destination)
                result.compress(Bitmap.CompressFormat.PNG, 90, fos)
                fos.flush()
                fos.close()
                commonApiCallBack.onSuccess(destination.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
                com.mvhub.mvhubplus.utils.cropImage.helpers.Logger.e("ERROR", e.message)
                commonApiCallBack.onFailure(e)
            }

        }
    }

}