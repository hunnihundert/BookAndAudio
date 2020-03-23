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
import android.widget.Button
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.adapter.ViewPager2Adapter
import com.hooni.bookandaudio.util.Util
import java.io.File
import java.util.*

class BookViewFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPager2Adapter
    private lateinit var pickFolder: Button
    private lateinit var switchFragment: Button
    private var selectedFolder = Uri.EMPTY

    companion object {
        private const val PICK_MAIN_FOLDER = 1
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_SINGLE_FOLDER = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_viewer, container, false)

        // TODO: Change to ViewBinding
        pickFolder = view.findViewById(R.id.folderPicker)
        switchFragment = view.findViewById(R.id.switchFragment)
        initRecyclerView(view)
        pickFolder.setOnClickListener {
            pickFolder()
        }
        switchFragment.setOnClickListener {
            findNavController().navigate(R.id.action_oneFolderFragment_to_allFoldersFragment)
        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_MAIN_FOLDER -> {
                    // folder picker
                    data?.data?.also { it ->
                        selectedFolder = it
                        viewPagerAdapter.setImageList(getImageList(it))
                    }
                }
            }
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView(view: View) {
        viewPager = view.findViewById(R.id.main_image)
        viewPagerAdapter = ViewPager2Adapter()
        viewPagerAdapter.setImageList(listOf())
        viewPager.adapter = viewPagerAdapter
    }

    private fun pickFolder() {
        val myUri = DocumentFile.fromSingleUri(requireContext(), Uri.parse(Util.ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
        }
        startActivityForResult(Intent.createChooser(intent, "Select something"), PICK_MAIN_FOLDER)
    }


    private fun getImageList(uri: Uri): List<Pair<File?, File?>> {
        var resultList = listOf<Pair<File?, File?>>()
        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_SINGLE_FOLDER
            )
        } else {
            val selectedFolder =
                File("${Util.ROOT_DIRECTORY}${uri.path!!.substringAfter("primary:")}")
            val files = selectedFolder.listFiles()

            if (files.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_is_null_or_empty),
                    Toast.LENGTH_SHORT
                ).show()
                return resultList
            }
            // TODO: reconsider, if there is a non-jpg file in the folder to still show images
            if (files.any { !it.isJpeg() }) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_does_not_contain_images),
                    Toast.LENGTH_SHORT
                ).show()
                return resultList
            }
            // pass list to adapter

            selectedFolder.listFiles()?.let {
                it.toMutableList<File?>().apply {
                    add(1, null)
                    if (0 == size % 2)
                        add(size - 1, null)
                    add(null)
                }.zipWithNext().run { slice(indices step 2) }
            }?.let { resultList = it }
        }
        return resultList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE_SINGLE_FOLDER -> viewPagerAdapter.setImageList(
                    getImageList(selectedFolder)
                )
            }

        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.external_storage_reading_permission_not_granted),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun File.isJpeg(): Boolean {
        val nameOfFile = this.toString()
        val fileExtension = nameOfFile.substringAfterLast('.').toLowerCase(Locale.getDefault())
        return (fileExtension == "jpeg" || fileExtension == "jpg")
    }
}