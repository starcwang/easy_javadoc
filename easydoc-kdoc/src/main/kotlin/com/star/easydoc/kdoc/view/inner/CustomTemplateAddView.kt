package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.common.config.EasyDocConfig.VariableType
import java.util.AbstractMap.SimpleEntry
import javax.swing.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-12 00:22:00
 */
class CustomTemplateAddView : DialogWrapper(false) {
    private lateinit var panel: JPanel
    private lateinit var methodName: JTextField
    private lateinit var groovyCode: JTextField
    private lateinit var textPane: JTextPane
    private lateinit var customTypeComboBox: JComboBox<String>

    init {
        init()
        title = "添加自定义方法"
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    override fun doValidate(): ValidationInfo? {
        if (methodName.text == null || methodName.text.length <= 0 || !methodName.text.startsWith("$") || !methodName.text.endsWith("$")) {
            return ValidationInfo("请输入方法名，并用\$前后包裹，例如:\$NAME\$", methodName)
        }
        if (groovyCode.text == null || groovyCode.text.length <= 0) {
            return ValidationInfo("请输入正确的自定义脚本", groovyCode)
        }
        if (customTypeComboBox.selectedItem == null) {
            return ValidationInfo("请选择自定义类型", groovyCode)
        }
        return super.doValidate()
    }

    val entry: Map.Entry<String, EasyDocConfig.CustomValue>
        get() = SimpleEntry(
            methodName.text,
            EasyDocConfig.CustomValue(VariableType.fromDesc(customTypeComboBox.selectedItem!!.toString()), groovyCode.text)
        )
}