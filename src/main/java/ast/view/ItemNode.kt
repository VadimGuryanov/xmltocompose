package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Drawable

data class ItemNode(
    override val view: ViewAttributes,
    val icon: Drawable?,
    val title: String?
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitMenuItem(this)
}
