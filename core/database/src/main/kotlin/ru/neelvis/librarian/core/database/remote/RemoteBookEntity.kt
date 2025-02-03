package ru.neelvis.librarian.core.database.remote

//TODO: implement synchronization with the remote DB
//@Serializable
internal data class RemoteBookEntity(
    val id: Int,
    val title: String,
    val authors: List<String>,
    val cover: ByteArray,
    val publishedDate: String,
    val description: String,
)
