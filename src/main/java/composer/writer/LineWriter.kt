package composer.writer

private const val INDENT = "    "

class LineWriter {
    private val builder = StringBuilder()
    private var indent = 0

    fun writeBlock(block: LineWriter.() -> Unit) {
        indent++
        block(this)
        indent--
    }

    fun startLine(text: String = "") {
        repeat(indent) { builder.append(INDENT) }
        builder.append(text)
    }

    fun continueLine(text: String) {
        builder.append(text)
    }

    fun endLine() {
        builder.append("\n")
    }

    fun endLine(text: String) {
        continueLine(text)
        endLine()
    }

    fun writeLine(text: String = "") {
        startLine(text)
        endLine()
    }

    fun getString(): String {
        return builder.toString().trim()
    }
}
