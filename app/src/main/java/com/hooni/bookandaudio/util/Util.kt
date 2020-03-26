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
    }
}