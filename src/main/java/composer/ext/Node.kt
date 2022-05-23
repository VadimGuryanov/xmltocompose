package composer.ext

import ast.Node
import java.util.WeakHashMap

private val refMap = WeakHashMap<Node, String>()
private var refCounter = 0

fun Node.getRef(): String {
    view.id?.apply { return this }

    refMap[this]?.apply { return this }

    refCounter++
    val ref = "ref_$refCounter"
    refMap[this] = ref

    return ref
}
