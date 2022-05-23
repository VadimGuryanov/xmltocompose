package parser.xml.view

import ast.view.SwitchNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

fun XmlPullParser.switch(): SwitchNode {
    val viewAttributes = viewAttributes()
    val checked = getAttributeValue(null, "android:checked")?.toBoolean() ?: false

    return SwitchNode(viewAttributes, checked)
}
