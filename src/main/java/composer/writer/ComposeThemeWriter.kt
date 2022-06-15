package composer.writer

import ast.ViewGroupNode
import ast.theme.ColorNode
import ast.theme.StyleNode
import ast.view.ItemForStyleNode as ItemNode
import composer.ext.color
import utils.IMPORT_PACKAGE
import utils.getColorName

class ComposeThemeWriter {

    private val writerTheme = LineWriter()
//    private val writerType = LineWriter()
    private val writerStyles = LineWriter()
    private val writerColor = LineWriter()
    private val writerCompositionLocal = LineWriter()
    private val writerLocalObject = LineWriter()

    fun writeImports(nameTheme: String) {
        writerTheme.writeLine("""
            $IMPORT_PACKAGE
            
            import androidx.compose.foundation.isSystemInDarkTheme
            import androidx.compose.material.MaterialTheme
            import androidx.compose.material.darkColors
            import androidx.compose.material.lightColors
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.Color
            import androidx.compose.runtime.CompositionLocalProvider
            
            @Composable
            fun $nameTheme(
                darkTheme: Boolean = isSystemInDarkTheme(),
                content: @Composable () -> Unit
            ) {
            
        """.trimIndent())

        writerCompositionLocal.writeLine("CompositionLocalProvider(")

//        writerType.writeLine("""
//            import androidx.compose.material.Typography
//            import androidx.compose.runtime.staticCompositionLocalOf
//            import androidx.compose.ui.text.TextStyle
//            import androidx.compose.ui.text.font.FontFamily
//            import androidx.compose.ui.text.font.FontStyle
//            import androidx.compose.ui.text.font.FontWeight
//            import androidx.compose.ui.unit.sp
//            import androidx.compose.ui.unit.dp
//
//        """.trimIndent())
//        writerType.writeLine("")
//        writerType.writeLine("val typography = Typography(")
//        writerType.writeBlock {  }


        writerStyles.writeLine("""
            $IMPORT_PACKAGE
            
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.staticCompositionLocalOf
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.text.font.FontFamily
            import androidx.compose.ui.unit.TextUnit
            import androidx.compose.ui.text.font.FontStyle
            import androidx.compose.ui.unit.sp
            import androidx.compose.ui.unit.dp
            
        """.trimIndent())

        writerLocalObject.writeLine("object AppTheme {")
    }

    fun writeImportsForColor() {
        writerColor.writeLine(IMPORT_PACKAGE)
        writerColor.endLine()
        writerColor.writeLine("import androidx.compose.ui.graphics.Color")
        writerColor.endLine()
    }

    fun writeThemes(light: StyleNode?, night: StyleNode?) {
        light?.let { writeTheme(it, true) }
        night?.let { writeTheme(it, false) }

        writerStyles.writeLine("data class ${light?.name?.capitalize()}Class(")
        writerStyles.writeBlock {
            light?.viewGroup?.children?.forEach {
                val nodeItem = it as ItemNode
                writerStyles.writeLine("val ${nodeItem.name}: Color,")
            }
        }
        writerStyles.writeLine(")")
        writerStyles.endLine()

        writerLocalObject.writeBlock {
            writeLine("""
                val colors: ${light?.name?.capitalize()}Class
                    @Composable
                    get() = Local${light?.name?.capitalize()}.current
                """.trimIndent()
            )
        }

        writerStyles.writeLine("val Local${light?.name?.capitalize()} = staticCompositionLocalOf<${light?.name?.capitalize()}Class> {")
        writerStyles.writeBlock {
            writeLine("error(\"No value provides\")")
        }
        writerStyles.writeLine("}")
        writerStyles.endLine()

        writerTheme.writeBlock {
            writerTheme.writeLine("val colors = if (darkTheme) ${light?.name}Light else ${night?.name}Night")
        }

        writerCompositionLocal.writeBlock {
            writeLine("Local${light?.name?.capitalize()} provides colors,")
        }
    }

