package parser.xml.view

import ast.values.LayoutManagerType
import ast.view.RecyclerNode
import org.xmlpull.v1.XmlPullParser
import parser.FilesRemember
import parser.xml.util.assertEndTagNext
import parser.xml.values.*
import parser.xml.viewAttributes

fun XmlPullParser.recyclerView(): RecyclerNode {
    val viewAttributes = viewAttributes()
    val layoutManager = layoutManager()
    val orientation = orientation()
    val spanCount = when (layoutManager) {
        LayoutManagerType.GRID -> getAttributeValue(null, "app:spanCount")
        LayoutManagerType.LINEAR -> null
    }
    val listItem = getAttributeValue(null, "tools:listitem")?.let {
        FilesRemember.addNewFile(it)
        it.substring(8, it.length)
    }
    assertEndTagNext()
    return RecyclerNode(viewAttributes, orientation, layoutManager, spanCount?.toInt(), listItem)
}
