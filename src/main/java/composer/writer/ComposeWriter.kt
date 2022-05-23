package composer.writer

import ast.values.*
import composer.ext.getRef
import composer.model.Chain
import utils.*

class ComposeWriter {
    private val writer = LineWriter()

    fun writePreview(fileName: String) {
        writer.continueLine(
            """
            $IMPORT_COMPOSE_FOUNDATION
            $IMPORT_COMPOSE_MATERIAL
            $IMPORT_COMPOSE_COMPOSABLE
            $IMPORT_COMPOSE_LAYOUT_SIZE
            $IMPORT_COMPOSE_UI
            $IMPORT_COMPOSE_UI_RES
            $IMPORT_COMPOSE_UI_TEXT_STYLE
            $IMPORT_COMPOSE_UI_UNIT
            $IMPORT_COMPOSE_PREVIEW
            $IMPORT_COMPOSE_CONSTRAINT
            
            @Preview
            @Composable
            fun Preview() {
                MaterialTheme {
                   ${fileName.substring(0, fileName.length - 3)}() 
                }
            }
            
            @Composable
            fun ${fileName.substring(0, fileName.length - 3)}() {
            """.trimIndent()
        )
        writer.endLine()
    }

    fun writeCall(
        name: String,
        parameters: List<CallParameter?> = emptyList(),
        linePrefix: String = "",
        endLine: Boolean = true,
        block: (ComposeWriter.() -> Unit)? = null
    ) {
        writer.startLine("$linePrefix$name")

        writeParameters(parameters, block != null)

        if (block == null) {
            if (endLine) {
                writer.endLine()
            }
        } else {
            writer.endLine(" {")
            writeBlock(block)
            writer.startLine("$linePrefix}")
            if (endLine) {
                writer.endLine()
            }
        }
    }

    fun writeRefsDeclaration(refs: Set<String>) {
        writer.startLine("val (")
        writer.continueLine(refs.joinToString(", "))
        writer.endLine(") = createRefs()")
        writer.writeLine()
    }

    fun writeChains(chains: Set<Chain>) {
        chains.forEach { chain ->
            if (chain.direction == Chain.Direction.HORIZONTAL) {
                writer.startLine("createHorizontalChain")
            } else {
                writer.startLine("createVerticalChain")
            }

            val parameters = (listOf(chain.head) + chain.elements).map { node ->
                CallParameter(ParameterValue.RawValue(node.getRef()))
            }.toMutableList()

            when (chain.style) {
                Constraints.Chain.Style.PACKED ->
                    parameters.add(
                        CallParameter(
                            name = "chainStyle",
                            value = ParameterValue.RawValue("ChainStyle.Packed")
                        )
                    )
                Constraints.Chain.Style.SPREAD ->
                    parameters.add(
                        CallParameter(
                            name = "chainStyle",
                            value = ParameterValue.RawValue("ChainStyle.Spread")
                        )
                    )
                Constraints.Chain.Style.SPREAD_INSIDE ->
                    parameters.add(
                        CallParameter(
                            name = "chainStyle",
                            value = ParameterValue.RawValue("ChainStyle.SpreadInside")
                        )
                    )
            }

            writeParameters(parameters, false)
            writer.endLine()
        }
        writer.writeLine()
    }

    private fun writeString(value: ParameterValue.StringValue) {
        writer.continueLine("\"")
        writer.continueLine(value.raw)
        writer.continueLine("\"")
    }

    private fun writeEmptyLambda() {
        writer.continueLine("{}")
    }

    private fun writeLambda(value: ParameterValue.LambdaValue) {
        writer.continueLine("{ ")
        value.lambda.invoke(this)
        writer.continueLine(" }")
    }

    private fun writeColor(value: ParameterValue.ColorValue) {
        when (val color = value.color) {
            is Color.Absolute -> {
                writer.continueLine("Color(")
                writer.continueLine("0x")
                writer.continueLine(color.value.toString(16))
                writer.continueLine(".toInt()")
                writer.continueLine(")")
            }
            is Color.Resource -> {
                writer.continueLine("colorResource(id = R.color.${color.name})")
            }
        }
    }

