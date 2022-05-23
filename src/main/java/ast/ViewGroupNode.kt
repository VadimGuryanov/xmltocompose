package ast

import ast.attributes.ViewGroupAttributes

interface ViewGroupNode : Node {
    val viewGroup: ViewGroupAttributes
}
