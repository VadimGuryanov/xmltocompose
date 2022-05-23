package ast.values

sealed class Color {

    data class Absolute(val value: Long): Color()

    data class Resource(val name: String): Color()
}
