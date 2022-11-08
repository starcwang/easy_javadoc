package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.util.AbstractMap.SimpleEntry
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * @author wangchao
 * @date 2019/08/25
 */
class WordMapAddView : DialogWrapper(false) {
    private lateinit var panel: JPanel
    private lateinit var sourceTextField: JTextField
    private lateinit var targetTextField: JTextField
    private lateinit var source: JLabel
    private lateinit var target: JLabel

    init {
        init()
        title = "添加单词映射"
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    override fun doValidate(): ValidationInfo? {
        if (sourceTextField.text == null || sourceTextField.text.isEmpty()) {
            return ValidationInfo("请输入原单词", sourceTextField)
        }
        return if (targetTextField.text == null || targetTextField.text.isEmpty()) {
            ValidationInfo("请输入转换后的单词", targetTextField)
        } else super.doValidate()
    }

    val mapping: Map.Entry<String, String>
        get() = SimpleEntry(sourceTextField.text.toLowerCase(), targetTextField.text)
}