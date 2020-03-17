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


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Uri>()

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val image = view.main_image

        fun bind(item: Uri) {
            val bitmapToSet = setImageToImageView(image.context,item)
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

    internal fun setImageUriList(listOfUris: List<Uri>) {
        listOfImages = listOfUris
    }

    private fun setImageToImageView(context: Context, pathFileName: Uri): Bitmap {
        val mySource = ImageDecoder.createSource(context.contentResolver,pathFileName)
        return ImageDecoder.decodeBitmap(mySource)
    }
}