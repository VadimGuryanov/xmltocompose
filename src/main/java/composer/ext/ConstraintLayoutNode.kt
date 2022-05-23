package composer.ext

import ast.Node
import ast.values.Constraints
import ast.viewgroup.ConstraintLayoutNode
import composer.model.Chain

fun ConstraintLayoutNode.findRefs(): Set<String> {
    return viewGroup.children
        .map { node -> node.view.constraints.collectRefs().map { it.id } + node.getRef() }
        .flatten()
        .toSet()
}

fun ConstraintLayoutNode.findChains(): Set<Chain> {
    return findChainsIn(Chain.Direction.HORIZONTAL) + findChainsIn(Chain.Direction.VERTICAL)
}

private fun ConstraintLayoutNode.findChainsIn(direction: Chain.Direction): Set<Chain> {
    val lookupMap = mutableMapOf<String, Node>()
    viewGroup.children.forEach { node ->
        lookupMap[node.getRef()] = node
    }

    val chainBuilder = ChainBuilder(direction)

    viewGroup.children.forEach { node ->
        val tailNode = node.findTailNodeIn(direction, lookupMap)
        if (tailNode != null) {
            chainBuilder.addLink(node, tailNode)
        }
    }

    return chainBuilder.build()
}

private fun Node.findTailNodeIn(
    direction: Chain.Direction,
    lookupMap: Map<String, Node>
): Node? {
    val id = view.constraints.tailId(direction) ?: return null
    if (id == Constraints.Id.Parent) {
        return null
    }

    val tailRef = (id as Constraints.Id.View).id
    val tail = lookupMap[tailRef]

    val headId = tail?.view?.constraints?.headId(direction) ?: return null
    if (headId !is Constraints.Id.View) {
        return null
    }

    return if (headId.id == this.getRef()) {
        tail
    } else {
        return null
    }
}

private fun Constraints.tailId(direction: Chain.Direction): Constraints.Id? {
    return when (direction) {
        Chain.Direction.HORIZONTAL -> horizontalTailId()
        Chain.Direction.VERTICAL -> verticalTailId()
    }
}

private fun Constraints.headId(direction: Chain.Direction): Constraints.Id? {
    return when (direction) {
        Chain.Direction.HORIZONTAL -> horizontalHeadId()
        Chain.Direction.VERTICAL -> verticalHeadId()
    }
}

private fun Constraints.horizontalTailId(): Constraints.Id? {
    return when {
        relative.endToStart != null -> relative.endToStart
        relative.rightToLeft != null -> relative.rightToLeft
        else -> null
    }
}

private fun Constraints.verticalTailId(): Constraints.Id? {
    return when {
        relative.bottomToTop != null -> relative.bottomToTop
        else -> null
    }
}

private fun Constraints.horizontalHeadId(): Constraints.Id? {
    return when {
        relative.startToEnd != null -> relative.startToEnd
        relative.leftToRight != null -> relative.leftToRight
        else -> null
    }
}

private fun Constraints.verticalHeadId(): Constraints.Id? {
    return when {
        relative.topToBottom != null -> relative.topToBottom
        else -> null
    }
}

private class ChainBuilder(
    private val direction: Chain.Direction
) {
    val chains = mutableMapOf<Node, Set<Node>>()

    private fun headWithTail(chains: Map<Node, Set<Node>>, tail: Node): Node? {
        chains.forEach { (head, chain) ->
            if (chain.last() == tail) {
                return head
            }
        }
        return null
    }

    fun addLink(head: Node, tail: Node) {
        if (tail in chains) {
            val existingChain = chains[tail]!!
            chains.remove(tail)
            chains[head] = setOf(tail) + existingChain
            return
        }

        val existingHead = headWithTail(chains, head)
        if (existingHead != null) {
            val newChain = chains[existingHead]!! + tail
            chains[existingHead] = newChain
            return
        }

        chains[head] = setOf(tail)
    }

    fun build(): Set<Chain> {
        return chains.map { (head, elements) ->
            val style = when (direction) {
                Chain.Direction.HORIZONTAL -> head.view.constraints.chain.horizontalStyle
                Chain.Direction.VERTICAL -> head.view.constraints.chain.verticalStyle
            }

            Chain(direction, head, elements, style)
        }.toSet()
    }
}
