package ru.neelvis.librarian.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.core.model.Book

fun interface LookForBookRepository {
    fun searchBookByKeywords(searchString: String): Flow<Book>
}