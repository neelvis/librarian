package ru.neelvis.librarian.feature.dashboard.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.neelvis.librarian.common.ui.BooksListUiState
import ru.neelvis.librarian.common.usecase.UseCaseResult
import ru.neelvis.librarian.core.domain.usecases.GetAllBooksUseCase
import ru.neelvis.librarian.core.domain.usecases.RemoveBooksUseCase
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject
import kotlin.collections.joinToString
import kotlin.collections.map

// Hold book card on the dashboard, to select multiple books
enum class SelectionMode { VIEW, SELECT }

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getAllBooksUseCase: GetAllBooksUseCase,
    val removeBooksUseCase: RemoveBooksUseCase,
) : ViewModel() {

    private val _selectionMode: MutableStateFlow<SelectionMode> = MutableStateFlow(SelectionMode.VIEW)
    fun setSelectionMode(mode: SelectionMode) {
        _selectionMode.value = mode
    }

    val selectionMode: StateFlow<SelectionMode> = _selectionMode.asStateFlow()

    // Set of the selected books, when _selectionMode is SELECT:
    private var _selectedBooks: MutableStateFlow<Set<Book>> = MutableStateFlow<Set<Book>>(setOf())
    val selectedBooks: StateFlow<Set<Book>> = _selectedBooks.asStateFlow()
    fun selectBook(book: Book) {
        _selectedBooks.value += book
        Log.d("BOOK", "Selected books: ${selectedBooks.value.map { it.title }.joinToString(", ")}")
    }

    fun deselectBook(book: Book) {
        _selectedBooks.value -= book
        Log.d("BOOK", "Selected books: ${selectedBooks.value.map { it.title }.joinToString(", ")}")
    }

    fun isSelected(book: Book) = _selectedBooks.value.contains(book)

    fun deleteSelected() {
        removeBooks(selectedBooks.value.toList())
        clearSelection()
    }

    fun clearSelection() {
        _selectedBooks.value = setOf()
        _selectionMode.value = SelectionMode.VIEW
    }

    // selected book in VIEW mode: open book card with extended info
    private val _currentBook: MutableState<Book> = mutableStateOf(Book())
    fun setCurrentBook(book: Book) {
        _currentBook.value = book
    }

    val currentBook: Book
        get() = _currentBook.value

    // All books in the database
    val booksUiState: StateFlow<BooksListUiState> =
        with(getAllBooksUseCase.invoke(Unit)) {
            when (this) {
                is UseCaseResult.Error -> {
                    flowOf(
                        BooksListUiState.Error
                    ).stateIn(//TODO: what's that
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5_000),
                        initialValue = BooksListUiState.Error
                    )
                }

                is UseCaseResult.Success -> {
                    this.data.map(BooksListUiState::Success).stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5_000),
                        initialValue = BooksListUiState.IsLoading
                    )
                }
            }
        }.also { Log.d(null, "getAllBooksUseCase invoked") }


    private val _isBookRemoved = MutableLiveData<Boolean?>(null)
    val isBookRemoved: LiveData<Boolean?> = _isBookRemoved
    fun removeCurrentBook() {
        removeBooks(listOf(currentBook))
    }

    fun removeBooks(books: List<Book>) {
        viewModelScope.launch {
            val result = async { removeBooksUseCase.invoke(books) }.await()
            _isBookRemoved.postValue(result is UseCaseResult.Success)
            Log.d(null, "removing ${books.size} books: $result")
        }
    }

    fun confirmRemoving() {
        _isBookRemoved.value = null
    }
}
