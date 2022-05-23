package ast.values

data class Constraints(
    val relative: RelativePositioning = RelativePositioning(),
    val chain: Chain = Chain()
) {
    data class RelativePositioning(
        val bottomToBottom: Id? = null,
        val bottomToTop: Id? = null,
        val endToEnd: Id? = null,
        val endToStart: Id? = null,
        val leftToLeft: Id? = null,
        val leftToRight: Id? = null,
        val rightToLeft: Id? = null,
        val rightToRight: Id? = null,
        val startToEnd: Id? = null,
        val startToStart: Id? = null,
        val topToBottom: Id? = null,
        val topToTop: Id? = null
    )

    data class Chain(
        val horizontalStyle: Style? = null,
        val verticalStyle: Style? = null
    ) {
        enum class Style {
            SPREAD,
            SPREAD_INSIDE,
            PACKED
        }
    }

    sealed class Id {
        data class View(val id: String): Id()
        object Parent : Id()
    }
}
