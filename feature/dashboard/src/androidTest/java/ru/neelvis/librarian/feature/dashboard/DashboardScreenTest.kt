package ru.neelvis.librarian.feature.dashboard

import DashboardScreen
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.neelvis.librarian.core.model.Book
import ru.neelvis.librarian.feature.dashboard.viewmodels.SelectionMode

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<DashboardTestActivity>()

    private fun getBooks(count: Int) = List(count) { Book(id = it.toString(), title = "Book $it") }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun grid_displays_books() {
        val books = getBooks(10)
        composeTestRule.setContent {
            DashboardScreen(
                books = books,
                onBookClicked = {},
                onBookLongClicked = {},
                onDeleteSelectedClick = {},
                onClearSelectionClick = {},
                selectionMode = SelectionMode.VIEW,
                selectedBooks = setOf()
            )
        }
        books.forEach { book ->
            composeTestRule.onNodeWithText(book.title).assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun enter_selection_mode_and_select_books() {
        val books = getBooks(5)
        var selectionMode = mutableStateOf(SelectionMode.VIEW)
        var selectedBooks = mutableStateOf(setOf<Book>())
        composeTestRule.setContent {
            DashboardScreen(
                books = books,
                onBookClicked = {
                    if (selectionMode.value == SelectionMode.SELECT) {
                        selectedBooks.value = if (selectedBooks.value.contains(it)) selectedBooks.value - it else selectedBooks.value + it
                    }
                },
                onBookLongClicked = {
                    selectionMode.value = SelectionMode.SELECT
                    selectedBooks.value = selectedBooks.value + it
                },
                onDeleteSelectedClick = {},
                onClearSelectionClick = {
                    selectedBooks.value = setOf()
                    selectionMode.value = SelectionMode.VIEW
                },
                selectionMode = selectionMode.value,
                selectedBooks = selectedBooks.value
            )
        }
        // Long click to enter selection mode
        composeTestRule.onNodeWithText(books[0].title).performTouchInput { longClick() }
        composeTestRule.waitForIdle()
        // Tap to select another book
        composeTestRule.onNodeWithText(books[1].title).performClick()
        composeTestRule.waitForIdle()
        // Check both are selected
        composeTestRule.onNodeWithText("Selected").assertExists()
        // Tap again to deselect
        composeTestRule.onNodeWithText(books[1].title).performClick()
        composeTestRule.waitForIdle()
        // Only one selected
        composeTestRule.onAllNodesWithText("Selected").assertCountEquals(1)
        // Clear selection
        composeTestRule.onNodeWithText("Clear Selection").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Selected").assertCountEquals(0)
    }

    // Swipe selection test (simulate drag gesture)
    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun swipe_selects_multiple_books() {
        val books = getBooks(5)
        var selectionMode = mutableStateOf(SelectionMode.SELECT)
        var selectedBooks = mutableStateOf(setOf<Book>())
        composeTestRule.setContent {
            DashboardScreen(
                books = books,
                onBookClicked = {
                    if (selectionMode.value == SelectionMode.SELECT) {
                        selectedBooks.value = if (selectedBooks.value.contains(it)) selectedBooks.value - it else selectedBooks.value + it
                    }
                },
                onBookLongClicked = {},
                onDeleteSelectedClick = {},
                onClearSelectionClick = {
                    selectedBooks.value = setOf()
                    selectionMode.value = SelectionMode.VIEW
                },
                selectionMode = selectionMode.value,
                selectedBooks = selectedBooks.value
            )
        }
        // Simulate swipe by performing drag across book nodes
        val node1 = composeTestRule.onNodeWithText(books[0].title)
        val node2 = composeTestRule.onNodeWithText(books[1].title)
        node1.performTouchInput { down(center); moveTo(node2.fetchSemanticsNode().positionInRoot + node2.fetchSemanticsNode().size.center.toOffset()); up() }
        composeTestRule.waitForIdle()
        // Both should be selected
        composeTestRule.onAllNodesWithText("Selected").assertCountEquals(2)
    }
} 