package parser.xml.values

import ast.values.Constraints
import parser.xml.*
import org.xmlpull.v1.XmlPullParser

fun XmlPullParser.constraints(): Constraints {
    return Constraints(
        relative = Constraints.RelativePositioning(
            bottomToBottom = constraintId("app:layout_constraintBottom_toBottomOf"),
            bottomToTop = constraintId("app:layout_constraintBottom_toTopOf"),
            endToEnd = constraintId("app:layout_constraintEnd_toEndOf"),
            endToStart = constraintId("app:layout_constraintEnd_toStartOf"),
            leftToLeft = constraintId("app:layout_constraintLeft_toLeftOf"),
            leftToRight = constraintId("app:layout_constraintLeft_toRightOf"),
            rightToLeft = constraintId("app:layout_constraintRight_toLeftOf"),
            rightToRight = constraintId("app:layout_constraintRight_toRightOf"),
            startToEnd = constraintId("app:layout_constraintStart_toEndOf"),
            startToStart = constraintId("app:layout_constraintStart_toStartOf"),
            topToBottom = constraintId("app:layout_constraintTop_toBottomOf"),
            topToTop = constraintId("app:layout_constraintTop_toTopOf")
        ),
        chain = Constraints.Chain(
            horizontalStyle = chainStyle("app:layout_constraintHorizontal_chainStyle"),
            verticalStyle = chainStyle("app:layout_constraintVertical_chainStyle")
        )
    )
}

private fun XmlPullParser.constraintId(name: String): Constraints.Id? {
    return if (getAttributeValue(null, name) == "parent") {
        Constraints.Id.Parent
    } else {
        id(name)?.let { Constraints.Id.View(it) }
    }
}

private fun XmlPullParser.chainStyle(name: String): Constraints.Chain.Style? {
    return when (val value = getAttributeValue(null, name)) {
        null -> null
        "spread" -> Constraints.Chain.Style.SPREAD
        "spread_inside" -> Constraints.Chain.Style.SPREAD_INSIDE
        "packed" -> Constraints.Chain.Style.PACKED
        else -> throw Exception("Unknown chain style: $value")
    }
}
