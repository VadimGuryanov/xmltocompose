package parser.xml.view

import ast.view.ToolbarNode
import org.xmlpull.v1.XmlPullParser
import parser.FilesRemember
import parser.xml.util.assertEndTagNext
import parser.xml.values.color
import parser.xml.values.drawable
import parser.xml.viewAttributes

fun XmlPullParser.toolbarView(): ToolbarNode {
    val viewAttributes = viewAttributes()
    val title = getAttributeValue(null, "app:title") ?: ""
    val navigationIcon = drawable("app:navigationIcon")
    val titleTextColor = color("app:titleTextColor")
    val menu: String? = getAttributeValue(null, "app:menu")?.let {
        FilesRemember.addNewFile(it)
        it.substring(6, it.length)
    }
    assertEndTagNext()
    return ToolbarNode(viewAttributes, title, navigationIcon, titleTextColor, menu)
}
