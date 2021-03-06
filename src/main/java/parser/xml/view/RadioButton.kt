package parser.xml.view

import ast.view.RadioButtonNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.viewAttributes

fun XmlPullParser.radioButton(): RadioButtonNode {
    val viewAttribute = viewAttributes()
    val text = getAttributeValue(null, "android:text")
    val checked = getAttributeValue(null, "android:checked")?.toBoolean() ?: false

    assertEndTagNext()
    return RadioButtonNode(viewAttribute, text, checked)
}
