package ast.viewgroup

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class CardViewNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes = ViewGroupAttributes(listOf())
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitCardView(this)
}
