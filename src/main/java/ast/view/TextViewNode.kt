package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Color
import ast.values.Size

data class TextViewNode(
    override val view: ViewAttributes,
    val text: String,
    val textColor: Color? = null,
    val textSize: Size? = null,
    val maxLines: Int? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitTextView(this)
}
