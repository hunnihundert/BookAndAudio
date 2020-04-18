package com.hooni.bookandaudio.util

import android.graphics.Bitmap
import android.graphics.Canvas

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
    }
}