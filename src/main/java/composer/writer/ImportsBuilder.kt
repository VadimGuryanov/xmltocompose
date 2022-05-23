package composer.writer

class ImportsBuilder {
    private val builder = StringBuilder()

    fun writeImport(import: String) {
        builder.append(import)
        builder.append("\n")
    }
}