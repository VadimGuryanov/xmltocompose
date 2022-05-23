package ast

import ast.attributes.ViewAttributes

interface Node {
    val view: ViewAttributes

    fun accept(visitor: Visitor)
}
