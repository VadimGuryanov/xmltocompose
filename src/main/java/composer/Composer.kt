package composer

import ast.Layout
import composer.visitor.ComposingVisitor

class Composer {
    fun compose(layout: Layout, fileName: String): String {
        val visitor = ComposingVisitor(fileName)
        layout.accept(visitor)
        return visitor.getResult()
    }

    fun composeForTheme(light: Layout, night: Layout, fileName: String): Map<String, String> {
        val visitor = ComposingVisitor(fileName)
        light.acceptTheme(night, visitor)
        return visitor.getResultTheme()
    }

    class ComposerException(message: String) : Exception(message)
}
