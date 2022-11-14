package com.star.easydoc.kdoc.view.template

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.ConfigurationException
import com.star.easydoc.kdoc.config.EasyKdocConfigComponent
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Nls
import java.util.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-10 17:35:00
 */
class ClassConfigurable : AbstractTemplateConfigurable() {
    private val config = ServiceManager.getService(EasyKdocConfigComponent::class.java).state
    private val classConfigView = ClassConfigView(config)

    override val view: ClassConfigView
        get() = classConfigView

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "类注释模板配置"
    }

    override fun isModified(): Boolean {
        val templateConfig = config.classTemplateConfig
        if (templateConfig.isDefault != view.isDefault) {
            return true
        }
        return templateConfig.template != view.template
    }

    override fun apply() {
        val templateConfig = config.classTemplateConfig
        templateConfig.isDefault = view.isDefault
        templateConfig.template = view.template
        if (templateConfig.customMap == null) {
            templateConfig.customMap = TreeMap()
        }
        if (!view.isDefault) {
            if (StringUtils.isBlank(view.template)) {
                throw ConfigurationException("使用自定义模板，模板不能为空")
            }
            val temp = StringUtils.strip(view.template)
            if (!temp.startsWith("/**") || !temp.endsWith("*/")) {
                throw ConfigurationException("模板格式不正确，正确的kdoc应该以\"/**\"开头，以\"*/\"结束")
            }
        }
    }

    override fun reset() {
        val templateConfig = config.classTemplateConfig
        view.isDefault = BooleanUtils.isTrue(templateConfig.isDefault)
        view.template = templateConfig.template
    }
}