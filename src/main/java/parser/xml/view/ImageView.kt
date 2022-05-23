package parser.xml.view

import ast.view.ImageViewNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.util.assertEndTagNext
import parser.xml.values.drawable
import parser.xml.viewAttributes

fun XmlPullParser.imageView(): ImageViewNode {
    val viewAttributes = viewAttributes()
    val compatSrc = drawable("app:srcCompat")
    val src = drawable("android:src")

    assertEndTagNext()

    return ImageViewNode(viewAttributes, compatSrc ?: src)
}
