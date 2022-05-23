package parser.xml.view

import ast.view.CheckBoxNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

fun XmlPullParser.checkBox(): CheckBoxNode {
    val viewAttributes = viewAttributes()
    val text = getAttributeValue(null, "android:text")
    val checked = getAttributeValue(null, "android:checked")?.toBoolean() ?: false
    return CheckBoxNode(viewAttributes, text, checked)
}
