package com.hooni.bookandaudio.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hooni.bookandaudio.util.Util
import java.io.File

class SharedViewModel : ViewModel() {
    val selectedBookFile = MutableLiveData<File>()
    val thumbnails = MutableLiveData<List<Pair<String, File>>>()

    internal fun setBookFolder(selectedFolder: File) {
        selectedBookFile.value = selectedFolder
    }

    internal fun setThumbnails(uriOfMainFolder: Uri): Boolean {
        var isValidDirectory = false
        lateinit var tempTitleThumbnail: Pair<String, File>
        val selectedFolder =
            File("${Util.ROOT_DIRECTORY}${uriOfMainFolder.path!!.substringAfter("primary:")}")
        val subDirectoriesLevel = selectedFolder.listFiles()?.filter {
            it.isDirectory
        }

        if (!subDirectoriesLevel.isNullOrEmpty()) {
            isValidDirectory = true
            val tempList = mutableListOf<Pair<String, File>>()
            for (book in subDirectoriesLevel) {
                val nameOfTheBook = book.name.substringAfterLast("/")
                val imageOfTheBook = book.listFiles()!![1].listFiles()!![0]
                tempTitleThumbnail = Pair(nameOfTheBook, imageOfTheBook)
                tempList.add(tempTitleThumbnail)
            }
            thumbnails.value = tempList
        }
        return isValidDirectory
    }


}