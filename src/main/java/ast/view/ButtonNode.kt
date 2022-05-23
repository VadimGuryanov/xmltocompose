package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class ButtonNode(
    override val view: ViewAttributes,
    val text: String
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitButton(this)
}
