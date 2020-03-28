package com.hooni.bookandaudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.databinding.GridlayoutListItemBinding
import com.hooni.bookandaudio.util.Util
import java.io.File

class ThumbnailAdapter(private val clickListener: (File) -> Unit) :
    RecyclerView.Adapter<ThumbnailAdapter.CustomViewHolder>() {
    private var thumbnailList = listOf<Pair<String, File>>()
    private lateinit var binding: GridlayoutListItemBinding

    companion object {
        private const val REQUIRED_THUMBNAIL_WIDTH = 200
        private const val REQUIRED_THUMBNAIL_HEIGHT = 180
    }

    inner class CustomViewHolder(binding: GridlayoutListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail
        private val title = binding.title


        fun bind(
            item: Pair<String, File>,
            clickListener: (File) -> Unit
        ) {
            val titleToSet = item.first
            val resizedBitmapToSet = Util.decodeSampledBitmapFromFile(
                item.second,
                REQUIRED_THUMBNAIL_WIDTH,
                REQUIRED_THUMBNAIL_HEIGHT
            )
            thumbnail.setImageBitmap(resizedBitmapToSet)
            title.text = titleToSet

            itemView.setOnClickListener {
                val directoryWithImagesOfBook = item.second.parentFile
                directoryWithImagesOfBook?.let {
                    clickListener(directoryWithImagesOfBook)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        binding =
            GridlayoutListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return thumbnailList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(thumbnailList[position], clickListener)
    }

    internal fun setThumbnailList(thumbnailListToSet: List<Pair<String, File>>) {
        thumbnailList = thumbnailListToSet.sortedBy {
            it.first
        }
    }
}