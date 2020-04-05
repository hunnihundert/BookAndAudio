package com.hooni.bookandaudio.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.databinding.BookPageListItemBinding
import com.hooni.bookandaudio.util.Util
import com.hooni.bookandaudio.util.Util.Companion.mergeImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class BookViewerAdapter : RecyclerView.Adapter<BookViewerAdapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Pair<File?, File?>>()
    lateinit var binding: BookPageListItemBinding

    inner class CustomViewHolder(binding: BookPageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val image = binding.mainImage

        suspend fun bind(item: Pair<File?, File?>) {
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
            BookPageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOfImages.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            holder.bind(listOfImages[position])
        }
    }

    internal fun setImageList(listOfUris: List<Pair<File?, File?>>) {
        listOfImages = listOfUris
    }
}