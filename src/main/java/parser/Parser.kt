package parser

import ast.Layout
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import parser.xml.layout
import java.io.BufferedReader

class Parser {

    @Throws(Exception::class)
    fun parse(text: String): Layout {
        return parse(text.reader().buffered())
    }

    @Throws(Exception::class)
    fun parse(reader: BufferedReader): Layout {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(reader)
        return parse(parser)
    }

    @Throws(Exception::class)
    fun parse(parser: XmlPullParser): Layout {
        try {
            return parser.layout()
        } catch (e: XmlPullParserException) {
            throw Exception("Invalid XML: ${e.message}", e)
        }
    }
}
