package parser.xml.values

import ast.values.Color
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.color(name: String): Color? {
    val raw = getAttributeValue(null, name)
    return when {
        raw == null -> null
        raw.startsWith("#") ->
            try {
                val colorAsString = raw.substring(1)
                val colorToParse = if (colorAsString.length == 6) {
                    "ff$colorAsString"
                } else {
                    colorAsString
                }

                Color.Absolute(colorToParse.toLong(16))
            } catch (e: NumberFormatException) {
                throw Exception("Could not parse color: $raw", e)
            }
        raw.startsWith("@color/") -> Color.Resource(name = raw.removePrefix("@color/"))
        else -> throw Exception("Unknown color format: $raw")
    }
}
