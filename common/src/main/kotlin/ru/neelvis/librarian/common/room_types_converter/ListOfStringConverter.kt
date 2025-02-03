package ru.neelvis.librarian.common.room_types_converter

import androidx.room.TypeConverter

class ListOfStringConverter {
    private val separator: String = "%%"

    @TypeConverter
    fun stringToList(value: String): List<String> = value.split(separator)

    @TypeConverter
    fun StringListToString(value: List<String>): String = value.joinToString(separator)
}