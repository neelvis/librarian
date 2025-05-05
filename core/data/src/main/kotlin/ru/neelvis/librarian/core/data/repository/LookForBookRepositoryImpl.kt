package ru.neelvis.librarian.core.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.neelvis.librarian.core.domain.repository.LookForBookRepository
import ru.neelvis.librarian.core.model.Book
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import ru.neelvis.librarian.core.database.mapper.toBookWithCover
import ru.neelvis.librarian.core.model.OpenLibBook
import ru.neelvis.librarian.core.model.OpenLibResponse


val LookForBookRepositoryImpl = object : LookForBookRepository {
    val apiUri = "http://openlibrary.org/"
    val commonQuery = "search.json?q="
    val coverUri = "https://covers.openlibrary.org/b/id/"
    val coverQuerySuffix = "-M.jpg"

    override fun searchBookByKeywords(searchString: String): Flow<Book> = flow {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
        val coverClient = HttpClient(CIO)
        try {
            val response: OpenLibResponse = client.get("$apiUri$commonQuery$searchString").body()
            val books = response.books
            for (book in books) {
                val cover: ByteArray? = book.coverI?.let { coverClient.get("$coverUri${book.coverI}$coverQuerySuffix").bodyAsBytes() }
                emit(book.toBookWithCover(cover))
            }

        } finally {
            client.close()
        }
    }
}