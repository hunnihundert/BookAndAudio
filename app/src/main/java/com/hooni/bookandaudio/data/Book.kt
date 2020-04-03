package com.hooni.bookandaudio.data

import java.io.File

// media can be null in case of a book without audio
data class Book(val title: String, val imageDirectory: File, val mediaDirectory: File?)