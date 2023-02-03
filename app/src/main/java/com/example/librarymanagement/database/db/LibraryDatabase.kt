package com.example.librarymanagement.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.librarymanagement.database.model.Book
import com.example.librarymanagement.database.dao.LibraryDao
import com.example.librarymanagement.utils.Constants.DATABASE_NAME

@Database(entities = [Book::class], version = 2, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun libraryDao(): LibraryDao

    companion object {
        @Volatile
        private var dbInstance: LibraryDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = dbInstance ?: synchronized(LOCK) {
            dbInstance ?: buildDatabase(context).also { dbInstance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                LibraryDatabase::class.java, DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
    }
}