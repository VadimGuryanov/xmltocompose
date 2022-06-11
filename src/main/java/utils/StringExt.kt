package utils

fun String.getMainFolder(): String = "$this/app/src/main/java/test/plugin/project"

fun String.getResFolder(): String = "$this/app/src/main/res"

fun String.getFileName(): String {
    val words = this
        .substring(0, this.length - 4)
        .split('/')
        .last()
        .split('_')
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_NAME)
    }
}

fun String.getShortFileName(): String {
    val words = this
        .split('_')
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_SHORT_NAME)
    }
}

fun String.getMenuName(): String {
    val words = this
        .split('_')
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_SHORT_NAME)
    }
}

fun String.getScreenName(): String {
    val words = this
        .split('_')
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_SCREEN_NAME)
    }
}

private const val DEFAULT_NAME = "Composed.kt"
private const val DEFAULT_SHORT_NAME = "Composed"
private const val DEFAULT_SCREEN_NAME = "Screen"
