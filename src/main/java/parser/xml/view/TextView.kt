package parser.xml.view

import ast.view.TextViewNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.values.color
import parser.xml.values.integer
import parser.xml.values.size
import parser.xml.viewAttributes

fun XmlPullParser.textView(): TextViewNode {
    val viewAttributes = viewAttributes()
    val text = getAttributeValue(null, "android:text") ?: ""
    val textColor = color("android:textColor")
    val textSize = size("android:textSize")
    val maxLines = integer("android:maxLines")
    assertEndTagNext()
    return TextViewNode(viewAttributes, text, textColor, textSize, maxLines)
}
