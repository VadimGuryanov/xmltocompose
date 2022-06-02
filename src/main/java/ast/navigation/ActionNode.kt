package ast.navigation

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class ActionNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitAction(this)
}
