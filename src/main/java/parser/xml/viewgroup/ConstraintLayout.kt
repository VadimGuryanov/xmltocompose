package parser.xml.viewgroup

import ast.viewgroup.ConstraintLayoutNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.constraintLayout(): ConstraintLayoutNode {
    return ConstraintLayoutNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
