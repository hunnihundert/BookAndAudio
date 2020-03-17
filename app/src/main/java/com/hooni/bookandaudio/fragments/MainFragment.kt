package com.hooni.bookandaudio.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.viewPager2Adapter.ViewPager2Adapter
import java.io.File

class MainFragment: Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPager2Adapter
    private lateinit var testImageView: ImageView
    private lateinit var pickFile: Button
    private lateinit var pickFolder: Button
    private lateinit var uris: List<Uri>

    companion object {
        private val PICK_FILE = 0
        private val PICK_MAIN_FOLDER = 1
        private val ROOT_DIRECTORY = "/storage/emulated/0/"
        private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0
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
        lateinit var thisOne: DocumentFile
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                PICK_FILE -> {
                    // file picker
                    data?.data?.also {
                        Log.d("Uri",it.toString())
                        uris = listOf(it,it,it)
                    }
                }
                PICK_MAIN_FOLDER -> {
                    // folder picker
                    data?.data?.also { it ->
                        Log.d("Uri",it.toString())
                        listDirectories(it)
                        //uris = setUris(it)
                    }
                }
            }
            viewPagerAdapter.setImageUriList(uris)
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView(view: View) {
        viewPager = view.findViewById(R.id.main_image)
        viewPagerAdapter = ViewPager2Adapter()
        uris = listOf()
        viewPagerAdapter.setImageUriList(uris)
        viewPager.adapter = viewPagerAdapter
    }

    private fun pickFile() {
        val myUri = DocumentFile.fromSingleUri(requireContext(),Uri.parse(ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            //type = "image/jpeg"
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
            //flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(Intent.createChooser(intent,"Select something"),PICK_FILE)
    }

    private fun pickFolder() {
        val myUri = DocumentFile.fromSingleUri(requireContext(),Uri.parse(ROOT_DIRECTORY))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
        }
        startActivityForResult(Intent.createChooser(intent,"Select something"),PICK_MAIN_FOLDER)
    }


    private fun listDirectories(uri: Uri) {
        val selectedFolder = File("$ROOT_DIRECTORY${uri.path!!.substringAfter("primary:")}")
        if(requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }
        // files is an array containing all files of the directory of rootPath
        val files = selectedFolder.listFiles()
        Log.d("files",files?.size.toString())
        for(file in files) {
            Log.d("files","Filename: ${file.name}")
        }
    }

    private fun setImageToImageView(pathFileName: Uri) {
        val mySource = ImageDecoder.createSource(requireContext().contentResolver,pathFileName)
        val myBitmap = ImageDecoder.decodeBitmap(mySource)
        testImageView.setImageBitmap(myBitmap)
    }
}