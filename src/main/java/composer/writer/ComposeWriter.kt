package composer.writer

import ast.ViewGroupNode
import ast.navigation.ActionNode
import ast.navigation.ArgumentNode
import ast.navigation.FragmentNode
import ast.navigation.NavigationNode
import ast.values.*
import ast.view.BottomNavNode
import composer.ext.getRef
import composer.model.Chain
import composer.model.NavigateScreen
import utils.*

class ComposeWriter {
    private val writer = LineWriter()

    init {
        writer.writeLine(IMPORT_PACKAGE)
        writer.endLine()
        writer.writeLine(IMPORT_R)
    }

    fun writePreview(fileName: String) {
        val name = fileName.substring(0, fileName.length - 3)
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
            $IMPORT_COMPOSE_UI_LAZY
            $IMPORT_COMPOSE_UI_LAZY_ITEMS
            $IMPORT_COMPOSE_UI_REMEMBER
            $IMPORT_COMPOSE_GRAPHICS
            
            @Preview(showBackground = true)
            @Composable
            fun Preview$name() {
                MaterialTheme {
                   $name() 
                }
            }
            
            @Composable
            fun $name() {
            """.trimIndent()
        )
        writer.endLine()
        writer.writeBlock {  }
    }

    fun writeNavGraphDependence() {
        writer.continueLine(
            """
            $IMPORT_COMPOSE_COMPOSABLE
            $IMPORT_COMPOSE_NAV_COMPOSABLE
            $IMPORT_COMPOSE_NAV_HOST
            $IMPORT_COMPOSE_REMEMBER_NAV
            $IMPORT_COMPOSE_NAV_ARG
            $IMPORT_COMPOSE_NAV_TYPE
          
            """.trimIndent()
        )
        writer.endLine()
    }

    fun writeCall(
        name: String,
        parameters: List<CallParameter?> = emptyList(),
        linePrefix: String = "",
        endLine: Boolean = true,
        isAddNewLineForParam: Boolean = false,
        block: (ComposeWriter.() -> Unit)? = null
    ) {
        if (endLine) {
            writer.startLine("$linePrefix$name")
        } else {
            writer.continueLine("$linePrefix$name")
        }

        writeParameters(parameters, block != null, isAddNewLineForParam)

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

    fun writeCallItem(
        name: String,
        linePrefix: String = "",
        endLine: Boolean = true,
        block: (ComposeWriter.() -> Unit)? = null
    ) {
        writer.startLine("$linePrefix$name")
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

    fun writeFiled(name: String, block: String, isRemember: Boolean = false) {
        writer.startLine("val $name = ")
        if (isRemember) {
            writer.continueLine("remember { $block }")
        } else {
            writer.continueLine(block)
        }
        writer.writeLine()
    }

    fun writeItemsBlock(nameField: String, itemListFileName: String) {
        writer.startLine("items(${nameField}_list) { item ->")
        writer.writeLine()
        writer.writeLine("\t${itemListFileName.getShortFileName()}()")
        writer.writeLine()
        writer.writeLine("}")
    }

    fun writeBottomNavItems(node: BottomNavNode, itemsName: String) {
        writer.startLine("$itemsName.forEach { item -> ")
        writer.writeLine()
        writeBlock {
            writer.writeLine("BottomNavigationItem(")
            writeBlock {
                val icon = "Icon(painterResource(id = item.icon), contentDescription = item.title${
                    node.itemIconTint?.let {
                        when (it) {
                            is Color.Resource -> {
                                ", tint = colorResource(id = R.color.${it.name})"
                            }
                            is Color.Absolute -> {
                                ", tint = Color(${it.value})"
                            }
                        }
                    } ?: ""
                }) },"
                writer.writeLine("icon = { $icon")
                if (!node.labelVisibilityMode) {
                    val label = "label = { Text(text = item.title)" + if (node.itemTextColor != null) {
                        when (node.itemTextColor) {
                            is Color.Absolute -> ", color = colorResource(id = R.color.${node.itemTextColor.value}) },"
                            is Color.Resource -> ", color = Color(${node.itemTextColor.name}) },"
                        }
                    } else {
                        " },"
                    }
                    writer.writeLine(label)
                }
                val selectedContentColor = when (node.itemTextColor) {
                    is Color.Resource -> "colorResource(id = R.color.${node.itemTextColor.name})"
                    is Color.Absolute -> "Color(${node.itemTextColor.value})"
                    else -> ""
                }
                writer.writeLine(
                    """
                selectedContentColor = $selectedContentColor,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = ${node.labelVisibilityMode},
                selected = false,
                onClick = {
                    /* Add code later */
                }"""
                )
            }
            writer.startLine(")")
        }
        writer.writeLine()

        writer.writeLine()
        writer.writeLine("}")
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

    private fun writeGridColumns(value: ParameterValue.GridColumnsValue) {
        writer.continueLine("GridCells.Fixed(${value.columns})")
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

    private fun writeParameters(parameters: List<CallParameter?>, isFollowedByLambda: Boolean ,isAddNewLine: Boolean = false) {
        if (parameters.isEmpty() && isFollowedByLambda) return

        writer.continueLine("(")

        var addSeparator = false
        parameters.filterNotNull().forEach { parameter ->
            if (addSeparator) {
                writer.continueLine(", ")
            }

            if (parameter.name != null) {
                if (isAddNewLine) {
                    writer.writeBlock {
                        writer.endLine()
                        writer.startLine(parameter.name)
                    }
                } else {
                    writer.continueLine(parameter.name)
                }
                writer.continueLine(" = ")
            }

            writeParameterValue(parameter.value)

            addSeparator = true
        }

        if (isAddNewLine) {
            writer.endLine()
            writer.startLine(")")
        } else {
            writer.continueLine(")")
        }
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
            is ParameterValue.GridColumnsValue -> writeGridColumns(value)
            is ParameterValue.TextStringComposable -> writeTextString(value)
            is ParameterValue.IconComposable -> writeIcon(value)
        }
    }

    private fun writeTextString(value: ParameterValue.TextStringComposable) {
        writer.continueLine(" { Text(text = ")
        if (value.text.contains("@string/")) {
            writer.continueLine("stringResource(R.string.${value.text.substring(8, value.text.length)}), ")
        } else {
            writer.continueLine("\"${value.text}\", ")
        }
        writer.continueLine("color = ")
        value.color?.let { writeColor(it) }
        writer.continueLine(") }")
    }

    private fun writeIcon(value: ParameterValue.IconComposable) {
        writer.continueLine(" {")
        writer.endLine()
        writer.writeBlock {
            writer.writeBlock {
                writer.startLine("IconButton(onClick = { /*TODO*/ }) { ")
                writer.writeBlock {
                    writer.endLine()
                    writer.startLine("Icon(")
                    writer.continueLine("painter = ")
                    writeDrawable(value.drawable)
                    writer.continueLine(", contentDescription = null,")
                    writer.continueLine("tint = Color.Unspecified")
                    writer.endLine(")")
                }
                writer.writeLine("}")
            }
        }
        writer.startLine("}")
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

    fun writeFunction(name: String, fileName: String, itemTitles: List<String>) {
        writer.writeLine("fun $name(): List<$fileName> = listOf(")
        writeBlock {
            itemTitles.forEach {
                writer.writeLine("$fileName.$it,")
            }
        }
        writer.writeLine(")")
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
        return writer.getString() + "\n"
    }

    fun writeEnd(text: String) {
        writer.writeLine(text)
    }


    fun writeNavigation(node: NavigationNode, fileName: String, block: (ComposeWriter.() -> Unit)?) {

        val list = mutableListOf<NavigateScreen>()
        node.viewGroup.children.forEach {
            if (it is FragmentNode) {
                list.add(
                    NavigateScreen(id = it.id, name = it.name, label = it.label, layout = it.layout)
                )
            }
        }
        writeScreen(list, fileName)

        val startDestination = list.first {
            it.id.substring(5, it.id.length) == node.startDestination.substring(4, node.startDestination.length)
        }


        writer.writeLine("@Composable")
        writer.writeLine("fun NavGraph() {")
        writeBlock {
            writer.writeLine("val navController = rememberNavController()")
            writer.writeLine("NavHost(navController = navController, startDestination = ${fileName}.${startDestination.label.capitalize()}.route) { ")
            if (block != null) {
                writeBlock(block)
            }
            writer.writeLine("}")
        }
        writer.writeLine("}")
    }

    fun writeScreen(list: List<NavigateScreen>, fileName: String) {
        writer.writeLine("sealed class $fileName(val route: String) {")
        list.forEach {
            writer.continueLine("\tobject ")
            writer.continueLine(it.label.capitalize())
            writer.writeLine(": $fileName(\"${it.id.substring(5, it.id.length)}\")")
        }
        writer.writeLine("}")
    }

    fun writeFragment(node: FragmentNode, filename: String, block: (ComposeWriter.() -> Unit)?) {
        writer.startLine("composable(route = ")
        writer.continueLine("$filename.${node.label.capitalize()}.route")
        if (block != null) {
            writer.continueLine(", ")
            writeBlock(block)
        }
        node.viewGroup.children.firstOrNull {
            (it as? ViewGroupNode)?.viewGroup?.children?.isNotEmpty() == true
        }?.let {
            writer.startLine(") { ")
        } ?: run {
            writer.continueLine(") { ")
        }

        node.layout?.let {
            writer.continueLine("${it.getShortFileName()}()")
        }
        writer.endLine(" }")
    }

    fun writeAction(node: ActionNode, block: (ComposeWriter.() -> Unit)) {
        if (node.viewGroup.children.isNotEmpty()) {
            writer.writeLine()
            writer.startLine("arguments = listOf(")
            writeBlock(block)
            writer.writeLine(")")
        }
    }

    fun writeArgument(node: ArgumentNode) {
        writer.writeLine()
        writer.writeLine("navArgument(name = \"${node.name}\") { ")
        writer.writeBlock {
            writer.writeLine("type = ${getNavType(node.argType)}")
            writer.writeLine("defaultValue = \"${node.defaultValue}\"")
            writer.writeLine("nullable = ${node.nullable}")
        }
        writer.writeLine("},")
    }

    private fun getNavType(type: String): String = when (type) {
        "string" -> "NavType.StringType"
        "integer" -> "NavType.IntType"
        "boolean" -> "NavType.BoolType"
        "long" -> "NavType.LongType"
        "float" -> "NavType.FloatType"
        else -> "NavType.StringType"
    }
}
