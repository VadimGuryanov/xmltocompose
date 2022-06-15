package parser.xml.theme

import ast.theme.ColorNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

fun XmlPullParser.colorResource(): ColorNode {
    return ColorNode(
        view = viewAttributes(),
        name = getAttributeValue(null, "name"),
        value = nextText()
    )
}
