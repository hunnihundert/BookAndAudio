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
import java.io.File

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
        model.selectedBookFile.observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.setImageList(getImageList(it))
            viewPagerAdapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView(view: View) {
        viewPager = view.findViewById(R.id.main_image)
        viewPagerAdapter = BookViewerAdapter()
        viewPagerAdapter.setImageList(listOf())
        viewPager.adapter = viewPagerAdapter
    }

    private fun getImageList(selectedBookFile: File): List<Pair<File?, File?>> {
        var resultList = listOf<Pair<File?, File?>>()

        selectedBookFile.listFiles()?.let {
            it.toMutableList<File?>().apply {
                add(1, null)
                if (0 == size % 2)
                    add(size - 1, null)
                add(null)
            }.zipWithNext().run { slice(indices step 2) }
        }?.let { resultList = it }

        return resultList
    }
}