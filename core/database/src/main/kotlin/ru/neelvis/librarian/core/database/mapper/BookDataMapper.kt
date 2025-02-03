package ru.neelvis.librarian.core.database.mapper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.neelvis.librarian.core.database.local.LocalBookEntity
import ru.neelvis.librarian.core.model.Book

fun LocalBookEntity.toBook(): Book =
    Book(
        id = this.uuid,
        title = this.title,
        authors = this.authors,
        cover = this.bookCover,
        isbn = this.isbn,
        publishedDate = this.publishedDate,
        description = this.description
    )

fun Flow<List<LocalBookEntity>>.toBookList(): Flow<List<Book>> =
    map { it.map(LocalBookEntity::toBook) }

fun Book.toLocalBookEntity(): LocalBookEntity =
    LocalBookEntity(
        uuid = this.id,
        title = this.title,
        authors = this.authors,
        bookCover = this.cover,
        isbn = this.isbn,
        publishedDate = this.publishedDate,
        description = this.description
    )

fun List<Book>.toLocalBookEntities(): List<LocalBookEntity> = this.map { book ->
    LocalBookEntity(
        uuid = book.id,
        title = book.title,
        authors = book.authors,
        bookCover = book.cover,
        isbn = book.isbn,
        publishedDate = book.publishedDate,
        description = book.description
    )
}

