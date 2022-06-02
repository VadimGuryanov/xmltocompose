package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.LayoutManagerType
import ast.values.Orientation

data class RecyclerNode(
    override val view: ViewAttributes,
    val orientation: Orientation? = Orientation.Vertical,
    val layoutManager: LayoutManagerType? = LayoutManagerType.LINEAR,
    val spanCount: Int? = null,
    val listItem: String? = null
) : Node {
    override fun accept(visitor: Visitor) = visitor.visitRecyclerView(this)
}
