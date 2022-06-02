package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Color

data class BottomNavNode(
    override val view: ViewAttributes,
    val itemIconTint: Color? = null,
    val itemTextColor: Color? = null,
    val labelVisibilityMode: Boolean = false,
    val menu: String? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitBottomNavView(this)
}
