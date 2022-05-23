package ast.viewgroup

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes
import ast.values.Orientation

data class LinearLayoutNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes,
    val orientation: Orientation = Orientation.Vertical
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitLinearLayout(this)
}
