package ru.neelvis.librarian.feature.add_books.viewmodels

import android.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.neelvis.librarian.common.events.BookEvent
import ru.neelvis.librarian.core.domain.usecases.AddBookUseCase
import ru.neelvis.librarian.core.model.Book

@OptIn(ExperimentalCoroutinesApi::class)
class AddBookViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addBookUseCase: AddBookUseCase
    private lateinit var viewModel: AddBookViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        addBookUseCase = mockk()
        viewModel = AddBookViewModel(addBookUseCase)
    }

    @Test
    fun `addBookToList emits BookAdded event`() = runTest {
        viewModel.isBookAdded.test {
            viewModel.addBookToList(Book(id = "1", title = "Test"))
            assertEquals(BookEvent.BOOK_ADDED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}