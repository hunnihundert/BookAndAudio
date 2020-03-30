package com.hooni.bookandaudio.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.adapter.BookViewerAdapter
import com.hooni.bookandaudio.databinding.FragmentBookViewerBinding
import com.hooni.bookandaudio.viewmodel.SharedViewModel

class BookViewFragment : Fragment() {
    private var _binding: FragmentBookViewerBinding? = null
    private val bookViewFragmentBinding get() = _binding!!

    private lateinit var mp: MediaPlayer
    private var totalTime = 0
    private var currentTrack = 0
    private var currentTotalTracks = 0

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
        initRecyclerView()
        initButtons()
        initMediaPlayer()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.bookPages.observe(viewLifecycleOwner, Observer {
            viewPagerAdapter.setImageList(it)
            viewPagerAdapter.notifyDataSetChanged()
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mp.stop()
        mp.release()
    }

    private fun initRecyclerView() {
        viewPager = bookViewFragmentBinding.mainImage
        viewPagerAdapter = BookViewerAdapter()
        viewPagerAdapter.setImageList(listOf())
        viewPager.adapter = viewPagerAdapter
    }

    private fun initButtons() {
        // images
        bookViewFragmentBinding.playPauseMedia.setBackgroundResource(android.R.drawable.ic_media_play)
        bookViewFragmentBinding.nextTrack.setBackgroundResource(android.R.drawable.ic_media_next)
        bookViewFragmentBinding.previousTrack.setBackgroundResource(android.R.drawable.ic_media_previous)

        // onClickListeners
        bookViewFragmentBinding.playPauseMedia.setOnClickListener {
            // check if playing/pause
            if (mp.isPlaying) {
                mp.pause()
                bookViewFragmentBinding.playPauseMedia.setBackgroundResource(android.R.drawable.ic_media_play)
            } else {
                mp.start()
                bookViewFragmentBinding.playPauseMedia.setBackgroundResource(android.R.drawable.ic_media_pause)
            }
        }
        bookViewFragmentBinding.previousTrack.setOnClickListener {
            if (currentTrack > 0) {
                currentTrack -= 1
                playTrack(currentTrack)
                Toast.makeText(requireContext(), "Track: ${currentTrack + 1}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        bookViewFragmentBinding.nextTrack.setOnClickListener {
            if (currentTrack < currentTotalTracks - 1) {
                currentTrack += 1
                playTrack(currentTrack)
                Toast.makeText(requireContext(), "Track: ${currentTrack + 1}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initMediaPlayer() {
        mp = MediaPlayer()
        currentTrack = 0
        currentTotalTracks = model.getMediaPaths()?.size ?: 0
        mp.run {
            setDataSource(model.getMediaPaths()?.get(currentTrack))
            setVolume(0.5f, 0.5f)
            prepare()
        }
        totalTime = mp.duration
        initVolumeBar()
        initPositionBar()
    }

    private fun initVolumeBar() {
        bookViewFragmentBinding.mediaVolume.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        val selectedVolume = progress / 100.0f
                        mp.setVolume(selectedVolume, selectedVolume)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }

    private fun initPositionBar() {
        bookViewFragmentBinding.mediaPosition.max = totalTime
        bookViewFragmentBinding.mediaPosition.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )
    }

    private fun playTrack(trackToPlay: Int) {
        mp.stop()
        mp.release()
        mp = MediaPlayer()
        mp.setDataSource(model.getMediaPaths()?.get(trackToPlay))
        mp.prepare()
        totalTime = mp.duration
        initPositionBar()
        mp.start()
    }
}