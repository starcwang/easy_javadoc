package com.star.easydoc.kdoc.config

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.Disposer
import com.intellij.util.xmlb.XmlSerializerUtil
import com.star.easydoc.common.Consts
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.common.config.EasyDocConfig.TemplateConfig
import com.star.easydoc.kdoc.listener.AppActivationListener
import java.util.*

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = [Storage("easyJavadoc.xml")])
class EasyJavadocConfigComponent : PersistentStateComponent<EasyDocConfig> {
    /** 配置  */
    private lateinit var config: EasyDocConfig

    override fun getState(): EasyDocConfig {
        config = EasyDocConfig()
        config.author = System.getProperty("user.name")
        config.dateFormat = Consts.DEFAULT_DATE_FORMAT
        config.simpleFieldDoc = true
        config.methodReturnType = EasyDocConfig.LINK_RETURN_TYPE
        config.wordMap = TreeMap()
        config.translator = Consts.YOUDAO_TRANSLATOR
        config.classTemplateConfig = TemplateConfig()
        config.methodTemplateConfig = TemplateConfig()
        config.fieldTemplateConfig = TemplateConfig()
        return config
    }

    override fun loadState(state: EasyDocConfig) {
        XmlSerializerUtil.copyBean<EasyDocConfig?>(state, Objects.requireNonNull(getState())!!)

        // 设置消息监听
        val app = ApplicationManager.getApplication()
        val disposable = Disposer.newDisposable()
        Disposer.register(app, disposable)
        val connection = app.messageBus.connect(disposable)
        connection.subscribe(ApplicationActivationListener.TOPIC, AppActivationListener())
    }
}