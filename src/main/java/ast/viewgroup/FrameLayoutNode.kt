package ast.viewgroup

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class FrameLayoutNode(
    override val viewGroup: ViewGroupAttributes,
    override val view: ViewAttributes
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitFrameLayout(this)
}
