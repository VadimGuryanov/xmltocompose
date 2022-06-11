package ast

data class Layout(
    val children: List<Node>
) {
    fun accept(visitor: Visitor) = visitor.visitLayout(this)

    fun acceptTheme(theme: Layout, visitor: Visitor) = visitor.visitTheme(this, theme)
}
