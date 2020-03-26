package com.hooni.bookandaudio.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.util.Util
import com.hooni.bookandaudio.util.Util.Companion.mergeImages
import kotlinx.android.synthetic.main.viewpager_list_item.view.*
import java.io.File


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Pair<File?, File?>>()

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val image = view.main_image

        fun bind(item: Pair<File?, File?>) {
            lateinit var bitmapToSet: Bitmap
            bitmapToSet = if (item.second == null) {
                Util.fileToBitmap(item.first!!)
            } else {
                val firstBitmap = Util.fileToBitmap(item.first!!)
                val secondBitmap = Util.fileToBitmap(item.second!!)
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
}