package com.hooni.bookandaudio.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import java.io.File

class Util {
    companion object {

        const val ROOT_DIRECTORY = "/storage/emulated/0/"

        internal fun fileToBitmap(file: File): Bitmap {
            val options = BitmapFactory.Options().apply {
                outConfig = Bitmap.Config.ARGB_8888
                inMutable = true
            }
            return BitmapFactory.decodeFile(file.path, options)
        }

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

            if (width > reqWidth || height > reqHeight) {
                val halfWidth = width / 2
                val halfHeight = width / 2

                while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        internal fun decodeSampledBitmapFromFile(
            file: File,
            reqWidth: Int,
            reqHeight: Int
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