package ru.neelvis.librarian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenLibResponse(
    val numFound: Int,
    val books: List<OpenLibBook>
)


@Serializable
data class OpenLibBook(
    val key: String,
    val title: String,
    val coverI: String? = null,
    val authorName: List<String>,
    val isbn: String,
    val firstPublishYear: String? = null,
)