    fun writeStyle(node: StyleNode) {
        if (node.parent?.contains("Theme") == true) return
//        val listValue = node.viewGroup.children.map { it as ItemNode }.map { it.name }.toList()
//        var listAttr: List<ItemNode>? = null
//        if (listValue.contains("android:fontFamily") ||
//            listValue.contains("android:textSize") ||
//            listValue.contains("android:textColor") ||
//            listValue.contains("android:textStyle")) {
//            writerType.writeLine("${node.name} = TextStyle(")
//            writerType.writeBlock {
//                listAttr = typeStyle(node)
//            }
//            writerType.writeLine("),")
//        }

//        if (listAttr?.isEmpty() == true) {
            writerStyles.writeLine("data class ${node.name?.capitalize()}(")
            writerStyles.writeBlock {
                node.viewGroup.children.forEach {
                    (it as? ItemNode)?.let {
                        if (!typeStyle(it, writerStyles)) {
                            val name = if (it.name?.contains("android") == true) {
                                val nameItem = it.name.split(":")
                                "${nameItem.first()}${nameItem.last().capitalize()}"
                            } else if (it.name?.contains("app") == true) {
                                val nameItem = it.name.split(":")
                                "${nameItem.first()}${nameItem.last().capitalize()}"
                            } else {
                                it.name ?: "name"
                            }

                            startLine("val $name: ")
                            when {
                                it.value?.contains("@color/") == true -> {
                                    val color = it.value.substring(7, it.value.length).getColorName()
                                    continueLine("Color = $color")
                                }
                                it.value?.contains("sp") == true -> {
                                    val sp = it.value.substring(it.value.length - 2, it.value.length)
                                    continueLine("TextUnit = ${it.value.substring(it.value.length - 2)}.$sp")
                                }
                                it.value?.contains("dp") == true -> {
                                    val dp = it.value.substring(it.value.length - 2, it.value.length)
                                    continueLine("TextUnit = ${it.value.substring(it.value.length - 2)}.$dp")
                                }
                                else -> continueLine("String? = null")
                            }
                            continueLine(",")
                            endLine()
                        }
                    }
                }
            }
            writerStyles.writeLine(")")
            writerStyles.endLine()

            writerStyles.writeLine("val Local${node.name?.capitalize()} = staticCompositionLocalOf<${node.name?.capitalize()}> {")
            writerStyles.writeBlock {
                writeLine("error(\"No value provides\")")
            }
            writerStyles.writeLine("}")
            writerStyles.endLine()


            writerTheme.writeBlock {
                writeLine("val ${node.name?.toLowerCase()} = ${node.name}()")
            }
            writerCompositionLocal.writeBlock {
                writeLine("Local${node.name?.capitalize()} provides ${node.name?.toLowerCase()},")
            }

            writerLocalObject.writeBlock {
                writeLine("""
                    val ${node.name?.toLowerCase()}: ${node.name?.capitalize()}
                        @Composable
                        get() = Local${node.name?.capitalize()}.current
                    """.trimIndent()
                )
                endLine()
            }
//        }
    }

    fun writeColor(node: ColorNode) {
        val c = node.value.substring(1, node.value.length)
        val name = node.name.getColorName()
        val color = if (node.value.length > 7) "Color(0x$c)" else "Color(0xFF$c)"
        writerColor.writeLine("val $name = $color")
    }

    fun getResult(): Map<String, String> {
//        writerType.writeLine(")")
//        writerType.endLine()
//        writerType.writeLine("val LocalTypography = staticCompositionLocalOf<Typography> { error(\"No value provides\") }")

        writerCompositionLocal.writeBlock { writeLine("content = content") }
        writerCompositionLocal.writeLine(")")

        writerTheme.endLine()
        writerTheme.writeBlock {
            writerCompositionLocal.getString().split("\n").forEachIndexed { index, s ->
                if (index == 0 || index == writerCompositionLocal.getString().split("\n").size - 1) {
                    writerTheme.writeLine(s)
                } else {
                    writerTheme.writeBlock {
                        writeLine(s)
                    }
                }
            }
        }
        writerTheme.writeLine("}")

        writerStyles.writeLine(writerLocalObject.getString())
        writerStyles.writeLine("}")

        return mapOf(
            "Theme" to writerTheme.getString(),
//            "Type" to writerType.getString(),
            "Styles" to writerStyles.getString()
        )
    }

    fun getResultColor(): String = writerColor.getString()

    private fun writeTheme(node: StyleNode, isLight: Boolean) {
        val pre = if (isLight) "Light" else "Night"
        node.parent?.let {
            if (it.contains("Theme")) {
                writerStyles.writeLine("val ${node.name}$pre = ${node.name?.capitalize()}Class(")
                writerStyles.writeBlock {
                    node.viewGroup.children.forEach {
                        (it as? ItemNode)?.let {
                            val color = it.value?.substring(7, it.value.length)?.getColorName()
                            writeLine("${it.name} = $color,")
                        }
                    }
                }
                writerStyles.writeLine(")")
            }
        }
    }

    private fun typeStyle(item: ItemNode, writerType: LineWriter): Boolean {
        var isFound = true
        when(item.name) {
            "android:fontFamily" -> {
                writerType.startLine("val fontFamily: FontFamily = ")
                when (item.value) {
                    "sans-serif" -> writerType.continueLine("FontFamily.SansSerif")
                    "cursive" -> writerType.continueLine("FontFamily.Cursive")
                    "serif" -> writerType.continueLine("FontFamily.Serif")
                    "monospace" -> writerType.continueLine("FontFamily.Monospace")
                    else -> writerType.continueLine("FontFamily.Default")
                }
                writerType.continueLine(",")
                writerType.endLine()
            }
            "android:textSize" -> {
                item.value?.let { value ->
                    val sp = value.substring(value.length - 2, value.length)
                    writerType.writeLine("val fontSize: TextUnit = ${value.substring(0, value.length - 2)}.$sp,")
                }
            }
            "android:textColor" -> {
                val color = item.value?.substring(7, item.value.length)?.getColorName()
                writerType.writeLine("val color: Color = $color,")
            }
            "android:textStyle" -> {
                writerType.startLine("val fontStyle: FontStyle = ")
                when (item.value) {
                    "italic" -> writerType.continueLine("FontStyle.Italic")
                    else -> writerType.continueLine("FontStyle.Normal")
                }
                writerType.continueLine(",")
                writerType.endLine()
            }
            else -> {
                isFound = false
            }
        }
        return isFound
    }

}