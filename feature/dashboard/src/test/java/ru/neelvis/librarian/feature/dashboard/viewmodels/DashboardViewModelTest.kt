package ru.neelvis.librarian.feature.dashboard.viewmodels

import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.domain.usecases.GetAllBooksUseCase
import ru.neelvis.librarian.core.domain.usecases.RemoveBooksUseCase
import ru.neelvis.librarian.core.domain.usecases.UpdateBookUseCase
import ru.neelvis.librarian.core.model.Book

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var getAllBooksUseCase: GetAllBooksUseCase
    private lateinit var removeBooksUseCase: RemoveBooksUseCase
    private lateinit var updateBookUseCase: UpdateBookUseCase
    private lateinit var viewModel: DashboardViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        getAllBooksUseCase = mockk()
        removeBooksUseCase = mockk()
        updateBookUseCase = mockk()
        viewModel = DashboardViewModel(
            getAllBooksUseCase = getAllBooksUseCase,
            removeBooksUseCase = removeBooksUseCase,
            updateBookUseCase = updateBookUseCase
        )
    }

    @Test
    fun `updateBook emits BookUpdated event`() = runTest {
        viewModel.isBookUpdatedFlow.test {
            viewModel.updateBook(Book(id = "1", title = "Test"))
            assertEquals(BookEvent.BOOK_UPDATED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeBook emits BookRemoved event`() = runTest {
        viewModel.isBookRemovedFlow.test {
            viewModel.setCurrentBook(Book(id = "1", title = "Test"))
            viewModel.removeCurrentBook()
            assertEquals(BookEvent.BOOK_REMOVED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
} 