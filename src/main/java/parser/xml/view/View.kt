package parser.xml.view

import ast.view.ViewNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.viewAttributes

fun XmlPullParser.view(): ViewNode {
    val viewAttributes = viewAttributes()
    assertEndTagNext()
    return ViewNode(viewAttributes)
}
