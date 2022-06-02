package parser.xml.values

import ast.values.LayoutManagerType
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.layoutManager(): LayoutManagerType {
    val value = getAttributeValue(null, "app:layoutManager")
    return value?.let {
        when {
            it.contains("LinearLayoutManager") -> LayoutManagerType.LINEAR
            it.contains("GridLayoutManager") -> LayoutManagerType.GRID
            else -> LayoutManagerType.LINEAR
        }
    } ?: LayoutManagerType.LINEAR
}
