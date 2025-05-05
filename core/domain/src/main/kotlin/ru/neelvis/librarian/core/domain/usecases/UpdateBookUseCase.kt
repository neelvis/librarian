package ru.neelvis.librarian.core.domain.usecases;

import kotlinx.coroutines.CoroutineDispatcher
import ru.neelvis.librarian.common.usecase.SuspendUseCase
import ru.neelvis.librarian.core.domain.di.DefaultDispatcher
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(private val repository: BooksRepository, @DefaultDispatcher dispatcher: CoroutineDispatcher) :
    SuspendUseCase<Book, Unit>(dispatcher) {
    override suspend fun execute(parameters: Book) {
        repository.updateBookInfo(book = parameters)
    }
}