    private fun writeModifierValue(value: ParameterValue.ModifierValue) {
        var addComma = false
        writer.continueLine("Modifier.")
        value.builder.getModifiers().forEach { modifier ->
            if (addComma) {
                writer.continueLine(".")
            }

            writer.continueLine(modifier.name)

            writeParameters(
                modifier.parameters,
                isFollowedByLambda = modifier.lambda != null
            )

            modifier.lambda?.let { lambda ->
                writer.endLine(" {")
                writeBlock {
                    lambda.invoke(this)
                }
                writer.startLine("}")
            }

            addComma = true
        }
    }

    private fun writeParameters(parameters: List<CallParameter?>, isFollowedByLambda: Boolean) {
        if (parameters.isEmpty() && isFollowedByLambda) return

        writer.continueLine("(")

        var addSeparator = false
        parameters.filterNotNull().forEach { parameter ->
            if (addSeparator) {
                writer.continueLine(", ")
            }

            if (parameter.name != null) {
                writer.continueLine(parameter.name)
                writer.continueLine(" = ")
            }

            writeParameterValue(parameter.value)

            addSeparator = true
        }

        writer.continueLine(")")
    }

    private fun writeSize(value: ParameterValue.SizeValue) {
        when (value.size) {
            is Size.Dp -> writer.continueLine("${value.size.value}.dp")
            is Size.Sp -> writer.continueLine("${value.size.value}.sp")
        }
    }

    private fun writeDrawable(value: ParameterValue.DrawableValue) {
        when (value.drawable) {
            is Drawable.ColorValue -> writeColor(
                ParameterValue.ColorValue(value.drawable.color)
            )
            is Drawable.Resource -> {
                writeCall(
                    name = "painterResource",
                    parameters = listOf(
                        CallParameter(ParameterValue.RawValue("R.drawable.${value.drawable.name}"))
                    ),
                    endLine = false
                )
            }
            is Drawable.AndroidResource -> {
                writeCall(
                    name = "painterResource",
                    parameters = listOf(
                        CallParameter(ParameterValue.RawValue("android.R.drawable.${value.drawable.name}"))
                    ),
                    endLine = false
                )
            }
            is Drawable.StyleAttribute -> {
                writeCall(
                    name = "painterResource",
                    parameters = listOf(
                        CallParameter(ParameterValue.RawValue(value.drawable.name))
                    ),
                    endLine = false
                )
            }
        }
    }

    private fun writeKeyboardType(value: ParameterValue.KeyboardTypeValue) {
        writer.continueLine("KeyboardType")
        writer.continueLine(".")
        val inputType = when (value.inputType) {
            InputType.Text -> "Text"
            InputType.Number -> "Number"
            InputType.Phone -> "Phone"
            InputType.Uri -> "Uri"
            InputType.Email -> "Email"
            InputType.Password -> "Password"
            InputType.NumberPassword -> "NumberPassword"
        }
        writer.continueLine(inputType)
    }

    private fun writeParameterValue(value: ParameterValue) {
        when (value) {
            is ParameterValue.StringValue -> writeString(value)
            is ParameterValue.EmptyLambdaValue -> writeEmptyLambda()
            is ParameterValue.ColorValue -> writeColor(value)
            is ParameterValue.ModifierValue -> writeModifierValue(value)
            is ParameterValue.RawValue -> writer.continueLine(value.raw)
            is ParameterValue.SizeValue -> writeSize(value)
            is ParameterValue.DrawableValue -> writeDrawable(value)
            is ParameterValue.KeyboardTypeValue -> writeKeyboardType(value)
            is ParameterValue.LambdaValue -> writeLambda(value)
        }
    }

    fun writeRelativePositioningConstraint(
        from: String,
        id: Constraints.Id,
        to: String
    ) {
        writer.startLine("$from.linkTo(")
        writeConstraintId(id)
        writer.endLine(".$to)")
    }

    fun writeSizeConstraint(dimension: String, size: LayoutSize) {
        if (size == LayoutSize.Absolute(Size.Dp(0))) {
            writer.startLine("$dimension = Dimension.fillToConstraints")
            writer.endLine()
        }
    }

    private fun writeConstraintId(id: Constraints.Id) {
        if (id is Constraints.Id.Parent) {
            writer.continueLine("parent")
        } else if (id is Constraints.Id.View) {
            writer.continueLine(id.id)
        }
    }

    private fun writeBlock(block: ComposeWriter.() -> Unit) {
        writer.writeBlock {
            block()
        }
    }

    fun getString(): String {
        return writer.getString() + "\n}"
    }
}
