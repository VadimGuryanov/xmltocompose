package parser.xml.values

import ast.values.InputType
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.inputType(): InputType {
    return when (val value = getAttributeValue(null, "android:inputType")) {
        "text" -> InputType.Text
        "number" -> InputType.Number
        "phone" -> InputType.Phone
        "textUri" -> InputType.Uri
        "textEmailAddress" -> InputType.Email
        "textPassword" -> InputType.Password
        "numberPassword" -> InputType.NumberPassword
        null -> InputType.Text
        else -> throw Exception("Unknown inputType: $value")
    }
}
