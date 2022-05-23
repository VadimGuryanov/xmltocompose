package ast.viewgroup

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class UnknownNode(
    val name: String,
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitUnknown(this)
}
