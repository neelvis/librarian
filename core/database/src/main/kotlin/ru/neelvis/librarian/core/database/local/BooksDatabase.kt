package ru.neelvis.librarian.core.database.local

import androidx.room.Database
import androidx.room.Fts4
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.neelvis.librarian.common.room_types_converter.ListOfStringConverter

@Fts4
@Database(entities = [LocalBookEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListOfStringConverter::class)
abstract class BooksDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}