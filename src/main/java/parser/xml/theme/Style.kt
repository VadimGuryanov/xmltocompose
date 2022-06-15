package parser.xml.theme

import ast.theme.StyleNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.style(): StyleNode {
    val name = getAttributeValue(null, "name")
    val parent = getAttributeValue(null, "parent")
    return StyleNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes(),
        name = name,
        parent = parent
    )
}
