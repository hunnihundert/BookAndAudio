package com.hooni.bookandaudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class SharedViewModel : ViewModel() {
    val selectedBookFile = MutableLiveData<File>()

    fun setLibraryFolderList(selectedFolder: File) {
        selectedBookFile.value = selectedFolder
    }
}