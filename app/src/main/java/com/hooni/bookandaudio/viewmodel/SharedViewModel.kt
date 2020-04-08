package com.hooni.bookandaudio.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hooni.bookandaudio.data.Book
import com.hooni.bookandaudio.util.Util
import java.io.File
import java.util.*

class SharedViewModel : ViewModel() {

    val library = MutableLiveData<List<Book>>()
    val bookPages = MutableLiveData<List<Pair<File?, File?>>>()
    val arePagesSwitched = MutableLiveData<Boolean>()
    private val mediaPaths = MutableLiveData<List<String>>()
    val selectedBook = MutableLiveData<Book>()
    val booksPerLine = MutableLiveData<Int>()

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
                val bookImages = findDirectory(book, Util.PAGES_DIRECTORY)
                val bookMedia = findDirectory(book, Util.MEDIA_DIRECTORY)

                //
                if (bookImages == null) continue
                bookData = Book(bookTitle, bookImages, bookMedia)
                tempList.add(bookData)
            }
            library.value = tempList
        }
        return isValidDirectory
    }

    private fun findDirectory(directory: File?, nameOfDirectory: String): File? {
        return when {
            directory == null -> {
                null
            }
            directory.name.substringAfterLast("/") == nameOfDirectory -> {
                directory
            }
            else -> {
                var result: File? = null
                if (directory.listFiles() != null) {
                    val subDirectories = directory.listFiles()!!.filter {
                        it.isDirectory
                    }
                    for (subSubDirectory in subDirectories) {
                        result = findDirectory(subSubDirectory, nameOfDirectory)
                        if (result != null) return result
                    }
                }
                result
            }
        }
    }

    internal fun setBookPages(selectedBook: Book) {
        var resultList = listOf<Pair<File?, File?>>()

        // creating Pairs of Images that belong together
        // images are added to a list and then added to pairs
        // - on index 1 and index n 'null' will be added, so first and last image have 'null' as a pair
        // - if there is an uneven amount of pages, the last page will get 'null' as a partner
        // - every second pair will be removed as zip with next means (1,2),(2,3),(3,4), etc.
        // - arePagesSwitched: as 2 pages are shown at the same time to get the "book-feeling"
        //   some times the wrong pages are attached to each other, to counter that, the second page
        //   also gets 'null' as a pair to switch that

        selectedBook.imageDirectory.listFiles()?.let {
            it.toMutableList<File?>().apply {
                add(1, null)
                arePagesSwitched.value?.let { _arePagesSwitched ->
                    if (_arePagesSwitched) add(3, null)
                }
                if (0 == size % 2)
                    add(size - 1, null)
                add(null)
            }.zipWithNext().run { slice(indices step 2) }
        }?.let { resultList = it }

        bookPages.value = resultList
    }


    private fun setMediaPaths() {
        val resultList = mutableListOf<String>()
        if (selectedBook.value!!.mediaDirectory == null) {
            mediaPaths.value = null
            return
        }
        for (file in selectedBook.value!!.mediaDirectory!!.listFiles()!!) {
            if (file.extension.toLowerCase(Locale.getDefault()) == "mp3" || file.extension.toLowerCase(
                    Locale.getDefault()
                ) == "wma"
            ) resultList.add(file.path)
        }
        mediaPaths.value = resultList
    }

    internal fun getMediaPaths() = mediaPaths.value

    internal fun setBookFolder(_selectedBook: Book) {
        selectedBook.value = _selectedBook
        setMediaPaths()
        setBookPages(_selectedBook)
    }

    internal fun setArePagesSwitched(_arePagesSwitched: Boolean) {
        arePagesSwitched.value = _arePagesSwitched
    }

    internal fun getArePagesSwitched() = arePagesSwitched.value ?: false

    internal fun setBooksPerLine(_booksPerLine: Int) {
        booksPerLine.value = _booksPerLine
    }

}