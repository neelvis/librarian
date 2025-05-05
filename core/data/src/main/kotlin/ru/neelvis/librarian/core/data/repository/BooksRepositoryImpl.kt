package ru.neelvis.librarian.core.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.neelvis.librarian.core.data.di.DefaultDispatcher
import ru.neelvis.librarian.core.database.local.BookDao
import ru.neelvis.librarian.core.database.mapper.toBook
import ru.neelvis.librarian.core.database.mapper.toBookList
import ru.neelvis.librarian.core.database.mapper.toLocalBookEntities
import ru.neelvis.librarian.core.database.mapper.toLocalBookEntity
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BooksRepositoryImpl @Inject constructor(
    private val booksDatabase: BookDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : BooksRepository {

    override suspend fun addBook(book: Book) {
        val bookID = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        Log.d("AddBook", "Book with title ${book.title} and uuid $bookID")
        withContext(dispatcher) {
            booksDatabase.insertBook(
                book.copy(id = bookID).toLocalBookEntity()
            )
        }
    }

    override fun getBooksByTitle(title: String): Flow<List<Book>> =
        booksDatabase.findBooksByTitle(title).toBookList()

    override fun getBookByID(id: String): Book =
        booksDatabase.findBookByID(id).toBook()

    override fun getAllBooks(): Flow<List<Book>> = booksDatabase.getAllBooks().toBookList()

    override suspend fun updateBookInfo(book: Book) {
        booksDatabase.updateBook(book.toLocalBookEntity())
    }

    override suspend fun removeBook(book: Book) {
        booksDatabase.deleteBook(book.toLocalBookEntity())
    }

    override suspend fun removeBooks(books: List<Book>) {
        booksDatabase.deleteBooks(books.toLocalBookEntities())
    }

    override suspend fun clearDB() {
        withContext(dispatcher) {
            booksDatabase.clearDB()
        }
    }
}
