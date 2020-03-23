package com.hooni.bookandaudio.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import java.io.File

class Util {
    companion object {

        const val ROOT_DIRECTORY = "/storage/emulated/0/"

        internal fun uriToBitmap(file: File): Bitmap {
            val mySource = ImageDecoder.createSource(file)
            return ImageDecoder.decodeBitmap(mySource)
        }

        internal fun Bitmap.mergeImages(secondImage: Bitmap): Bitmap {
            val result =
                Bitmap.createBitmap(width + secondImage.width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            canvas.drawBitmap(this.copy(Bitmap.Config.ARGB_8888, false), 0f, 0f, null)
            canvas.drawBitmap(
                secondImage.copy(Bitmap.Config.ARGB_8888, false),
                width.toFloat(),
                0f,
                null
            )
            return result
        }
    }
}