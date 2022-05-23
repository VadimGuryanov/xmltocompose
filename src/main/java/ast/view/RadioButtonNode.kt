package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class RadioButtonNode(
    override val view: ViewAttributes,
    val text: String?,
    val checked: Boolean
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitRadioButton(this)
}
