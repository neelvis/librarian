package ru.neelvis.librarian.feature.add_books.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.neelvis.librarian.common.usecase.UseCaseResult
import ru.neelvis.librarian.core.domain.usecases.AddBookUseCase
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

@HiltViewModel
class AddBooksViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase,
) : ViewModel() {

    private val _isBookAdded = MutableLiveData<Boolean?>(null)
    val isBookAdded: LiveData<Boolean?> = _isBookAdded

    fun addBookToList(
        book: Book,
    ) {
        viewModelScope.launch {
            val result = async {
                addBookUseCase.invoke(book)
            }.await()
            _isBookAdded.postValue(result is UseCaseResult.Success)
        }
    }

    fun finishAddingBook() {
        _isBookAdded.value = null
    }
}