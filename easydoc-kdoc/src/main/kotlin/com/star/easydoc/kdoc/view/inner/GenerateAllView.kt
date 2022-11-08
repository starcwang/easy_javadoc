package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-10-28 00:30:00
 */
class GenerateAllView : DialogWrapper(false) {
    lateinit var fieldCheckBox: JCheckBox
    lateinit var methodCheckBox: JCheckBox
    lateinit var classCheckBox: JCheckBox
    lateinit var panel: JPanel
    lateinit var innerClassCheckBox: JCheckBox

    init {
        init()
        title = "请选择生成内容"
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}