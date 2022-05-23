package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class SwitchNode(
    override val view: ViewAttributes,
    val checked: Boolean
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitSwitch(this)
}
