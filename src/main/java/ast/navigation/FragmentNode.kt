package ast.navigation

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class FragmentNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes,
    val id: String,
    val name: String,
    val label: String,
    val layout: String?
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitFragment(this)
}
