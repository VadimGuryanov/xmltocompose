package utils

fun String.getMainFolder(): String = "$this/app/src/main/java"

fun String.getFileName(): String {
    val words = this
        .substring(0, this.length - 4)
        .split("_")
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_NAME)
    }
}

private const val DEFAULT_NAME = "Composed.kt"
