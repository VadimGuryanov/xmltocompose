package parser.xml

import ast.Node
import ast.attributes.ViewGroupAttributes
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.TEXT

fun XmlPullParser.viewGroupAttributes(): ViewGroupAttributes {
    val children = mutableListOf<Node>()

    while (next() != END_TAG) {
        if (eventType == TEXT) continue
        val child = node()
        children.add(child)
    }

    return ViewGroupAttributes(children)
}
