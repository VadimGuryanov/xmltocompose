package parser.xml.navigation

import ast.navigation.ArgumentNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

fun XmlPullParser.argument(): ArgumentNode {
    val name = getAttributeValue(null, "android:name")
    val defaultValue = getAttributeValue(null, "android:defaultValue") ?: "@null"
    val argType = getAttributeValue(null, "app:argType") ?: "string"
    val nullable = getAttributeValue(null, "app:nullable").toBoolean()
    return ArgumentNode(
        view = viewAttributes(),
        name = name,
        defaultValue = defaultValue,
        argType = argType,
        nullable = nullable
    )
}
