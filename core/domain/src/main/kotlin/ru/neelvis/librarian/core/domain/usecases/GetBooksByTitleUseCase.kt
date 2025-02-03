package ru.neelvis.librarian.core.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.neelvis.librarian.common.usecase.UseCase
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import ru.neelvis.librarian.core.model.Book
import javax.inject.Inject

class GetBooksByTitleUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
) :
    UseCase<String, Flow<List<Book>>>() {
    override fun execute(parameters: String): Flow<List<Book>> {
        return booksRepository.getBooksByTitle(parameters)
    }

}