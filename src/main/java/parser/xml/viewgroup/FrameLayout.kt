package parser.xml.viewgroup

import ast.viewgroup.FrameLayoutNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.frameLayout(): FrameLayoutNode {
    return FrameLayoutNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes()
    )
}
