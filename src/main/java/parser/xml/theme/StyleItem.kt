package parser.xml.theme

import ast.view.ItemForStyleNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

fun XmlPullParser.itemForStyle(): ItemForStyleNode {
    val name = getAttributeValue(null, "name")
    return ItemForStyleNode(
        view = viewAttributes(),
        name = name,
        value = nextText()
    )
}
