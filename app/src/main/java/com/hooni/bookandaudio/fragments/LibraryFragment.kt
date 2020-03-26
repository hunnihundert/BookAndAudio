package com.hooni.bookandaudio.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.hooni.bookandaudio.databinding.FragmentThumbnailViewerBinding
import com.hooni.bookandaudio.util.Util
import com.hooni.bookandaudio.viewmodel.SharedViewModel
import java.io.File

class LibraryFragment : Fragment() {

    private var _binding: FragmentThumbnailViewerBinding? = null
    private val libraryFragmentBinding get() = _binding!!

    companion object {
        private const val PICK_MAIN_FOLDER = 0
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_ALL_FOLDERS = 0
    }

    private val model: SharedViewModel by activityViewModels()
    private lateinit var gridlayoutManager: GridLayoutManager
    private lateinit var thumbnailAdapter: ThumbnailAdapter
    private lateinit var thumbnailRecyclerView: RecyclerView
    private lateinit var selectedFolder: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThumbnailViewerBinding.inflate(layoutInflater)
        val view = libraryFragmentBinding.root
        initRecyclerView(view)
        model.thumbnails.observe(viewLifecycleOwner, Observer {
            thumbnailAdapter.setThumbnailList(it)
            thumbnailAdapter.notifyDataSetChanged()
        })
        libraryFragmentBinding.allFolderPicker.setOnClickListener {
            pickFolder()
        }
        return view
    }

    private fun initRecyclerView(view: View) {
        thumbnailRecyclerView = view.findViewById(R.id.thumbnail_recycler_view)
        gridlayoutManager =
            GridLayoutManager(requireContext(), 6, LinearLayoutManager.VERTICAL, false)
        thumbnailRecyclerView.layoutManager = gridlayoutManager
        thumbnailAdapter =
            ThumbnailAdapter { selectedBook: File -> displaySelectedBook(selectedBook) }
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
            if (!model.setThumbnails(mainFolder)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_is_null_or_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun displaySelectedBook(_selectedBook: File) {
        model.setBookFolder(_selectedBook)
        findNavController().navigate(R.id.action_allFoldersFragment_to_oneFolderFragment)
    }
}