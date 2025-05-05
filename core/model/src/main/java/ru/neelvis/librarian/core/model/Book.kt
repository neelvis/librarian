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
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false
        if (title != other.title) return false
        if (authors != other.authors) return false
        if (source != other.source) return false
        if (!cover.contentEquals(other.cover)) return false
        if (isbn != other.isbn) return false
        if (publishedDate != other.publishedDate) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + authors.hashCode()
        result = 31 * result + (source?.hashCode() ?: 0)
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + (isbn?.hashCode() ?: 0)
        result = 31 * result + (publishedDate?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        return result
    }
}