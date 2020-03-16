package com.hooni.bookandaudio.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.hooni.bookandaudio.R
import com.hooni.bookandaudio.viewPager2Adapter.ViewPager2Adapter
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.io.File

class MainFragment: Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerAdapter: ViewPager2Adapter
    private lateinit var listOfDirectories: List<DocumentFile>
    private lateinit var testImageView: ImageView
    private lateinit var testButton: Button

    companion object {
        private val PICK_MAIN_FOLDER = 1
        private val SUB_DIRECTORY_TEST = "영어/음원 O/A House Is A house for Me/"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_test, container, false)
        testImageView = view.findViewById(R.id.imageView)
        testButton = view.findViewById(R.id.imageTestButton)
//        initRecyclerView(view)
//        view.open_file_choser.setOnClickListener {
//           pickMainFolder()
//        }
        testButton.setOnClickListener {
            pickMainFolder()

        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        lateinit var thisOne: DocumentFile
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                Log.d("Uri",it.toString())
                //thisOne = DocumentFile.fromTreeUri(requireContext(),it) ?: return
                createListOfImages(it)
            }
        }
        //listOfDirectories = thisOne.listFiles().filter { it.isDirectory }


    }

    private fun initRecyclerView(view: View) {
        val strings = listOf("1","2","3")
        viewPager = view.findViewById(R.id.main_image)
        viewPagerAdapter = ViewPager2Adapter()
        viewPagerAdapter.setStringList(strings)
        viewPager.adapter = viewPagerAdapter
    }

    private fun pickMainFolder() {
        val myUri = DocumentFile.fromSingleUri(requireContext(),Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A"))
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, myUri!!.uri)
            //flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivityForResult(Intent.createChooser(intent,"Select something"),PICK_MAIN_FOLDER)
    }

    private fun listDirectories() {
        val directory = requireContext().getExternalFilesDir(null)
        val files = directory!!.listFiles()
        Log.d("files",files?.size.toString())
        for(file in files) {
            Log.d("files","Filename: ${file.name}")
        }
    }

    private fun createListOfImages(pathFileName: Uri) {
        val mySource = ImageDecoder.createSource(requireContext().contentResolver,pathFileName)
        val myBitmap = ImageDecoder.decodeBitmap(mySource)
        testImageView.setImageBitmap(myBitmap)
    }
}