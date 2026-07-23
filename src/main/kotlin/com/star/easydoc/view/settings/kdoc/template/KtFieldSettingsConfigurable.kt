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
class KtFieldSettingsConfigurable : AbstractTemplateConfigurable<KtFieldSettingsView>() {
    private val config = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    /** Lazy: Settings tree instantiates Configurable without building UI */
    private var ktFieldConfigView: KtFieldSettingsView? = null

    override fun getView(): KtFieldSettingsView {
        if (ktFieldConfigView == null) {
            ktFieldConfigView = KtFieldSettingsView(config)
        }
        return ktFieldConfigView!!
    }

    override fun getDisplayName(): String {
        return "EasyDocKtFieldTemplate"
    }

    override fun isModified(): Boolean {
        val settingsView = ktFieldConfigView ?: return false
        val templateConfig = config.kdocFieldTemplateConfig
        if (templateConfig.isDefault != settingsView.isDefault) {
            return true
        }
        return templateConfig.template != settingsView.template
    }

    override fun apply() {
        val settingsView = getView()
        val templateConfig = config.kdocFieldTemplateConfig
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
        val templateConfig = config.kdocFieldTemplateConfig
        settingsView.isDefault = BooleanUtils.isTrue(templateConfig.isDefault)
        settingsView.template = templateConfig.template
    }
}
