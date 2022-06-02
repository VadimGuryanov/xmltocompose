package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Color
import ast.values.Drawable

data class ToolbarNode(
    override val view: ViewAttributes,
    val title: String,
    val navigationIcon: Drawable? = null,
    val titleTextColor: Color? = null,
    val menu: String? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitToolbarView(this)
}
