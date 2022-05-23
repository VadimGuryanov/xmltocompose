package parser.xml.values

import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.integer(name: String): Int? {
    val value = getAttributeValue(null, name)

    return try {
        value?.let { Integer.parseInt(it) }
    } catch (e: NumberFormatException) {
        throw Exception("Could not parse integer: $value", e)
    }
}
