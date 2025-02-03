package ru.neelvis.librarian.core.database.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM localbookentity")
    fun getAllBooks(): Flow<List<LocalBookEntity>>

    @Query("SELECT * FROM localbookentity WHERE title = :requiredTitle")
    fun findBooksByTitle(requiredTitle: String): Flow<List<LocalBookEntity>>

    @Query("SELECT * FROM localbookentity WHERE uuid = :requiredID LIMIT 1")
    fun findBookByID(requiredID: String): LocalBookEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: LocalBookEntity)

    @Update
    fun updateBook(book: LocalBookEntity)

    @Delete
    suspend fun deleteBook(book: LocalBookEntity)

    @Delete
    suspend fun deleteBooks(books: List<LocalBookEntity>)
}