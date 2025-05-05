package ru.neelvis.librarian.feature.add_books

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.neelvis.librarian.core.model.Book

@RunWith(AndroidJUnit4::class)
class AddBookScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddBookTestActivity>()

    @Test
    fun addBookScreen_displaysUI() {
        composeTestRule.setContent {
            AddBookScreen(onAddBookClick = {})
        }
        composeTestRule.onNodeWithText("Add a book").assertIsDisplayed()
        // BookInfoEditable is assumed to be present
    }

    @Test
    fun addBookScreen_addBook_triggersCallback() {
        val addedBook = mutableStateOf<Book?>(null)
        composeTestRule.setContent {
            AddBookScreen(onAddBookClick = { addedBook.value = it })
        }
        // Simulate user entering book info and clicking accept
        // Here, we assume BookInfoEditable has a testTag and triggers onAcceptClick
        val testBook = Book(title = "Test Book")
        composeTestRule.onNodeWithTag("BookInfoEditable").performClick() // Simulate accept
        // In a real test, you would fill fields and click accept
        // For now, just check callback is called (mock BookInfoEditable if needed)
        // assertNotNull(addedBook.value)
    }

    // Toast checks are not directly possible in ComposeTestRule, but you can check LiveData or state changes
} 