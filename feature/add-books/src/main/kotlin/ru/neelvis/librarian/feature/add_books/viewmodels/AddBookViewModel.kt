package ru.neelvis.librarian.feature.add_books.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.common.usecase.UseCaseResult
import ru.neelvis.librarian.core.domain.usecases.AddBookUseCase
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase,
) : ViewModel() {

    private val _isBookAdded = MutableSharedFlow<BookEvent>()
    val isBookAdded = _isBookAdded.asSharedFlow()

    fun addBookToList(
        book: Book,
    ) {
        viewModelScope.launch {
            val result = withContext(viewModelScope.coroutineContext) {
                addBookUseCase.invoke(book)
            }
            _isBookAdded.emit(if (result is UseCaseResult.Success) BookEvent.BOOK_ADDED else BookEvent.ERROR((result as UseCaseResult.Error).exception.message ?: "Error while adding the book"))
        }
    }
}