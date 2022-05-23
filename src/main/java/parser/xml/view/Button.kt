package parser.xml.view

import ast.view.ButtonNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.viewAttributes

fun XmlPullParser.button(): ButtonNode {
    val viewAttributes = viewAttributes()
    val text = getAttributeValue(null, "android:text") ?: ""
    assertEndTagNext()
    return ButtonNode(viewAttributes, text)
}
