package ast.values

sealed class Drawable {

    data class ColorValue(val color: Color): Drawable()

    data class Resource(val name: String): Drawable()

    data class AndroidResource(val name: String): Drawable()

    data class StyleAttribute(val name: String): Drawable()
}
