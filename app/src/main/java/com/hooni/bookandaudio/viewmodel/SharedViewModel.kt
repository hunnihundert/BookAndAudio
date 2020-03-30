package com.hooni.bookandaudio.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.util.Util
import java.io.File

class SharedViewModel : ViewModel() {
    val library = MutableLiveData<List<Book>>()
    val bookPages = MutableLiveData<List<Pair<File?, File?>>>()
    private val mediaPaths = MutableLiveData<List<String>>()
    private val selectedBook = MutableLiveData<Book>()

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

    private fun setBookPages(selectedBook: Book) {
        var resultList = listOf<Pair<File?, File?>>()

        // creating Pairs of Images that belong together
        // images are added to a list and then added to pairs
        // - on index 1 and index n 'null' will be added, so first and last image have 'null' as a pair
        // - if there is an uneven amount of pages, the last page will get 'null' as a partner
        // - every second pair will be removed as zip with next means (1,2),(2,3),(3,4), etc.

        selectedBook.imageDirectory.listFiles()?.let {
            it.toMutableList<File?>().apply {
                add(1, null)
                if (0 == size % 2)
                    add(size - 1, null)
                add(null)
            }.zipWithNext().run { slice(indices step 2) }
        }?.let { resultList = it }

        bookPages.value = resultList
    }


    private fun setMediaPaths() {
        val resultList = mutableListOf<String>()
        for (file in selectedBook.value!!.mediaDirectory.listFiles()!!) {
            resultList.add(file.path)
        }
        mediaPaths.value = resultList
    }

    internal fun getMediaPaths() = mediaPaths.value

    internal fun setBookFolder(_selectedBook: Book) {
        selectedBook.value = _selectedBook
        setMediaPaths()
        setBookPages(_selectedBook)
    }



}