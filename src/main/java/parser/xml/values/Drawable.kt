package parser.xml.values

import ast.values.Color
import ast.values.Drawable
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.drawable(name: String): Drawable? {
    val value = getAttributeValue(null, name)

    return when {
        value == null -> null

        value.startsWith("#") -> color(name)?.let { Drawable.ColorValue(it) }

        value.startsWith("@color/") -> color(name)?.let {
            Drawable.ColorValue(Color.Resource(name = value.substring(7)))
        }

        value.startsWith("@drawable/") -> Drawable.Resource(name = value.substring(10))

        value.startsWith("@android:drawable/") -> Drawable.AndroidResource(name = value.substring(18))

        value.startsWith("?") -> Drawable.StyleAttribute(name = value.substring(1))

        else -> throw Exception("Unknown drawable format: $value")
    }
}
