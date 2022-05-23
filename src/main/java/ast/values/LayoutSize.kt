package ast.values

sealed class LayoutSize {
    object MatchParent : LayoutSize()
    object WrapContent : LayoutSize()
    data class Absolute(val size: Size) : LayoutSize()
}
