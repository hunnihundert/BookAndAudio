package com.hooni.bookandaudio.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.*
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.adapter.ThumbnailAdapter
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.databinding.FragmentLibraryViewerBinding
import com.hooni.bookandaudio.util.Util
import com.hooni.bookandaudio.viewmodel.SharedViewModel

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryViewerBinding? = null
    private val libraryFragmentBinding get() = _binding!!

    companion object {
        private const val PICK_MAIN_FOLDER = 0
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_ALL_FOLDERS = 0
        private const val BOOKS_PER_LINE = 4
    }

    private val model: SharedViewModel by activityViewModels()
    private lateinit var gridlayoutManager: GridLayoutManager
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private lateinit var thumbnailRecyclerView: RecyclerView
    private lateinit var selectedFolder: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = ""
        val preSelectedFolder = sharedPref.getString("uri", defaultValue)
        if (preSelectedFolder != "") setThumbnailFileList(preSelectedFolder!!.toUri())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLibraryViewerBinding.inflate(layoutInflater)
        val view = libraryFragmentBinding.root
        initRecyclerView(view)
        setScreenWidth()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.library.observe(viewLifecycleOwner, Observer {
            thumbnailAdapter.setThumbnailList(it)
            thumbnailAdapter.notifyDataSetChanged()
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(view: View) {
        thumbnailRecyclerView = view.findViewById(R.id.thumbnail_recycler_view)
        gridlayoutManager =
            GridLayoutManager(requireContext(), BOOKS_PER_LINE, LinearLayoutManager.VERTICAL, false)
        thumbnailRecyclerView.layoutManager = gridlayoutManager
        thumbnailAdapter =
            ThumbnailAdapter { selectedBook: Book -> displaySelectedBook(selectedBook) }
        thumbnailAdapter.setThumbnailList(listOf())
        thumbnailRecyclerView.adapter = thumbnailAdapter
    }

    private fun pickFolder() {
        val myUri = DocumentFile.fromSingleUri(requireContext(), Uri.parse(Util.ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select app to choose folder"),
            PICK_MAIN_FOLDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_ALL_FOLDERS ->
                    setThumbnailFileList(selectedFolder)
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.external_storage_reading_permission_not_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_MAIN_FOLDER -> {
                    data?.data?.also {
                        selectedFolder = it
                        setThumbnailFileList(it)
                    }
                }
            }
        }
    }

    private fun setThumbnailFileList(mainFolder: Uri) {
        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_ALL_FOLDERS
            )
        } else {
            // setThumbnails() returns false if folder is null or empty
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                val uriString = mainFolder.toString()
                //string.toUri()
                putString("uri", uriString)
                commit()
            }
            if (!model.setLibrary(mainFolder)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_is_null_or_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun displaySelectedBook(_selectedBook: Book) {
        model.setBookFolder(_selectedBook)
        findNavController().navigate(R.id.action_libraryFragment_to_bookViewerFragment)
    }

    private fun setScreenWidth() {
        val rect = Rect()
        libraryFragmentBinding.root.getWindowVisibleDisplayFrame(rect)
        Util.screenWidth = rect.width()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.switch_pages).isVisible = false
        menu.findItem(R.id.change_grid).isVisible = true
        menu.findItem(R.id.full_screen).isVisible = true
        menu.findItem(R.id.library_folder).isVisible = true
        menu.findItem(R.id.subdirectory_settings).isVisible = true
        menu.findItem(R.id.about).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_grid -> {
                true
            }
            R.id.full_screen -> {
                true
            }
            R.id.library_folder -> {
                pickFolder()
                true
            }
            R.id.subdirectory_settings -> {
                true
            }
            R.id.about -> {
                findNavController().navigate(R.id.action_libraryFragment_to_aboutFragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}