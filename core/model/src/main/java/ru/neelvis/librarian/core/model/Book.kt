package ru.neelvis.librarian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String = "",
    val title: String = "",
    val authors: List<String> = emptyList<String>(),
    val source: String? = null,
    val cover: ByteArray? = byteArrayOf(),
    val isbn: String? = null,
    val publishedDate: String? = null,
    val description: String? = null,
)