package com.example.librarymanagement.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.librarymanagement.database.model.Book
import com.example.librarymanagement.utils.Constants.DATABASE_TABLE

@Dao
interface LibraryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun Delete(book: Book)

    @Query("Select * from $DATABASE_TABLE")
    fun getAllBooks(): LiveData<MutableList<Book>>

    @Query("select * from $DATABASE_TABLE where id = :id")
    suspend fun getBookById(id: Int): Book
}