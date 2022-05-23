package parser.xml.view

import ast.view.EditTextNode
import org.xmlpull.v1.XmlPullParser
import parser.xml.values.color
import parser.xml.values.inputType
import parser.xml.viewAttributes

fun XmlPullParser.editText(): EditTextNode {
    val text = getAttributeValue(null, "android:text") ?: ""
    val hint = getAttributeValue(null, "android:hint") ?: ""
    val textColorHint = color("android:textColorHint")

    return EditTextNode(
        view = viewAttributes(),
        text = text,
        inputType = inputType(),
        hint = hint,
        textColorHint = textColorHint
    )
}
