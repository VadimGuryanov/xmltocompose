package parser.xml.values

import ast.values.Size
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.size(name: String): Size? {
    val value = getAttributeValue(null, name)

    try {
        return when {
            value == null -> null
            value.endsWith("dp") -> Size.Dp(Integer.parseInt(value.substring(0, value.length - 2)))
            value.endsWith("sp") -> Size.Sp(Integer.parseInt(value.substring(0, value.length - 2)))
            else -> throw Exception("Unknown size value: $value")
        }
    } catch (e: java.lang.NumberFormatException) {
        throw Exception("Cannot parse layout size: $value")
    }
}
