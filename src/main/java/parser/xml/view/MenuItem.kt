package parser.xml.view

import ast.values.Drawable
import ast.view.ItemNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.values.drawable
import parser.xml.viewAttributes

fun XmlPullParser.item(): ItemNode {
    val viewAttributes = viewAttributes()
    val icon: Drawable? = drawable("android:icon")
    val title: String? = getAttributeValue(null, "android:title")
    assertEndTagNext()
    return ItemNode(viewAttributes, icon, title)
}
