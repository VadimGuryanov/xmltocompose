package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import composer.Composer
import parser.FilesRemember
import parser.Parser
import utils.getFileName
import utils.getFileTheme
import utils.getMainFolder
import utils.getResFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class MyAction: AnAction() {
    private val parser = Parser()
    private val composer = Composer()

    override fun actionPerformed(event: AnActionEvent) {
        val fileText = event.getData(PlatformDataKeys.FILE_TEXT)
        val file = event.getData(PlatformDataKeys.PSI_FILE)
        val absolutePath = file?.originalFile?.virtualFile?.path
        fileText?.let {
            try {
                FilesRemember.addPath(event.project?.basePath?.getResFolder() ?: throw Exception("file not found"))
                FilesRemember.addNewFile(absolutePath ?: throw Exception("file not found"))

                if (!checkIsResource(absolutePath)) {
                    while (FilesRemember.isExistFiles) {
                        val rememberFileName = FilesRemember.getFile()
                        showMessage("File : $rememberFileName")
                        LocalFileSystem.getInstance().findFileByPath(rememberFileName)?.let { file ->
                            val layout = parser.parse(file.inputStream.bufferedReader())
                            showMessage("Layout : $layout")
                            val fileName = rememberFileName.getFileName()
                            val result = composer.compose(layout, fileName)
                            createFile(event, fileName, result)
                        } ?: showMessage("Error : $rememberFileName")
                    }
                } else {

                    FilesRemember.isStyleFoundFile = true

                    showMessage("File : $absolutePath")
                    var path: List<String>? = null
                    if (absolutePath.contains("values/styles") || absolutePath.contains("values/themes")) {
                        path = absolutePath.split("values")
                        FilesRemember.addNewFile(path.first() + "values-night" + path.last())
                    } else {
                        path = absolutePath.split("values-night")
                        FilesRemember.addNewFile(path.first() + "values" + path.last())
                    }
                    FilesRemember.addNewFile(path.first() + "values/colors.xml")


                    val file1 = FilesRemember.getFile()
                    val file2 = FilesRemember.getFile()
                    val fileColor = FilesRemember.getFile()


                    showMessage("File1 : $file1")
                    showMessage("File2 : $file2")
                    showMessage("FileColor : $fileColor")

                    LocalFileSystem.getInstance().findFileByPath(file1)?.let { f1 ->
                        LocalFileSystem.getInstance().findFileByPath(file2)?.let { f2 ->
                            val layout1 = parser.parse(f1.inputStream.bufferedReader())
                            val layout2 = parser.parse(f2.inputStream.bufferedReader())

                            showMessage("layout1 : $layout1")
                            showMessage("layout2 : $layout2")

                            val fileName = file1.getFileTheme()

                            val result = composer.composeForTheme(layout1, layout2, fileName)

                            result.forEach {
                                createFile(
                                    event = event,
                                    fileName = "${it.key}.kt",
                                    result = it.value
                                )
                            }
                        }
                    }

                    LocalFileSystem.getInstance().findFileByPath(fileColor)?.let {
                        val layoutColors = parser.parse(it.inputStream.bufferedReader())
                        val fileName = "Color.kt"
                        val result = composer.compose(layoutColors, fileName)
                        createFile(event, fileName, result)
                    }

                    FilesRemember.isStyleFoundFile = false
                }
            } catch (e: Exception) {
                showMessage("${e.message} ParcerException")
            } catch (e: Composer.ComposerException) {
                showMessage("${e.message} ComposerException")
            }
        }
    }

    private fun createFile(event: AnActionEvent, fileName: String, result: String) {
        event.project?.basePath?.let { path ->
            val dirPath = "${path.getMainFolder()}/xml2compose"
            if (!File(dirPath).exists()) {
                Files.createDirectory(Paths.get(dirPath))
            }
            File("$dirPath/$fileName").writeText(result)
            showMessage("${event.project?.basePath} base path")
        }
    }

    private fun showMessage(str: String) {
        Messages.showMessageDialog(str, "Action", Messages.getInformationIcon())
    }

    private fun checkIsResource(filePath: String): Boolean =
        filePath.contains("values/styles") || filePath.contains("values-night/styles") ||
        filePath.contains("values/themes") || filePath.contains("values-night/themes")
}
