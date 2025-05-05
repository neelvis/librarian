package ru.neelvis.librarian.feature.add_books

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.core.ui.BookInfoEditable
import ru.neelvis.librarian.feature.add_books.viewmodels.AddBookViewModel

@Composable
internal fun AddBookScreen(addBookViewModel: AddBookViewModel = hiltViewModel<AddBookViewModel>(), onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        addBookViewModel.isBookAdded.collect { event ->
            if (event is BookEvent.BOOK_ADDED) {
                Toast.makeText(context, "Book added successfully.", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            } else {
                Toast.makeText(context, "Book was not added.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    AddBookScreen(onAddBookClick = { book -> addBookViewModel.addBookToList(book) })
}

@Composable
fun AddBookScreen(onAddBookClick: (Book) -> Unit) {
    Column {
        Text("Add a book")
        BookInfoEditable(onAcceptClick = onAddBookClick)
    }
}

@Preview
@Composable
fun AddBookScreenPreview() {
    AddBookScreen(onAddBookClick = {})
}