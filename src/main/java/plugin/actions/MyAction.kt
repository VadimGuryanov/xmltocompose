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

                while (FilesRemember.isExistFiles) {
                    val rememberFileName = FilesRemember.getFile()
                    showMessage("File : $rememberFileName")
                    LocalFileSystem.getInstance().findFileByPath(rememberFileName)?.let { file ->
                        val layout = parser.parse(file.inputStream.bufferedReader())
//                        showMessage("layout : $layout")
//                        layout.children.forEach {
//                            showMessage("ViewGroupNode : $it")
//                            (it as? ViewGroupNode)?.viewGroup?.children?.forEach {
//                                showMessage("Node : $it")
//                            }
//                        }
                        val fileName = rememberFileName.getFileName()
//                        showMessage("fileName : $fileName")
                        val result = composer.compose(layout, fileName)
                        createFile(event, fileName, result)
                    } ?: showMessage("Error : $rememberFileName")
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
}
