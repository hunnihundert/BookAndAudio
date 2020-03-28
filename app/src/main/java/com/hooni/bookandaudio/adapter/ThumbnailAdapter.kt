package com.hooni.bookandaudio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.util.Util
import kotlinx.android.synthetic.main.gridlayout_list_item.view.*
import java.io.File

class ThumbnailAdapter(private val clickListener: (File) -> Unit) :
    RecyclerView.Adapter<ThumbnailAdapter.CustomViewHolder>() {
    private var thumbnailList = listOf<Pair<String, File>>()

    companion object {
        private const val REQUIRED_THUMBNAIL_WIDTH = 200
        private const val REQUIRED_THUMBNAIL_HEIGHT = 180
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val thumbnail = view.thumbnail
        private val title = view.title


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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gridlayout_list_item, parent, false)
        return CustomViewHolder(view)
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