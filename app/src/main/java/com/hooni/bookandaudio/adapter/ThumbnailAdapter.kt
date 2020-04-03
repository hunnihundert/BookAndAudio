package com.hooni.bookandaudio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.databinding.LibraryListItemBinding
import com.hooni.bookandaudio.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThumbnailAdapter(private val clickListener: (Book) -> Unit) :
    RecyclerView.Adapter<ThumbnailAdapter.CustomViewHolder>() {
    private var thumbnailList = listOf<Book>()
    private lateinit var binding: LibraryListItemBinding

    companion object {
        private const val REQUIRED_THUMBNAIL_WIDTH = 200
        private const val REQUIRED_THUMBNAIL_HEIGHT = 180
    }

    inner class CustomViewHolder(binding: LibraryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val thumbnail = binding.thumbnail
        private val title = binding.title


        suspend fun bind(
            item: Book,
            clickListener: (Book) -> Unit
        ) {
            val titleToSet = item.title
            val coverImageToSet = item.imageDirectory.listFiles()!![0]


            val resizedBitmapToSet = Util.decodeSampledBitmapFromFile(
                coverImageToSet,
                REQUIRED_THUMBNAIL_WIDTH,
                REQUIRED_THUMBNAIL_HEIGHT
            )
            thumbnail.setImageBitmap(resizedBitmapToSet)
            title.text = titleToSet

            itemView.setOnClickListener {
//                val directoryWithImagesOfBook = (item.imageDirectory as File).parentFile
//                directoryWithImagesOfBook?.let {
//                    clickListener(item)
//                }
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