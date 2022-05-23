package ast.attributes

import ast.values.Constraints
import ast.values.Drawable
import ast.values.LayoutSize
import ast.values.Padding

data class ViewAttributes(
    val id: String? = null,
    val width: LayoutSize,
    val height: LayoutSize,
    val padding: Padding = Padding(),
    val background: Drawable? = null,
    val constraints: Constraints = Constraints()
)
