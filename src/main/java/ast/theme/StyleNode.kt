package ast.theme

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class StyleNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes,
    val name: String? = null,
    val parent: String? = null,
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitStyle(this)
}
