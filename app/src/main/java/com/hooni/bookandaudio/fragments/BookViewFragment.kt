package com.hooni.bookandaudio.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.adapter.BookViewerAdapter
import com.hooni.bookandaudio.databinding.FragmentBookViewerBinding
import com.hooni.bookandaudio.viewmodel.SharedViewModel

class BookViewFragment : Fragment() {
    private var _binding: FragmentBookViewerBinding? = null
    private val bookViewFragmentBinding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: BookViewerAdapter
    private val model: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookViewerBinding.inflate(layoutInflater)
        val view = bookViewFragmentBinding.root
        initRecyclerView(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.bookPages.observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.setImageList(it)
            viewPagerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView(view: View) {
        viewPager = view.findViewById(R.id.main_image)
        viewPagerAdapter = BookViewerAdapter()
        viewPagerAdapter.setImageList(listOf())
        viewPager.adapter = viewPagerAdapter
    }
}