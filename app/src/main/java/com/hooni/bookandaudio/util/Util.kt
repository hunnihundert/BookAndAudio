package com.hooni.bookandaudio.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import java.io.File

class Util {
    companion object {
        var screenWidth = 0
        const val ROOT_DIRECTORY = "/storage/emulated/0/"

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
                // meaning the respective dimension is irrelevant and therefore setting
                // the respective dimension check for the while loop will always be false
                val halfWidth = if (reqWidth == 0) -1 else width / 2
                val halfHeight = if (reqHeight == 0) -1 else height / 2

                if (reqHeight != 0 && reqWidth != 0) {
                    while (halfHeight / inSampleSize >= reqHeight || halfWidth / inSampleSize >= reqWidth) {
                        inSampleSize += 1
                    }
                }
            }



            return inSampleSize
        }

        internal fun decodeSampledBitmapFromFile(
            file: File,
            reqWidth: Int = 0,
            reqHeight: Int = 0
        ): Bitmap {
            return BitmapFactory.Options().run {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(file.path, this)
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                inJustDecodeBounds = false
                BitmapFactory.decodeFile(file.path, this)
            }
        }
    }
}