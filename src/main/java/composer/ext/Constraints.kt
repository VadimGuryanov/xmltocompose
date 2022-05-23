package composer.ext

import ast.values.Constraints

fun Constraints.hasConstraints(): Boolean {
    return this != Constraints()
}

fun Constraints.collectRefs(): Set<Constraints.Id.View> {
    return listOfNotNull(
        relative.bottomToBottom,
        relative.bottomToTop,
        relative.endToEnd,
        relative.endToStart,
        relative.leftToLeft,
        relative.leftToRight,
        relative.rightToLeft,
        relative.rightToRight,
        relative.startToEnd,
        relative.startToStart,
        relative.topToBottom,
        relative.topToTop
    ).filter {
        it != Constraints.Id.Parent
    }.map {
        it as Constraints.Id.View
    }.toSet()
}
