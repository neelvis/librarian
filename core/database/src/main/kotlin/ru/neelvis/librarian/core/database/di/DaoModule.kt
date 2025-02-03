package ru.neelvis.librarian.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.neelvis.librarian.core.database.local.BookDao
import ru.neelvis.librarian.core.database.local.BooksDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    fun providesBookDao(
        database: BooksDatabase,
    ): BookDao = database.bookDao()
}