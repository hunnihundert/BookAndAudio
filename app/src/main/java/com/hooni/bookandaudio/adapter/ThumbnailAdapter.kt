package com.hooni.bookandaudio.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.util.Util
import kotlinx.android.synthetic.main.gridlayout_list_item.view.*
import java.io.File

class ThumbnailAdapter : RecyclerView.Adapter<ThumbnailAdapter.CustomViewHolder>() {
    private var thumbnailList = listOf<Pair<String, File>>()

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val thumbnail = view.thumbnail
        private val title = view.title

        fun bind(item: Pair<String, File>) {
            val titleToSet = item.first
            val bitmapToSet = Util.uriToBitmap(item.second)
            val resizedBitmapToSet = Bitmap.createScaledBitmap(bitmapToSet, 150, 150, false)
            thumbnail.setImageBitmap(resizedBitmapToSet)
            title.text = titleToSet
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
        holder.bind(thumbnailList[position])
        holder.itemView.setOnClickListener {
            val folderWithImages = thumbnailList[holder.adapterPosition].second.parentFile
            // fragment starten

            // viewpager list update

        }
    }

    internal fun setThumbnailList(thumbnailListToSet: List<Pair<String, File>>) {
        thumbnailList = thumbnailListToSet.sortedBy {
            it.first
        }
    }
}