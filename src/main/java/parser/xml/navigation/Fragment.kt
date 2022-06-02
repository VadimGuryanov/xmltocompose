package parser.xml.navigation

import ast.navigation.FragmentNode
import org.xmlpull.v1.XmlPullParser
import parser.FilesRemember
import parser.xml.viewAttributes
import parser.xml.viewGroupAttributes

fun XmlPullParser.fragment(): FragmentNode {
    val id = getAttributeValue(null, "android:id")
    val name = getAttributeValue(null, "android:name")
    val label = getAttributeValue(null, "android:label")
    val layout = getAttributeValue(null, "tools:layout")?.let {
        FilesRemember.addNewFile(it)
        it.substring(8, it.length)
    }
    return FragmentNode(
        view = viewAttributes(),
        viewGroup = viewGroupAttributes(),
        id = id, name = name,
        label = label, layout = layout
    )
}