package parser.xml.view

import org.xmlpull.v1.XmlPullParser
import parser.xml.viewAttributes

/**
 * Parses a `<TextView>` element.
 *
 * https://developer.android.com/reference/kotlin/android/widget/TextView
 */
internal fun XmlPullParser.textView(): TextViewNode {
    val viewAttributes = viewAttributes()
    val text = getAttributeValue(null, "android:text") ?: ""
    val textColor = color("android:textColor")
    val textSize = size("android:textSize")
    val maxLines = integer("android:maxLines")

    assertEndTagNext()

    return TextViewNode(
        viewAttributes,
        text,
        textColor,
        textSize,
        maxLines
    )
}