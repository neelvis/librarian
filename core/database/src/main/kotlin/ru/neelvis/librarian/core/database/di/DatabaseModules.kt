package ru.neelvis.librarian.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.neelvis.librarian.core.database.local.BooksDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Singleton
    @Provides
    internal fun provideDatabase(@ApplicationContext context: Context): BooksDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            BooksDatabase::class.java, "books.db"
        ).build()
}