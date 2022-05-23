package parser.xml.viewgroup

import ast.values.Orientation
import ast.viewgroup.LinearLayoutNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.values.orientation
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.linearLayout(): LinearLayoutNode {
    return LinearLayoutNode(
        view = viewAttributes(),
        orientation = orientation() ?: Orientation.Vertical,
        viewGroup = viewGroupAttributes()
    )
}
