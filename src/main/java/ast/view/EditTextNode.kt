package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Color
import ast.values.InputType

data class EditTextNode(
    override val view: ViewAttributes,
    val text: String,
    val inputType: InputType,
    val hint: String,
    val textColorHint: Color? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitEditText(this)
}
