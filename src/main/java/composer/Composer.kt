package composer

import ast.Layout
import composer.visitor.ComposingVisitor

class Composer {
    fun compose(layout: Layout, fileName: String): String {
        val visitor = ComposingVisitor(fileName)
        layout.accept(visitor)
        return visitor.getResult()
    }

    class ComposerException(message: String) : Exception(message)
}
