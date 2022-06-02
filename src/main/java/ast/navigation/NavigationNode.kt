package ast.navigation

import ast.ViewGroupNode
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.attributes.ViewGroupAttributes

data class NavigationNode(
    override val view: ViewAttributes,
    override val viewGroup: ViewGroupAttributes,
    val startDestination: String
) : ViewGroupNode {
    override fun accept(visitor: Visitor) = visitor.visitNavigate(this)
}
