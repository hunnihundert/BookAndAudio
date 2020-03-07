package com.hooni.bookandaudio.viewPager2Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import kotlinx.android.synthetic.main.viewpager_list_item.view.*


class ViewPager2Adapter: RecyclerView.Adapter<ViewPager2Adapter.CustomViewHolder>() {
    private var listOfImages = emptyList<String>()

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val mainText = view.main_text

        fun bind(item: String) {
            mainText.text = itemView.context.getString(R.string.placeholder,item)
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

    internal fun setStringList(listOfStrings: List<String>) {
        listOfImages = listOfStrings
    }
}