package ast.view

import ast.Node
import ast.Visitor
import ast.attributes.ViewAttributes
import ast.values.Drawable

data class ImageViewNode(
    override val view: ViewAttributes,
    val src: Drawable?,
    val srcContent: String = ""
): Node {
    override fun accept(visitor: Visitor) = visitor.visitImageView(this)
}
