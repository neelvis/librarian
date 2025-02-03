package ru.neelvis.librarian.core.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import ru.neelvis.librarian.common.usecase.SuspendUseCase
import ru.neelvis.librarian.core.domain.di.DefaultDispatcher
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class RemoveBooksUseCase @Inject constructor(
    private val repository: BooksRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) :
    SuspendUseCase<List<Book>, Unit>(dispatcher) {
    override suspend fun execute(parameters: List<Book>) {
        repository.removeBooks(parameters)
    }
}