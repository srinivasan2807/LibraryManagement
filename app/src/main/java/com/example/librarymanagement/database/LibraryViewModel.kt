package com.example.librarymanagement.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymanagement.database.dao.LibraryDao
import com.example.librarymanagement.database.db.LibraryDatabase
import com.example.librarymanagement.database.model.Book
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application):ViewModel() {
    private val libraryDao:LibraryDao
    val allBooks:LiveData<MutableList<Book>>

    init {
       val database = LibraryDatabase(application)
        libraryDao =database.libraryDao()
        allBooks = libraryDao.getAllBooks()
    }
    fun addBook(book: Book)=viewModelScope.launch {
        libraryDao.addBook(book)
    }

    fun updateBook(book: Book)=viewModelScope.launch {
        libraryDao.updateBook(book)
    }

    fun deleteBook(book: Book)=viewModelScope.launch {
        libraryDao.Delete(book)
    }
}
