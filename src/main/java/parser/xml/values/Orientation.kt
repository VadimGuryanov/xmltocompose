package parser.xml.values

import ast.values.Orientation
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.orientation(): Orientation? {
    return when (val value = getAttributeValue(null, "android:orientation")) {
        "vertical" -> Orientation.Vertical
        "horizontal" -> Orientation.Horizontal
        null -> null
        else -> throw Exception("Unknown orientation: $value")
    }
}
