package ru.neelvis.librarian.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.core.model.Book

abstract class BooksRepository {
    abstract suspend fun addBook(
        title: String,
        authors: List<String>,
        cover: ByteArray?,
        isbn: String?,
        publishedDate: String?,
        description: String?,
    )

    abstract fun getBooksByTitle(title: String): Flow<List<Book>>
    abstract fun getBookByID(id: String): Book
    abstract fun getAllBooks(): Flow<List<Book>>
    abstract suspend fun removeBook(book: Book)
    abstract suspend fun removeBooks(book: List<Book>)
}