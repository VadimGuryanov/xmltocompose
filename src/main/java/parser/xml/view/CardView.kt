package parser.xml.view

import ast.viewgroup.CardViewNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.cardView(): CardViewNode {
    return CardViewNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
