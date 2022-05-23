package ast.values

sealed class Size {
    data class Dp(val value: Int) : Size()
    data class Sp(val value: Int) : Size()
}
