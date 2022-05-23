package parser.xml

import ast.Layout
import ast.Node
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.START_DOCUMENT
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT

fun XmlPullParser.layout(): Layout {
    val children = mutableListOf<Node>()

    var event = eventType
    while (event != END_DOCUMENT) {
        when (event) {
            START_DOCUMENT -> Unit
            START_TAG -> {
                val child = node()
                children.add(child)
            }
        }

        event = next()
    }

    return Layout(children)
}