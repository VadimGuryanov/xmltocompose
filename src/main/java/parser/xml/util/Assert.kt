package parser.xml.util

import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.assertEndTagNext() {
    if (next() != XmlPullParser.END_TAG) {
        throw Exception("Expected END_TAG event, but got $eventType")
    }
}
