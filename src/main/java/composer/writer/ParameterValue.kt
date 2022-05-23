package composer.writer

import ast.values.Color
import ast.values.Drawable
import ast.values.InputType
import ast.values.Size

sealed class ParameterValue {
    class RawValue(
        val raw: String
    ) : ParameterValue() {
        constructor(raw: Int) : this(raw.toString())
        constructor(raw: Boolean) : this(raw.toString())
    }

    class StringValue(
        val raw: String
    ) : ParameterValue()

    object EmptyLambdaValue : ParameterValue()

    class ColorValue(
        val color: Color
    ) : ParameterValue()

    class ModifierValue(
        val builder: ModifierBuilder
    ) : ParameterValue()

    class SizeValue(
        val size: Size
    ) : ParameterValue()

    class DrawableValue(
        val drawable: Drawable
    ) : ParameterValue()

    class KeyboardTypeValue(
        val inputType: InputType
    ) : ParameterValue()

    class LambdaValue(
        val lambda: ComposeWriter.() -> Unit
    ) : ParameterValue()
}

fun createSizeParameterValue(size: Size?): ParameterValue.SizeValue? {
    return size?.let { ParameterValue.SizeValue(it) }
}
