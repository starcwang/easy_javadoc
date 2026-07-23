package com.star.easydoc.view.settings.kdoc.template

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.ConfigurationException
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.view.settings.javadoc.template.AbstractTemplateConfigurable
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-10 17:35:00
 */
class KtMethodSettingsConfigurable : AbstractTemplateConfigurable<KtMethodSettingsView>() {
    private val config = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    /** Lazy: Settings tree instantiates Configurable without building UI */
    private var ktMethodConfigView: KtMethodSettingsView? = null

    override fun getView(): KtMethodSettingsView {
        if (ktMethodConfigView == null) {
            ktMethodConfigView = KtMethodSettingsView(config)
        }
        return ktMethodConfigView!!
    }

    override fun getDisplayName(): String {
        return "EasyDocKtMethodTemplate"
    }

    override fun isModified(): Boolean {
        val settingsView = ktMethodConfigView ?: return false
        val templateConfig = config.kdocMethodTemplateConfig
        if (templateConfig.isDefault != settingsView.isDefault) {
            return true
        }
        return templateConfig.template != settingsView.template
    }

    override fun apply() {
        val settingsView = getView()
        val templateConfig = config.kdocMethodTemplateConfig
        templateConfig.isDefault = settingsView.isDefault
        templateConfig.template = settingsView.template
        if (templateConfig.customMap == null) {
            templateConfig.customMap = TreeMap()
        }
        if (!settingsView.isDefault) {
            if (StringUtils.isBlank(settingsView.template)) {
                throw ConfigurationException("使用自定义模板，模板不能为空")
            }
            val temp = StringUtils.strip(settingsView.template)
            if (!temp.startsWith("/**") || !temp.endsWith("*/")) {
                throw ConfigurationException("模板格式不正确，正确的kdoc应该以\"/**\"开头，以\"*/\"结束")
            }
        }
    }

    override fun reset() {
        val settingsView = getView()
        val templateConfig = config.kdocMethodTemplateConfig
        settingsView.isDefault = BooleanUtils.isTrue(templateConfig.isDefault)
        settingsView.template = templateConfig.template
    }
}
