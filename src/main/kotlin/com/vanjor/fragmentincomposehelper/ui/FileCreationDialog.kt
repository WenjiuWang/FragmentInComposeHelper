package com.vanjor.fragmentincomposehelper.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.uiDesigner.core.AbstractLayout
import com.intellij.util.ui.GridBag
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class FileCreationDialog(
    project: Project,
    fileName: String,
    id: String,
    private val callBack: DialogInputCallback
): DialogWrapper(project) {

    private val centerPanel: JPanel = JPanel(GridBagLayout())

    private val fileNameField = JTextField(fileName)
    private val idField = JTextField(id)

    init {
        title = "Generate FragmentContainerView XML"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val gridBag = GridBag()
            .setDefaultWeightX(1.0)
            .setDefaultFill(GridBagConstraints.HORIZONTAL)
            .setDefaultInsets(JBUI.insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))

        centerPanel.add(JBLabel("New XML file name:"), gridBag.nextLine().next().weightx(0.2))
        centerPanel.add(fileNameField, gridBag.next().weightx(0.8))

        centerPanel.add(JBLabel("Container view ID:"), gridBag.nextLine().next().weightx(0.2))
        centerPanel.add(idField, gridBag.next().weightx(0.8))

        return centerPanel
    }

    override fun doOKAction() {
        super.doOKAction()
        callBack.onOk(fileNameField.text, idField.text)
    }

    interface DialogInputCallback {
        fun onOk(inputName: String, inputId: String)
    }
}