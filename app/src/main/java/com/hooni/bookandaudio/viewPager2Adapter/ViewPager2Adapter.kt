package com.hooni.bookandaudio.viewPager2Adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import kotlinx.android.synthetic.main.viewpager_list_item.view.*
import java.io.File


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Pair<File?, File?>>()

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val image = view.main_image

        fun bind(item: Pair<File?, File?>) {
            lateinit var bitmapToSet: Bitmap
            bitmapToSet = if (item.second == null) {
                uriToBitmap(item.first!!)
            } else {
                val firstBitmap = uriToBitmap(item.first!!)
                val secondBitmap = uriToBitmap(item.second!!)
                firstBitmap.mergeImages(secondBitmap)
            }
            image.setImageBitmap(bitmapToSet)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager_list_item,parent,false)
        return CustomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfImages.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(listOfImages[position])
    }

    internal fun setImageList(listOfUris: List<Pair<File?, File?>>) {
        listOfImages = listOfUris
    }


    private fun uriToBitmap(file: File): Bitmap {
        val mySource = ImageDecoder.createSource(file)
        return ImageDecoder.decodeBitmap(mySource)
    }

    private fun Bitmap.mergeImages(secondImage: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(width + secondImage.width, height, Bitmap.Config.ARGB_8888)
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