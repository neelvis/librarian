package ru.neelvis.librarian.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.core.model.Book

interface BooksRepository {
    suspend fun addBook(book: Book)
    fun getAllBooks(): Flow<List<Book>>
    fun getBooksByTitle(title: String): Flow<List<Book>>
    fun getBookByID(id: String): Book
    suspend fun updateBookInfo(book: Book)
    suspend fun removeBook(book: Book)
    suspend fun removeBooks(book: List<Book>)
    suspend fun clearDB()
}