package composer.visitor

import ast.Layout
import ast.Visitor
import ast.navigation.ActionNode
import ast.navigation.ArgumentNode
import ast.navigation.FragmentNode
import ast.navigation.NavigationNode
import ast.values.Drawable
import ast.values.LayoutManagerType
import composer.writer.ComposeWriter
import ast.values.Orientation
import ast.view.*
import ast.viewgroup.*
import composer.ext.findChains
import composer.ext.findRefs
import composer.writer.CallParameter
import composer.writer.Modifier
import composer.writer.ModifierBuilder
import composer.writer.ParameterValue
import utils.getMenuName
import utils.getScreenName

class ComposingVisitor(val fileName: String) : Visitor {
    private val writer = ComposeWriter()

    fun getResult(): String {
        return writer.getString()
    }

    override fun visitLayout(layout: Layout) {
        val isNotResourceFile = layout.children.first() !is MenuNode
        val isNotNavGraphFile = layout.children.first() !is NavigationNode
        if (isNotResourceFile) {
            if (isNotNavGraphFile) {
                writer.writePreview(fileName)
            } else {
                writer.writeNavGraphDependence()
            }
        }
        layout.children.forEach { view -> view.accept(this) }
        if (isNotResourceFile && isNotNavGraphFile) writer.writeEnd("}")
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

    override fun visitRecyclerView(node: RecyclerNode) {
        val block: (ComposeWriter.() -> Unit)? = if (node.listItem != null) {
            { writer.writeItemsBlock(node.listItem, node.listItem) }
        } else {
            null
        }
        val modifier = ModifierBuilder(node)
        writer.writeFiled("${node.listItem}_list", "emptyList<Unit>()", true)
        when (node.orientation) {
            Orientation.Horizontal -> {
                when(node.layoutManager) {
                    null, LayoutManagerType.LINEAR -> {
                        writer.writeCall(
                            name = "LazyRow",
                            parameters = listOf(
                                modifier.toCallParameter()
                            ),
                            block = block
                        )
                    }
                    LayoutManagerType.GRID -> {
                        writer.writeCall(
                            name = "LazyHorizontalGrid",
                            parameters = listOf(
                                modifier.toCallParameter(),
                                CallParameter(name = "columns", value = ParameterValue.GridColumnsValue(node.spanCount ?: 2))
                            ),
                            block = block
                        )
                    }
                }
            }
            null, Orientation.Vertical -> {
                when(node.layoutManager) {
                    null, LayoutManagerType.LINEAR ->  {
                        writer.writeCall(
                            name = "LazyColumn",
                            parameters = listOf(
                                modifier.toCallParameter()
                            ),
                            block = block
                        )
                    }
                    LayoutManagerType.GRID -> {
                        writer.writeCall(
                            name = "LazyVerticalGrid",
                            parameters = listOf(
                                modifier.toCallParameter(),
                                CallParameter(name = "columns", value = ParameterValue.GridColumnsValue(node.spanCount ?: 2))
                            ),
                            block = block
                        )
                    }
                }
            }
        }
    }

    override fun visitBottomNavView(node: BottomNavNode) {
        val itemsNameFiled = if (node.menu != null)"${node.menu}_list" else "exampleList"
        val block: (ComposeWriter.() -> Unit)? = if (node.menu != null) {
            { writer.writeBottomNavItems(node, itemsNameFiled) }
        } else {
            null
        }
        val modifier = ModifierBuilder(node)
        writer.writeFiled(
            name = itemsNameFiled,
            block= "get${node.menu?.getMenuName() ?: ""}List()"
        )
        writer.writeCall(
            name = "BottomNavigation",
            parameters = listOf(
                modifier.toCallParameter()
            ),
            block = block
        )
    }

    override fun visitToolbarView(node: ToolbarNode) {
        val modifier = ModifierBuilder(node)

        val listParams = mutableListOf<CallParameter>()
        modifier.toCallParameter()?.let {
            listParams.add(it)
        }
        node.view.background?.let {
            listParams.add(CallParameter(
                name = "backgroundColor",
                value = ParameterValue.DrawableValue(it)
            ))
        }
        node.navigationIcon?.let {
            listParams.add(CallParameter(
                name = "navigationIcon",
                value = ParameterValue.IconComposable(
                    drawable = ParameterValue.DrawableValue(node.navigationIcon),
                    contentDescription = null
                )
            ))
        }
        listParams.add(
            CallParameter(
                name = "title",
                value = ParameterValue.TextStringComposable(
                    text = node.title,
                    color = node.titleTextColor?.let {
                        ParameterValue.ColorValue(node.titleTextColor)
                    }
                )
            )
        )
        node.menu?.let {
            val itemsNameFiled = "${it}_list"
            writer.writeFiled(
                name = itemsNameFiled,
                block= "get${it.getMenuName()}List()"
            )

            listParams.add(
                CallParameter(
                    name = "actions",
                    value = ParameterValue.RawValue(" { } ")
                )
            )
        }

        writer.writeCall(
            name = "TopAppBar",
            parameters = listParams
        )
    }

    override fun visitMenuItem(node: ItemNode) {
        val className = fileName.substring(0, fileName.length - 3)
        val icon = (node.icon as? Drawable.Resource)?.let {
            "R.drawable.${it.name}"
        }
        writer.writeCallItem(name = "object ${node.title?.capitalize()}:  $className($icon, \"${node.title}\")\n", endLine = false)
    }

    override fun visitMenu(node: MenuNode) {
        val className = fileName.substring(0, fileName.length - 3)
        val itemTitles = mutableListOf<String>()
        writer.writeCall(
            name = "sealed class $className(var icon: Int, var title: String)") {
            node.viewGroup.children.forEach {
                it.accept(this@ComposingVisitor)
                val item = it as ItemNode
                item.title?.let {
                    itemTitles.add(it.capitalize())
                }
            }
        }

        writer.writeFunction("get${className}List", className, itemTitles)
    }

    override fun visitNavigate(node: NavigationNode) {
        writer.writeNavigation(node, fileName.substring(0, fileName.length - 3).getScreenName()) {
            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitFragment(node: FragmentNode) {
        writer.writeFragment(node, fileName.substring(0, fileName.length - 3).getScreenName()) {
            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitAction(node: ActionNode) {
        writer.writeAction(node) {
            node.viewGroup.children.forEach { view -> view.accept(this@ComposingVisitor) }
        }
    }

    override fun visitArguments(node: ArgumentNode) {
        writer.writeArgument(node)
    }
}

