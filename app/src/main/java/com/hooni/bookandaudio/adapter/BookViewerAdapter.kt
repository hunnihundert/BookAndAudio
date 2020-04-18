package com.hooni.bookandaudio.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.hooni.bookandaudio.databinding.BookPageListItemBinding
import com.hooni.bookandaudio.util.Util.Companion.mergeImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class BookViewerAdapter : RecyclerView.Adapter<BookViewerAdapter.CustomViewHolder>() {
    private var listOfImages = emptyList<Pair<File?, File?>>()
    lateinit var binding: BookPageListItemBinding

    inner class CustomViewHolder(binding: BookPageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val image = binding.mainImage

        suspend fun bind(item: Pair<File?, File?>) {
            lateinit var bitmapToSet: Bitmap
            if (item.second == null) {
                Glide
                    .with(image)
                    .load(item.first!!)
                    .fallback(android.R.drawable.ic_menu_report_image)
                    .fitCenter()
                    .into(image)
            } else {
                withContext(Dispatchers.IO) {
                    val firstBitmap =
                        Glide.with(image).asBitmap().load(item.first!!)
                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
                    val secondBitmap =
                        Glide.with(image).asBitmap().load(item.second!!)
                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
                    bitmapToSet = firstBitmap.mergeImages(secondBitmap)
                }
                Glide.with(image).load(bitmapToSet).fitCenter().into(image)
            }
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