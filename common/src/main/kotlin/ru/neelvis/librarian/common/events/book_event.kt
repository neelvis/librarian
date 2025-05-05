package ru.neelvis.librarian.common.events

sealed class BookEvent() {
    object BOOK_ADDED : BookEvent()
    object BOOK_REMOVED : BookEvent()
    object BOOK_UPDATED : BookEvent()
    data class ERROR(val message: String) : BookEvent()
}