package com.hooni.bookandaudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.databinding.LibraryListItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThumbnailAdapter(private val clickListener: (Book) -> Unit) :
    RecyclerView.Adapter<ThumbnailAdapter.CustomViewHolder>() {
    private var thumbnailList = listOf<Book>()
    private lateinit var binding: LibraryListItemBinding

    inner class CustomViewHolder(binding: LibraryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail


        fun bind(
            item: Book,
            clickListener: (Book) -> Unit
        ) {
            val coverImageToSet = item.imageDirectory.listFiles()!![0]
            Glide
                .with(thumbnail)
                .load(coverImageToSet)
                .fallback(android.R.drawable.ic_menu_report_image)
                .fitCenter()
                .into(thumbnail)

            itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        binding =
            LibraryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return thumbnailList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            holder.bind(thumbnailList[position], clickListener)
        }
    }

    internal fun setThumbnailList(thumbnailListToSet: List<Book>) {
        thumbnailList = thumbnailListToSet.sortedBy {
            it.title
        }
    }
}