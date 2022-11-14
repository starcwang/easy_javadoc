package com.star.easydoc.kdoc.view

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.star.easydoc.common.Consts
import com.star.easydoc.kdoc.config.EasyKdocConfigComponent
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Nls
import java.util.*
import javax.swing.JComponent

/**
 * @author wangchao
 * @date 2019/08/25
 */
class CommonConfigurable : Configurable {
    private val config = ServiceManager.getService(EasyKdocConfigComponent::class.java).state
    private val view = CommonConfigView()

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "EasyJavadoc"
    }

    override fun createComponent(): JComponent? {
        return view.component
    }

    override fun isModified(): Boolean {
        if (config.author != view.authorTextField.text) {
            return true
        }
        if (config.dateFormat != view.dateFormatTextField.text) {
            return true
        }
        if (config.simpleFieldDoc != view.simpleDocButton.isSelected) {
            return true
        }
        if (config.methodReturnType != view.methodReturnType) {
            return true
        }
        if (config.translator != view.translatorBox.selectedItem) {
            return true
        }
        if (config.appId != view.appIdTextField.text) {
            return true
        }
        if (config.token != view.tokenTextField.text) {
            return true
        }
        if (config.secretKey != view.secretKeyTextField.text) {
            return true
        }
        if (config.secretId != view.secretIdTextField.text) {
            return true
        }
        if (config.accessKeyId != view.accessKeyIdTextField.text) {
            return true
        }
        return if (config.accessKeySecret != view.accessKeySecretTextField.text) {
            true
        } else false
    }

    override fun apply() {
        config.author = view.authorTextField.text
        config.dateFormat = view.dateFormatTextField.text
        config.simpleFieldDoc = view.simpleDocButton.isSelected
        config.methodReturnType = view.methodReturnType
        config.translator = view.translatorBox.selectedItem!!.toString()
        config.appId = view.appIdTextField.text
        config.token = view.tokenTextField.text
        config.secretKey = view.secretKeyTextField.text
        config.secretId = view.secretIdTextField.text
        config.accessKeyId = view.accessKeyIdTextField.text
        config.accessKeySecret = view.accessKeySecretTextField.text
        if (config.wordMap == null) {
            config.wordMap = TreeMap()
        }
        if (config.author == null) {
            throw ConfigurationException("作者不能为null")
        }
        if (config.dateFormat == null) {
            throw ConfigurationException("日期格式不能为null")
        }
        if (config.simpleFieldDoc == null) {
            throw ConfigurationException("注释形式不能为null")
        }
        if (config.translator == null || !Consts.ENABLE_TRANSLATOR_SET.contains(config.translator)) {
            throw ConfigurationException("请选择正确的翻译方式")
        }
        if (Consts.BAIDU_TRANSLATOR == config.translator) {
            if (StringUtils.isBlank(config.appId)) {
                throw ConfigurationException("appId不能为空")
            }
            if (StringUtils.isBlank(config.token)) {
                throw ConfigurationException("密钥不能为空")
            }
        }
        if (Consts.TENCENT_TRANSLATOR == config.translator) {
            if (StringUtils.isBlank(config.secretKey)) {
                throw ConfigurationException("secretKey不能为空")
            }
            if (StringUtils.isBlank(config.secretId)) {
                throw ConfigurationException("secretId不能为空")
            }
        }
        if (Consts.ALIYUN_TRANSLATOR == config.translator) {
            if (StringUtils.isBlank(config.accessKeyId)) {
                throw ConfigurationException("accessKeyId不能为空")
            }
            if (StringUtils.isBlank(config.accessKeySecret)) {
                throw ConfigurationException("accessKeySecret不能为空")
            }
        }
    }

    override fun reset() {
        view.refresh()
    }
}