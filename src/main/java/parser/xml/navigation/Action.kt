package parser.xml.navigation

import ast.navigation.ActionNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.action(): ActionNode {
    return ActionNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
