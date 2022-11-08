package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

/**
 * @author wangchao
 * @date 2020/02/12
 */
class TranslateResultView(text: String?) : DialogWrapper(false) {
    private lateinit var textArea: JTextArea
    private lateinit var panel: JPanel
    private lateinit var scrollPane: JScrollPane

    init {
        init()
        title = "翻译结果"
        textArea.setSize(800, 800)
        textArea.text = text
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}