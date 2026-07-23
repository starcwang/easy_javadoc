package com.star.easydoc.view.settings.kdoc

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.star.easydoc.config.EasyDocConfigComponent
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 *
 * @author wangchao
 * @date 2022/12/04
 */
class KdocSettingsConfigurable : Configurable {

    private val config = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    /** Lazy: Settings tree instantiates Configurable without building UI */
    private var view: KdocSettingsView? = null

    private fun getView(): KdocSettingsView {
        if (view == null) {
            view = KdocSettingsView()
        }
        return view!!
    }

    override fun createComponent(): JComponent {
        return getView().component
    }

    override fun isModified(): Boolean {
        val settingsView = view ?: return false
        if (config.kdocAuthor != settingsView.getAuthorTextField()) {
            return true
        }
        if (config.kdocDateFormat != settingsView.getDateFormatTextField()) {
            return true
        }
        if (config.kdocParamType != settingsView.getKdocParamType()) {
            return true
        }
        if (config.kdocSimpleFieldDoc != settingsView.getKdocSimpleFieldDoc()) {
            return true
        }
        return false
    }

    override fun apply() {
        val settingsView = getView()
        config.kdocAuthor = settingsView.getAuthorTextField()
        config.kdocDateFormat = settingsView.getDateFormatTextField()
        config.kdocSimpleFieldDoc = settingsView.getKdocSimpleFieldDoc()
        config.kdocParamType = settingsView.getKdocParamType()

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

    @Nls
    override fun getDisplayName(): String {
        return "EasyDocKdoc"
    }

    override fun reset() {
        getView().refresh()
    }
}
