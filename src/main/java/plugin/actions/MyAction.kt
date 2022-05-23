package plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import composer.Composer
import parser.Parser
import utils.getFileName
import utils.getMainFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class MyAction: AnAction() {
    private val parser = Parser()
    private val composer = Composer()

    override fun actionPerformed(event: AnActionEvent) {
        val fileText = event.getData(PlatformDataKeys.FILE_TEXT)
        val file = event.getData(PlatformDataKeys.PSI_FILE)
        fileText?.let {
            try {
                val fileName = file?.name?.getFileName() ?: throw Exception("file not found")
                showMessage("Start action : $fileName")
                val layout = parser.parse(it)
                val result = composer.compose(layout, fileName)
                createFile(event, fileName, result)
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
            Files.createDirectory(Paths.get(dirPath))
            File("$dirPath/$fileName").writeText(result)
            showMessage("${event.project?.basePath} base path")
        }
    }

    private fun showMessage(str: String) {
        Messages.showMessageDialog(str, "Action", Messages.getInformationIcon())
    }
}
