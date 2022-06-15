package parser.xml.theme

import ast.theme.ResourceNode
import ast.values.Drawable
import ast.view.ItemNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.values.drawable
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.resource(): ResourceNode {
    return ResourceNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
