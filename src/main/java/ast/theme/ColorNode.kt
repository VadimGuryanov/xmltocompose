package ast.theme

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class ColorNode(
    override val view: ViewAttributes,
    val name: String,
    val value: String,
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitColor(this)
}

