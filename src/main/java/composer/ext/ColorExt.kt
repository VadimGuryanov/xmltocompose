package composer.ext

fun String.color(): String = if (this.contains("@color/")) {
    "Colors.${this.substring(7, this.length).capitalize()}"
} else this

