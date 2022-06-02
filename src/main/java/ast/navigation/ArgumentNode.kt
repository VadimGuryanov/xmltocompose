package ast.navigation

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes

data class ArgumentNode(
    override val view: ViewAttributes,
    val name: String,
    val defaultValue: String,
    val argType: String,
    val nullable: Boolean = false
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitArguments(this)
}
