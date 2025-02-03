package ru.neelvis.librarian.core.domain.usecases;

import javax.annotation.processing.Generated;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import ru.neelvis.librarian.core.domain.repository.BooksRepository;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
        value = "dagger.internal.codegen.ComponentProcessor",
        comments = "https://dagger.dev"
)
@SuppressWarnings({
        "unchecked",
        "rawtypes",
        "KotlinInternal",
        "KotlinInternalInJava",
        "cast",
        "deprecation",
        "nullness:initialization.field.uninitialized"
})
public final class GetAllBooksUseCase_Factory implements Factory<GetAllBooksUseCase> {
    private final Provider<BooksRepository> booksRepositoryProvider;

    public GetAllBooksUseCase_Factory(Provider<BooksRepository> booksRepositoryProvider) {
        this.booksRepositoryProvider = booksRepositoryProvider;
    }

    public static GetAllBooksUseCase_Factory create(
            javax.inject.Provider<BooksRepository> booksRepositoryProvider) {
        return new GetAllBooksUseCase_Factory(Providers.asDaggerProvider(booksRepositoryProvider));
    }

    public static GetAllBooksUseCase_Factory create(
            Provider<BooksRepository> booksRepositoryProvider) {
        return new GetAllBooksUseCase_Factory(booksRepositoryProvider);
    }

    public static GetAllBooksUseCase newInstance(BooksRepository booksRepository) {
        return new GetAllBooksUseCase(booksRepository);
    }

    @Override
    public GetAllBooksUseCase get() {
        return newInstance(booksRepositoryProvider.get());
    }
}
