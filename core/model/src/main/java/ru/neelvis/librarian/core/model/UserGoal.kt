package ru.neelvis.librarian.core.model

enum class UserGoal(val description: String) {
    SYSTEMIZE("Systemize my books collection"),
    LEARN("Learn about new books"),
    FIND_SIMILAR("Find more to read"),
    CHALLENGE("Get into reading challenge"),
    OTHER("Just wandering around"),
}