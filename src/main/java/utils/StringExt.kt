package utils

fun String.getMainFolder(): String = "$this/app/src/main/java/$DEFAULT_PACKAGE"

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

fun String.getFileTheme(): String {
    val words = this
        .substring(0, this.length - 4)
        .split('/')
        .last()
        .split('_')
    return buildString {
        words.forEach { append(it.capitalize()) }
        append(DEFAULT_THEME)
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

fun String.getColorName(): String = this.let {
    return@let buildString {
        it.split("_").forEach { str ->
            append(str.capitalize())
        }
    }
}

private const val DEFAULT_NAME = "Composed.kt"
private const val DEFAULT_THEME = "Theme"
private const val DEFAULT_SHORT_NAME = "Composed"
private const val DEFAULT_SCREEN_NAME = "Screen"
//private const val DEFAULT_PACKAGE = "kpfu/itis/petproject"
//const val DEFAULT_PACKAGE_2 = "kpfu.itis.petproject"
private const val DEFAULT_PACKAGE = "test/plugin/project"
const val DEFAULT_PACKAGE_2 = "test.plugin.project"
const val DEFAULT_FILE_PACKAGE = "${DEFAULT_PACKAGE_2}.xml2compose"

