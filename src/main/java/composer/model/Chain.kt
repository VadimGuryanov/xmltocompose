package composer.model

import ast.Node
import ast.values.Constraints

data class Chain(
    val direction: Direction,
    val head: Node,
    val elements: Set<Node>,
    val style: Constraints.Chain.Style? = null
) {
    enum class Direction {
        HORIZONTAL,
        VERTICAL
    }
}
