package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Drawable

data class ItemNode(
    override val view: ViewAttributes,
    val icon: Drawable? = null,
    val title: String? = null,
    val name: String? = null,
    val value: String? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitMenuItem(this)
}
