package parser.xml.viewgroup

import ast.viewgroup.UnknownNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.unknown(): UnknownNode {
    return UnknownNode(
        name = name,
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
