package ru.neelvis.librarian.core.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.neelvis.librarian.common.usecase.SuspendUseCase
import ru.neelvis.librarian.core.domain.di.DefaultDispatcher
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class GetBookByIDUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) :
    SuspendUseCase<String, Book>(coroutineDispatcher = dispatcher) {
    override suspend fun execute(parameters: String): Book {
        return withContext(dispatcher) {
            booksRepository.getBookByID(parameters)
        }
    }

}