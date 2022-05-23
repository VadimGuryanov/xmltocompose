package composer.visitor

import ast.Layout
import ast.Visitor
import ast.values.Drawable
import composer.writer.ComposeWriter
import ast.values.Orientation
import ast.view.ButtonNode
import ast.view.CheckBoxNode
import ast.view.EditTextNode
import ast.view.ImageViewNode
import ast.view.RadioButtonNode
import ast.view.SwitchNode
import ast.view.TextViewNode
import ast.view.ViewNode
import ast.viewgroup.CardViewNode
import ast.viewgroup.ConstraintLayoutNode
import ast.viewgroup.FrameLayoutNode
import ast.viewgroup.LinearLayoutNode
import ast.viewgroup.UnknownNode
import composer.ext.findChains
import composer.ext.findRefs
import composer.writer.CallParameter
import composer.writer.Modifier
import composer.writer.ModifierBuilder
import composer.writer.ParameterValue

class ComposingVisitor(val fileName: String) : Visitor {
    private val writer = ComposeWriter()

    fun getResult(): String {
        return writer.getString()
    }

    override fun visitLayout(layout: Layout) {
        writer.writePreview(fileName)
        layout.children.forEach { view -> view.accept(this) }
    }

    override fun visitView(node: ViewNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            "Box",
            parameters = listOf(
                modifier.toCallParameter()
            )
        )
    }

    override fun visitButton(node: ButtonNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "Button",
            parameters = listOf(
                CallParameter(name = "onClick", value = ParameterValue.EmptyLambdaValue),
                modifier.toCallParameter()
            )
        ) {
            writeCall(
                name = "Text",
                parameters = listOf(
                    CallParameter(name = "text", value = ParameterValue.StringValue(node.text)),
                    CallParameter(name = "textAlign", value = ParameterValue.RawValue("TextAlign.Center"))
                )
            )
        }
    }

    override fun visitTextView(node: TextViewNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "Text",
            parameters = listOf(
                CallParameter(name = "text", value = ParameterValue.StringValue(node.text)),
                node.textColor?.let { CallParameter(name = "color", value = ParameterValue.ColorValue(it)) },
                node.textSize?.let { CallParameter(name = "fontSize", value = ParameterValue.SizeValue(it)) },
                node.maxLines?.let { CallParameter(name = "maxLines", value = ParameterValue.RawValue(it)) },
                modifier.toCallParameter()
            )
        )
    }

    override fun visitCheckBox(node: CheckBoxNode) {
        val rowModifier = ModifierBuilder(node)

        writer.writeCall(
            "Row",
            parameters = listOf(
                rowModifier.toCallParameter()
            )
        ) {
            writeCall(
                name = "Checkbox",
                parameters = listOf(
                    CallParameter(name = "checked", value = ParameterValue.RawValue(node.checked)),
                    CallParameter(name = "onCheckedChange", value = ParameterValue.EmptyLambdaValue)
                )
            )
            node.text?.let { text ->
                val textModifier = ModifierBuilder()
                textModifier.add(
                    Modifier(
                        "align",
                        listOf(
                            CallParameter(ParameterValue.RawValue("Alignment.CenterVertically"))
                        )
                    )
                )

                writeCall(
                    name = "Text",
                    parameters = listOf(
                        CallParameter(ParameterValue.StringValue(text)),
                        textModifier.toCallParameter()
                    )
                )
            }
        }
    }

    override fun visitRadioButton(node: RadioButtonNode) {
        val rowModifier = ModifierBuilder(node)

        writer.writeCall(
            "Row",
            parameters = listOf(
                rowModifier.toCallParameter()
            )
        ) {
            writeCall(
                name = "RadioButton",
                parameters = listOf(
                    CallParameter(name = "selected", value = ParameterValue.RawValue(node.checked)),
                    CallParameter(name = "onClick", value = ParameterValue.EmptyLambdaValue)
                )
            )
            node.text?.let { text ->
                val textModifier = ModifierBuilder()
                textModifier.add(
                    Modifier(
                        name = "align",
                        parameters = listOf(
                            CallParameter(
                                ParameterValue.RawValue("Alignment.CenterVertically")
                            )
                        )
                    )
                )

                writeCall(
                    name = "Text",
                    parameters = listOf(
                        CallParameter(ParameterValue.StringValue(text)),
                        textModifier.toCallParameter()
                    )
                )
            }
        }
    }

    override fun visitCardView(node: CardViewNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "Card",
            parameters = listOf(
                modifier.toCallParameter()
            )
        ) {
            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitImageView(node: ImageViewNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "Image",
            parameters = listOf(
                node.src.let {
                    CallParameter(ParameterValue.DrawableValue(
                    it ?: Drawable.Resource("not_found")
                    ))
                },
                CallParameter(ParameterValue.StringValue(node.srcContent)),
                modifier.toCallParameter()
            )
        )
    }

    override fun visitLinearLayout(node: LinearLayoutNode) {
        val composable = when (node.orientation) {
            Orientation.Vertical -> "Column"
            Orientation.Horizontal -> "Row"
        }

        writer.writeCall(composable) {
            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitFrameLayout(node: FrameLayoutNode) {
        val rowModifier = ModifierBuilder(node)
        writer.writeCall(name = "Box", parameters = listOf(rowModifier.toCallParameter())) {
            node.viewGroup.children.forEach { it.accept(this@ComposingVisitor) }
        }
    }

    override fun visitConstraintLayout(node: ConstraintLayoutNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "ConstraintLayout",
            parameters = listOf(
                modifier.toCallParameter()
            )
        ) {
            val refs = node.findRefs()
            if (refs.isNotEmpty()) {
                writer.writeRefsDeclaration(refs)
            }

            val chains = node.findChains()
            if (chains.isNotEmpty()) {
                writer.writeChains(chains)
            }

            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitUnknown(node: UnknownNode) {
        val block: (ComposeWriter.() -> Unit)? = if (node.viewGroup.children.isEmpty()) {
            null
        } else {
            { node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) } }
        }

        val modifier = ModifierBuilder(node)

        writer.writeCall(
            node.name,
            parameters = listOf(modifier.toCallParameter()),
            linePrefix = "// ",
            block = block
        )
    }

    override fun visitEditText(node: EditTextNode) {
        val modifier = ModifierBuilder(node)
        val hintParameterValue = if (node.hint.isNotBlank()) {
            ParameterValue.LambdaValue {
                writeCall(
                    name = "Text",
                    parameters = listOf(
                        CallParameter(name = "text", value = ParameterValue.StringValue(node.hint)),
                        node.textColorHint?.let {
                            CallParameter(name = "color", value = ParameterValue.ColorValue(it))
                        }
                    ),
                    endLine = false
                )
            }
        } else {
            null
        }
        writer.writeCall(
            name = "TextField",
            parameters = listOf(
                CallParameter(name = "value", value = ParameterValue.StringValue(node.text)),
                CallParameter(name = "onValueChange", value = ParameterValue.EmptyLambdaValue),
                CallParameter(name = "keyboardType", value = ParameterValue.KeyboardTypeValue(node.inputType)),
                hintParameterValue?.let { CallParameter(name = "label", value = it) },
                modifier.toCallParameter()
            )
        )
    }

    override fun visitSwitch(node: SwitchNode) {
        val modifier = ModifierBuilder(node)

        writer.writeCall(
            name = "Switch",
            parameters = listOf(
                modifier.toCallParameter(),
                CallParameter(name = "checked", value = ParameterValue.RawValue(node.checked)),
                CallParameter(name = "onCheckedChange", value = ParameterValue.EmptyLambdaValue)
            )
        )
    }
}
