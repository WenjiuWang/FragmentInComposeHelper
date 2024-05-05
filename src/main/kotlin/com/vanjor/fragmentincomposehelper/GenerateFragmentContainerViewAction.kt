package com.vanjor.fragmentincomposehelper

import com.intellij.notification.*
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.vanjor.fragmentincomposehelper.ui.FileCreationDialog
import com.vanjor.fragmentincomposehelper.util.XMLUtil
import com.vanjor.fragmentincomposehelper.util.XMLUtil.camelToSnake
import com.vanjor.fragmentincomposehelper.util.XMLUtil.generateAndroidViewBindingSnippet
import org.jetbrains.kotlin.psi.KtFile
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.IOException


class GenerateFragmentContainerViewAction: AnAction(), FileCreationDialog.DialogInputCallback {

    private var fileName = ""
    private var viewId = ""

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        try {
            e.project?.let {
                val kotlinFile = e.getData(CommonDataKeys.PSI_FILE) as KtFile
                var layoutXmlFileName = ""

                kotlinFile.importList?.imports?.forEach { import ->
                    if (import.importedName.toString().contains("Binding")) {
                        layoutXmlFileName = XMLUtil.xmlFileNameFromBinding(import.importedName.toString())
                    }
                }

                val fragmentClass = kotlinFile.name.split('.').first()
                val fragmentClassFull = "${kotlinFile.packageDirective?.qualifiedName}.$fragmentClass"
                val xmlParentDir = FilenameIndex.getVirtualFilesByName(layoutXmlFileName, GlobalSearchScope.projectScope(e.project!!)).first().parent

                val defaultName = "${kotlinFile.name.split('.').first().camelToSnake()}_layout.xml"
                val defaultViewId = "${layoutXmlFileName.split('.').first()}_container_view"

                val dialog = FileCreationDialog(it, defaultName, defaultViewId, this@GenerateFragmentContainerViewAction)
                if (dialog.showAndGet()) {
                    val fileContent = XMLUtil.generateFragmentContainerXml(this.viewId, fragmentClassFull)

                    WriteCommandAction.runWriteCommandAction(e.project, "Write XML", "FRAGMENT_IN_COMPOSE_HELPER_ID", {
                        val file = xmlParentDir.findOrCreateChildData(this@GenerateFragmentContainerViewAction, this.fileName)
                        file.setBinaryContent(fileContent.toByteArray())
                        FileEditorManager.getInstance(it).openFile(file)
                        showNotificationWithCopyAction(it, fragmentClass, this.fileName, this.viewId)
                    })
                }
            }

        } catch (ex: IOException) {
            Messages.showErrorDialog(e.project, "Error creating FragmentContainerView XML file: ${ex.message}", "Error")
        }
    }

    override fun onOk(inputName: String, inputId: String) {
        this.fileName = inputName
        this.viewId = inputId
    }

    private fun showNotificationWithCopyAction(project: Project, fragmentClass: String, fileName: String, viewId: String) {

        val clipBoardContent = generateAndroidViewBindingSnippet(fragmentClass, fileName, viewId)

        val notification = NOTIFICATION_GROUP.createNotification(NOTIFICATION_TITLE, NOTIFICATION_SUCCESS_CONTENT, NotificationType.INFORMATION)
            .addAction(object : NotificationAction(NOTIFICATION_COPY_ACTION_TEXT) {
                override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                    val stringSelection = StringSelection(clipBoardContent)
                    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                    clipboard.setContents(stringSelection, null)
                    notification.expire()
                }
            })

        notification.notify(project)
    }

    override fun update(e: AnActionEvent) {
        val psiFile = e.dataContext.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = psiFile != null && isFragmentFile(psiFile)
    }

    private fun isFragmentFile(psiFile: PsiFile): Boolean {
        return psiFile.name.contains("Fragment")
    }

    companion object {
        const val NOTIFICATION_TITLE = "Fragment in Compose Helper"
        const val NOTIFICATION_SUCCESS_CONTENT = "XML file created successfully"
        const val NOTIFICATION_COPY_ACTION_TEXT = "Copy AndroidViewBinding Snippet"
        val NOTIFICATION_GROUP = NotificationGroup("com.vanjor.fragmentincomposehelper.notifications",NotificationDisplayType.BALLOON)
    }
}
