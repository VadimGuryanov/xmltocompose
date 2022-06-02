package parser.xml.navigation

import ast.navigation.NavigationNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.navigation(): NavigationNode {
    val startDestination = getAttributeValue(null, "app:startDestination") ?: ""
    return NavigationNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes(),
        startDestination = startDestination
    )
}
