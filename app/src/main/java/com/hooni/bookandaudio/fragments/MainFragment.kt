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
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.viewPager2Adapter.ViewPager2Adapter
import java.io.File
import java.util.*

class MainFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPager2Adapter
    private lateinit var pickFile: Button
    private lateinit var pickFolder: Button
    private lateinit var uris: List<Uri>
    private var selectedFolder = Uri.EMPTY

    companion object {
        private const val PICK_FILE = 0
        private const val PICK_MAIN_FOLDER = 1
        private const val ROOT_DIRECTORY = "/storage/emulated/0/"
        private const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_viewer, container, false)

        // TODO: Change to ViewBinding
//        testImageView = view.findViewById(R.id.imageView)
        pickFile = view.findViewById(R.id.filePicker)
        pickFolder = view.findViewById(R.id.folderPicker)
        initRecyclerView(view)
        pickFile.setOnClickListener {
            pickFile()
        }
        pickFolder.setOnClickListener {
            pickFolder()
        }
        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_FILE -> {
                    // file picker
                    data?.data?.also {
                        uris = listOf(it, it, it)
                        viewPagerAdapter.setImageList(uris)
                    }
                }
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
        uris = listOf()
        viewPagerAdapter.setImageList(uris)
        viewPager.adapter = viewPagerAdapter
    }

    private fun pickFile() {
        val myUri = DocumentFile.fromSingleUri(requireContext(), Uri.parse(ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            //type = "image/jpeg"
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
            //flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(Intent.createChooser(intent, "Select something"), PICK_FILE)
    }

    private fun pickFolder() {
        val myUri = DocumentFile.fromSingleUri(requireContext(), Uri.parse(ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
        }
        startActivityForResult(Intent.createChooser(intent, "Select something"), PICK_MAIN_FOLDER)
    }


    private fun getImageList(uri: Uri): List<File> {
        var resultList = listOf<File>()
        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
            return resultList
        } else {
            val selectedFolder = File("$ROOT_DIRECTORY${uri.path!!.substringAfter("primary:")}")
            val files = selectedFolder.listFiles()
            if (files.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_is_null_or_empty),
                    Toast.LENGTH_SHORT
                ).show()
                return resultList
            }
            if (files.any { !it.isJpeg() }) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selected_folder_does_not_contain_images),
                    Toast.LENGTH_SHORT
                ).show()
                return resultList
            }
            // pass list to adapter
            resultList = files.toList()
            return resultList
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE && permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewPagerAdapter.setImageList(getImageList(selectedFolder))
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