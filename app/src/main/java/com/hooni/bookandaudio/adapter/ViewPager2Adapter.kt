package com.hooni.bookandaudio.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.databinding.ViewpagerListItemBinding
import com.hooni.bookandaudio.util.Util
import com.hooni.bookandaudio.util.Util.Companion.mergeImages
import java.io.File


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Pair<File?, File?>>()
    lateinit var binding: ViewpagerListItemBinding

    inner class CustomViewHolder(binding: ViewpagerListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val image = binding.mainImage

        fun bind(item: Pair<File?, File?>) {
            lateinit var bitmapToSet: Bitmap
            bitmapToSet = if (item.second == null) {
                Util.decodeSampledBitmapFromFile(item.first!!, Util.screenWidth / 4)
            } else {
                val firstBitmap =
                    Util.decodeSampledBitmapFromFile(item.first!!, Util.screenWidth / 4)
                val secondBitmap =
                    Util.decodeSampledBitmapFromFile(item.second!!, Util.screenWidth / 4)
                firstBitmap.mergeImages(secondBitmap)
            }
            image.setImageBitmap(bitmapToSet)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        binding =
            ViewpagerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
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