package ast.values

data class Padding(
    val all: Size? = null,
    val left: Size? = null,
    val right: Size? = null,
    val start: Size? = null,
    val end: Size? = null,
    val top: Size? = null,
    val bottom: Size? = null,
    val horizontal: Size? = null,
    val vertical: Size? = null
)
