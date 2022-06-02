package parser.xml.view

import ast.view.BottomNavNode
import org.xmlpull.v1.XmlPullParser
import parser.FilesRemember
import parser.xml.util.assertEndTagNext
import parser.xml.values.*
import parser.xml.viewAttributes

fun XmlPullParser.bottomNavView(): BottomNavNode {
    val viewAttributes = viewAttributes()
    val itemIconTint = color("app:itemIconTint")
    val itemTextColor = color("app:itemTextColor")
    val labelVisibilityMode = getAttributeValue(null, "app:labelVisibilityMode")?.let {
        when (it) {
            "labeled" -> true
            else -> false
        }
    } ?: false
    val menu = getAttributeValue(null, "app:menu")?.let {
        FilesRemember.addNewFile(it)
        it.substring(6, it.length)
    }
    assertEndTagNext()
    return BottomNavNode(viewAttributes, itemIconTint, itemTextColor, labelVisibilityMode, menu)
}
