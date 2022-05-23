package parser.xml.values

import ast.values.Padding
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.padding(): Padding {
    return Padding(
        all = size("android:padding"),
        left = size("android:paddingLeft"),
        right = size("android:paddingRight"),
        start = size("android:paddingStart"),
        end = size("android:paddingEnd"),
        top = size("android:paddingTop"),
        bottom = size("android:paddingBottom"),
        horizontal = size("android:paddingHorizontal"),
        vertical = size("android:paddingVertical")
    )
}
