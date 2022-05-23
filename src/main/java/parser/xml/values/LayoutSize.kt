package parser.xml.values

import ast.values.LayoutSize
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.layoutSize(name: String): LayoutSize? {
    val value = getAttributeValue(null, name)
    return when {
        value == null -> null
        value == "match_parent" -> LayoutSize.MatchParent
        value == "wrap_content" -> LayoutSize.WrapContent
        else -> size(name)?.let { LayoutSize.Absolute(it) }
    }
}
