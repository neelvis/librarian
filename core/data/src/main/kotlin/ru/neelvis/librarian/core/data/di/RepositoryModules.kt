package ru.neelvis.librarian.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.neelvis.librarian.core.data.repository.AuthRepositoryImpl
import ru.neelvis.librarian.core.data.repository.BooksRepositoryImpl
import ru.neelvis.librarian.core.domain.repository.AuthRepository
import ru.neelvis.librarian.core.domain.repository.BooksRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BooksRepositoryModule {
    @Singleton
    @Binds
    internal abstract fun bindBooksRepository(repository: BooksRepositoryImpl): BooksRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Singleton
    @Binds
    internal abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository
}
