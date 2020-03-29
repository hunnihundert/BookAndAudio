package com.hooni.bookandaudio.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.util.Util
import java.io.File

class SharedViewModel : ViewModel() {
    val selectedBookFile = MutableLiveData<Book>()
    val library = MutableLiveData<List<Book>>()

    internal fun setBookFolder(selectedBook: Book) {
        selectedBookFile.value = selectedBook
    }

    internal fun setLibrary(uriOfMainFolder: Uri): Boolean {
        var isValidDirectory = false
        lateinit var bookData: Book
        val selectedFolder =
            File("${Util.ROOT_DIRECTORY}${uriOfMainFolder.path!!.substringAfter("primary:")}")
        val subDirectoriesLevel = selectedFolder.listFiles()?.filter {
            it.isDirectory
        }

        if (!subDirectoriesLevel.isNullOrEmpty()) {
            isValidDirectory = true
            val tempList = mutableListOf<Book>()
            for (book in subDirectoriesLevel) {
                val bookTitle = book.name.substringAfterLast("/")
                val bookImages = book.listFiles()!![1]
                val bookMedia = book.listFiles()!![0]
                bookData = Book(bookTitle, bookImages, bookMedia)
                tempList.add(bookData)
            }
            library.value = tempList
        }
        return isValidDirectory
    }


}