package com.hooni.bookandaudio.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.adapter.BookViewerAdapter
import com.hooni.bookandaudio.databinding.FragmentBookViewerBinding
import com.hooni.bookandaudio.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BookViewFragment : Fragment() {
    private var _binding: FragmentBookViewerBinding? = null
    private val bookViewFragmentBinding get() = _binding!!
    private var hasAudio = true

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
        initSupportActionBarMenu()
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
        model.arePagesSwitched.observe(viewLifecycleOwner, Observer {
            model.setBookPages(model.selectedBook.value!!)
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mp.stop()
        mp.release()
    }

    private fun initSupportActionBarMenu() {
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            if (hasAudio) {
                if (mp.isPlaying) {
                    mp.pause()
                    bookViewFragmentBinding.playPauseMedia.setBackgroundResource(android.R.drawable.ic_media_play)
                } else {
                    mp.start()
                    bookViewFragmentBinding.playPauseMedia.setBackgroundResource(android.R.drawable.ic_media_pause)
                }
            } else {
                showNoAudioNotification()
            }

        }
        // TODO: unify playing track, when tapping next/previous track and play
        // TODO: autoplay next track, when track is finished (+ notification)

        bookViewFragmentBinding.previousTrack.setOnClickListener {
            if (hasAudio) {
                if (currentTrack > 0) {
                    currentTrack -= 1
                    playTrack(currentTrack)
                    Toast.makeText(
                        requireContext(),
                        "Track: ${currentTrack + 1}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                showNoAudioNotification()
            }

        }
        bookViewFragmentBinding.nextTrack.setOnClickListener {
            if (hasAudio) {
                if (currentTrack < currentTotalTracks - 1) {
                    currentTrack += 1
                    playTrack(currentTrack)
                    Toast.makeText(
                        requireContext(),
                        "Track: ${currentTrack + 1}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                showNoAudioNotification()
            }
        }
    }

    private fun showNoAudioNotification() {
        Toast.makeText(
            requireContext(),
            getString(R.string.does_not_have_audio),
            Toast.LENGTH_SHORT
        ).show()
    }

    // TODO: tracklist indicator

    private fun initMediaPlayer() {
        mp = MediaPlayer()
        if (model.getMediaPaths().isNullOrEmpty()) {
            hasAudio = false
            return
        } else {
            hasAudio = true
        }
        currentTotalTracks = model.getMediaPaths()!!.size

        mp.run {
            setDataSource(model.getMediaPaths()!![currentTrack])
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
        setTimeOnPositionBar()

    }

    private fun setTimeOnPositionBar() {
        lifecycleScope.launch {
            var progress = mp.currentPosition
            while (progress < mp.duration) {
                progress = mp.currentPosition
                bookViewFragmentBinding.mediaPosition.progress = progress
                val timePlayed = progress
                val timeLeft = mp.duration - timePlayed
                bookViewFragmentBinding.timePlayed.text = formatIntToTime(timePlayed)
                bookViewFragmentBinding.timeLeft.text =
                    getString(R.string.time_left, formatIntToTime(timeLeft))
                delay(1_000)
            }
        }
    }

    private fun formatIntToTime(progress: Int): String {
        val minutes = progress / 1000 / 60
        val seconds = progress / 1000 % 60

        var time = "$minutes:"
        if (seconds < 10) time += "0"
        time += seconds

        return time
    }

    private fun playTrack(trackToPlay: Int) {
        mp.run {
            stop()
            release()
        }
        mp = MediaPlayer()
        mp.run {
            setDataSource(model.getMediaPaths()!![trackToPlay])
            prepare()
            totalTime = duration
            initPositionBar()
            start()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.switch_pages).isVisible = true
        menu.findItem(R.id.change_grid).isVisible = false
        menu.findItem(R.id.full_screen).isVisible = true
        menu.findItem(R.id.library_folder).isVisible = false
        menu.findItem(R.id.subdirectory_settings).isVisible = false
        menu.findItem(R.id.about).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_bookViewerFragment_to_libraryFragment)
                true
            }
            R.id.switch_pages -> {
                model.setArePagesSwitched(!model.getArePagesSwitched())
                true
            }
            R.id.full_screen -> {
                setFullScreen()
                true
            }
            R.id.about -> {
                findNavController().navigate(R.id.action_bookViewerFragment_to_aboutFragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setFullScreen() {
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()
        //bookViewFragmentBinding.root.systemUiVisibility =
        //    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        bookViewFragmentBinding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //bookViewFragmentBinding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}