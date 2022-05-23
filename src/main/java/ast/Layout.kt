package ast

data class Layout(
    val children: List<Node>
) {
    fun accept(visitor: Visitor) = visitor.visitLayout(this)
}
