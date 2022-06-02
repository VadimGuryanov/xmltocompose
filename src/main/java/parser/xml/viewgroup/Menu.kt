package parser.xml.viewgroup

import ast.viewgroup.MenuNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.menu(): MenuNode {
    return MenuNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
