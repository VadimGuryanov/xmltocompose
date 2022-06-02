package parser

object FilesRemember {

    private var path: String = ""
    private val files: MutableList<String> = mutableListOf()
    val isExistFiles: Boolean
        get() = files.isNotEmpty()

    fun addNewFile(fileName: String) {
        when {
            fileName.contains("@layout") -> {
                files.add("$path/layout/${fileName.substring(8, fileName.length)}.xml")
            }
            fileName.contains("@menu") -> {
                files.add("$path/menu/${fileName.substring(6, fileName.length)}.xml")
            }
            else -> {
                files.add(fileName)
            }
        }
    }

    fun addPath(path: String) {
        this.path = path
    }

    fun getFile(): String {
        val file = files.first()
        files.removeFirst()
        return file
    }
}
