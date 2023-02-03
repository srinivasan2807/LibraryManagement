package com.example.librarymanagement.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomMasterTable.TABLE_NAME
import com.example.librarymanagement.utils.Constants.DATABASE_TABLE

@Entity(tableName = DATABASE_TABLE)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name= "coverimage") val coverImage: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (!coverImage.contentEquals(other.coverImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + coverImage.contentHashCode()
        return result
    }
}