package ru.neelvis.librarian.core.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.neelvis.librarian.common.usecase.SuspendUseCase
import ru.neelvis.librarian.core.domain.di.DefaultDispatcher
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) :
    SuspendUseCase<Book, Unit>(dispatcher) {
    override suspend fun execute(parameters: Book) {
        withContext(dispatcher) {
            booksRepository.addBook(
                parameters.title,
                parameters.authors,
                parameters.cover,
                parameters.isbn,
                parameters.publishedDate,
                parameters.description
            )
        }
    }
}