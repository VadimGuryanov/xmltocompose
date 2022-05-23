package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class ViewNode(
    override val view: ViewAttributes
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitView(this)
}
