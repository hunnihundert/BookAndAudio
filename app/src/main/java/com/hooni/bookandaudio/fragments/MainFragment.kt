package com.hooni.bookandaudio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.viewPager2Adapter.ViewPager2Adapter
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainFragment: Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPager2Adapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main,container, false)
        initRecyclerView(view)
        return view
    }

    private fun initRecyclerView(view: View?) {
        val strings = listOf("eins","zwei", "drei", "vier", "fünf")
        viewPager = view!!.findViewById(R.id.view_pager)
        viewPagerAdapter = ViewPager2Adapter()
        viewPagerAdapter.setStringList(strings)
        viewPager.adapter = viewPagerAdapter
    }
}