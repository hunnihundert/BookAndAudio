package com.hooni.bookandaudio.viewPager2Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import kotlinx.android.synthetic.main.viewpager_list_item.view.*
import java.io.File


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Any>()

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val image = view.main_image

        fun bind(item: Any) {
            lateinit var bitmapToSet: Bitmap
            when (item) {
                is Uri -> bitmapToSet = setUriToImageView(image.context, item)
                is File -> bitmapToSet = setUriToImageView(item)
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

    internal fun setImageList(listOfUris: List<Any>) {
        listOfImages = listOfUris
    }

    private fun setUriToImageView(context: Context, pathFileName: Uri): Bitmap {
        val mySource = ImageDecoder.createSource(context.contentResolver,pathFileName)
        return ImageDecoder.decodeBitmap(mySource)
    }

    private fun setUriToImageView(file: File): Bitmap {
        val mySource = ImageDecoder.createSource(file)
        return ImageDecoder.decodeBitmap(mySource)
    }
}