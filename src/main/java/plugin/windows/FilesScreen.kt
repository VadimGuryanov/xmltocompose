package plugin.windows

import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.PsiDocumentManager

import javax.swing.*

class FilesScreen(toolWindow: ToolWindow) {

    val content: JPanel = JPanel()

    init {
        PsiDocumentManager.getInstance(project)
    }
}