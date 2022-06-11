package composer.writer

import ast.Node
import ast.values.LayoutSize
import ast.values.Padding
import ast.values.Size
import ast.view.BottomNavNode
import ast.view.ToolbarNode
import composer.ext.getRef
import composer.ext.hasConstraints

class ModifierBuilder(
    node: Node? = null
) {
    private val modifiers = mutableListOf<Modifier>()

    init {
        if (node != null) {
            addViewModifiers(node)
        }
    }

    fun add(modifier: Modifier) {
        modifiers.add(modifier)
    }

    fun addSize(name: String, size: Size) {
        modifiers.add(
            Modifier(
                name,
                listOf(
                    CallParameter(ParameterValue.SizeValue(size))
                )
            )
        )
    }

    fun getModifiers(): List<Modifier> {
        return modifiers
    }

    fun hasModifiers(): Boolean {
        return modifiers.isNotEmpty()
    }

    private fun addViewModifiers(node: Node) {
        val view = node.view

        when (view.width) {
            is LayoutSize.Absolute ->
                if (view.width != LayoutSize.Absolute(Size.Dp(0)) || !view.constraints.hasConstraints()) {
                    addSize("width", (view.width as LayoutSize.Absolute).size)
                }
            is LayoutSize.MatchParent -> add(Modifier("fillMaxWidth"))
        }

        when (view.height) {
            is LayoutSize.Absolute ->
                if (view.height != LayoutSize.Absolute(Size.Dp(0)) || !view.constraints.hasConstraints()) {
                    addSize("height", (view.height as LayoutSize.Absolute).size)
                }
            is LayoutSize.MatchParent -> add(Modifier("fillMaxHeight"))
        }

        if (node !is ToolbarNode && node !is BottomNavNode) {
            view.background?.let { drawable ->
                add(
                    Modifier(
                        name = "background",
                        parameters = listOf(
                            CallParameter(ParameterValue.DrawableValue(drawable))
                        )
                    )
                )
            }
        }

        if (view.constraints.hasConstraints()) {
            addConstraints(node)
        }

        addPadding(view.padding)
    }

    private fun addConstraints(node: Node) {
        val constraints = node.view.constraints
        add(
            Modifier("constrainAs", listOf(CallParameter(ParameterValue.RawValue(node.getRef())))) {
                constraints.relative.bottomToBottom?.let { writeRelativePositioningConstraint("bottom", it, "bottom") }
                constraints.relative.bottomToTop?.let { writeRelativePositioningConstraint("bottom", it, "top") }
                constraints.relative.endToEnd?.let { writeRelativePositioningConstraint("end", it, "end") }
                constraints.relative.endToStart?.let { writeRelativePositioningConstraint("end", it, "start") }
                constraints.relative.leftToLeft?.let { writeRelativePositioningConstraint("left", it, "left") }
                constraints.relative.leftToRight?.let { writeRelativePositioningConstraint("left", it, "right") }
                constraints.relative.rightToLeft?.let { writeRelativePositioningConstraint("right", it, "left") }
                constraints.relative.rightToRight?.let { writeRelativePositioningConstraint("right", it, "right") }
                constraints.relative.startToEnd?.let { writeRelativePositioningConstraint("start", it, "end") }
                constraints.relative.startToStart?.let { writeRelativePositioningConstraint("start", it, "start") }
                constraints.relative.topToBottom?.let { writeRelativePositioningConstraint("top", it, "bottom") }
                constraints.relative.topToTop?.let { writeRelativePositioningConstraint("top", it, "top") }
                writeSizeConstraint("width", node.view.width)
                writeSizeConstraint("height", node.view.height)
            }
        )
    }

    private fun addPadding(padding: Padding) {
        if (padding.all != null) {
            add(
                Modifier(
                    name = "padding",
                    parameters = listOf(
                        CallParameter(ParameterValue.SizeValue(padding.all!!))
                    )
                )
            )
        }

        if (padding.horizontal != null || padding.vertical != null) {
            add(
                Modifier(
                    name = "padding",
                    parameters = listOf(
                        createCallParameter("horizontal", createSizeParameterValue(padding.horizontal)),
                        createCallParameter("vertical", createSizeParameterValue(padding.vertical))
                    )
                )
            )
        }

        if (padding.left != null || padding.right != null) {
            add(
                Modifier(
                    name = "absolutePadding",
                    parameters = listOf(
                        createCallParameter("left", createSizeParameterValue(padding.left)),
                        createCallParameter("right", createSizeParameterValue(padding.right)),
                        createCallParameter("top", createSizeParameterValue(padding.top)),
                        createCallParameter("bottom", createSizeParameterValue(padding.bottom))
                    )
                )
            )
        } else if (padding.start != null || padding.end != null || padding.top != null || padding.bottom != null) {
            Modifier(
                name = "padding",
                parameters = listOf(
                    createCallParameter("start", createSizeParameterValue(padding.start)),
                    createCallParameter("end", createSizeParameterValue(padding.end)),
                    createCallParameter("top", createSizeParameterValue(padding.top)),
                    createCallParameter("bottom", createSizeParameterValue(padding.bottom))
                )
            )
        }
    }

    fun toCallParameter(): CallParameter? {
        if (!hasModifiers()) {
            return null
        }

        return CallParameter(
            name = "modifier",
            value = ParameterValue.ModifierValue(this)
        )
    }
}

data class Modifier(
    val name: String,
    val parameters: List<CallParameter?> = emptyList(),
    val lambda: (ComposeWriter.() -> Unit)? = null
)
