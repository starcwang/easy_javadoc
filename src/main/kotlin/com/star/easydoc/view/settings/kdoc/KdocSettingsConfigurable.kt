package com.star.easydoc.view.settings.kdoc

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.star.easydoc.config.EasyDocConfigComponent
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization
import javax.swing.JComponent

/**
 *
 * @author wangchao
 * @date 2022/12/04
 */
class KdocSettingsConfigurable : Configurable {

    private val config = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    private val view = KdocSettingsView()

    override fun createComponent(): JComponent {
        return view.component
    }

    override fun isModified(): Boolean {
        if (config.kdocAuthor != view.getAuthorTextField()) {
            return true
        }
        if (config.kdocDateFormat != view.getDateFormatTextField()) {
            return true
        }
        if (config.kdocParamType != view.getKdocParamType()) {
            return true
        }
        if (config.kdocSimpleFieldDoc != view.getKdocSimpleFieldDoc()) {
            return true
        }
        return false
    }

    override fun apply() {
        config.kdocAuthor = view.getAuthorTextField()
        config.kdocDateFormat = view.getDateFormatTextField()
        config.kdocSimpleFieldDoc = view.getKdocSimpleFieldDoc()
        config.kdocParamType = view.getKdocParamType()

        if (config.kdocAuthor == null) {
            throw ConfigurationException("作者不能为null")
        }
        if (config.kdocDateFormat == null) {
            throw ConfigurationException("日期格式不能为null")
        }
        if (config.kdocSimpleFieldDoc == null) {
            throw ConfigurationException("方法注释形式不能为null")
        }
        if (config.kdocParamType == null) {
            throw ConfigurationException("参数模式不能为null")
        }
    }

    @Nls(capitalization = Capitalization.Title)
    override fun getDisplayName(): String {
        return "EasyDocKdoc"
    }

    override fun reset() {
        view.refresh()
    }
}