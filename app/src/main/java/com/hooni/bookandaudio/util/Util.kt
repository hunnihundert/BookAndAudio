package com.hooni.bookandaudio.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class Util {
    companion object {
        var screenWidth = 0
        const val ROOT_DIRECTORY = "/storage/emulated/0/"
        const val PAGES_DIRECTORY = "책"
        const val MEDIA_DIRECTORY = "음원"


        internal fun Bitmap.mergeImages(secondImage: Bitmap): Bitmap {
            val result =
                Bitmap.createBitmap(width + secondImage.width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawBitmap(this, 0f, 0f, null)
            canvas.drawBitmap(secondImage, width.toFloat(), 0f, null)
            return result
        }


        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val (width, height) = options.run { outWidth to outHeight }
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                // if no value is entered for reqHeight or reqWidth, it will be set to 0
                // meaning the respective dimension is irrelevant
                // to make the respective condition of the value loop always false, the value
                // will be set to -100 (-1 may make the value to >0 due to int rounding)
                val halfWidth = if (reqWidth == 0) -100 else width / 2
                val halfHeight = if (reqHeight == 0) -100 else height / 2

                while (halfHeight / inSampleSize >= reqHeight || halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize += 1
                }
            }
            return inSampleSize
        }

        internal suspend fun decodeSampledBitmapFromFile(
            file: File,
            reqWidth: Int = 0,
            reqHeight: Int = 0
        ): Bitmap =
            withContext(Dispatchers.IO) {
                BitmapFactory.Options().run {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeFile(file.path, this)
                    inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                    inJustDecodeBounds = false
                    BitmapFactory.decodeFile(file.path, this)
                }
            }
    }
}