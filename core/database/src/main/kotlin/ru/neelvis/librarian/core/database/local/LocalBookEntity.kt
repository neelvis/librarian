package ru.neelvis.librarian.core.database.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalBookEntity(
    @PrimaryKey val uuid: String,
    val isbn: String?,
    val authors: List<String>,
    val title: String,
    val publishedDate: String?,
    val bookCover: ByteArray?,
    // @Ignore val bookCover: Bitmap?,
    val description: String?,
)