package ru.neelvis.librarian.core.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.common.usecase.UseCase
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class GetAllBooksUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
) :
    UseCase<Unit, Flow<List<Book>>>() {
    override fun execute(parameters: Unit): Flow<List<Book>> {
        return booksRepository.getAllBooks()
    }
}