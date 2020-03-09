package com.hooni.bookandaudio.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    companion object {
        private val PICK_MAIN_FOLDER = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main,container, false)
        //initRecyclerView(view)
        view.open_file_choser.setOnClickListener {
           pickMainFolder()
        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        lateinit var thisOne: DocumentFile
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also {
                Log.d("Uri",it.toString())
                thisOne = DocumentFile.fromTreeUri(requireContext(),it) ?: return
            }
        }
        listOfDirectories = thisOne.listFiles().filter { it.isDirectory }
    }

    private fun initRecyclerView(view: View?) {
        val strings = listOf("eins","zwei", "drei", "vier", "fünf")
        //viewPager = requireView().findViewById(R.id.view_pager)
        viewPagerAdapter = ViewPager2Adapter()
        viewPagerAdapter.setStringList(strings)
        viewPager.adapter = viewPagerAdapter
    }

    private fun pickMainFolder() {
        val myUri = DocumentFile.fromTreeUri(requireContext(),Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A"))
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
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
}