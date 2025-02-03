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
public final class GetBooksByTitleUseCase_Factory implements Factory<GetBooksByTitleUseCase> {
    private final Provider<BooksRepository> booksRepositoryProvider;

    public GetBooksByTitleUseCase_Factory(Provider<BooksRepository> booksRepositoryProvider) {
        this.booksRepositoryProvider = booksRepositoryProvider;
    }

    public static GetBooksByTitleUseCase_Factory create(
            javax.inject.Provider<BooksRepository> booksRepositoryProvider) {
        return new GetBooksByTitleUseCase_Factory(Providers.asDaggerProvider(booksRepositoryProvider));
    }

    public static GetBooksByTitleUseCase_Factory create(
            Provider<BooksRepository> booksRepositoryProvider) {
        return new GetBooksByTitleUseCase_Factory(booksRepositoryProvider);
    }

    public static GetBooksByTitleUseCase newInstance(BooksRepository booksRepository) {
        return new GetBooksByTitleUseCase(booksRepository);
    }

    @Override
    public GetBooksByTitleUseCase get() {
        return newInstance(booksRepositoryProvider.get());
    }
}
