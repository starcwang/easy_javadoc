package com.star.easydoc.javadoc.config;

import java.util.Objects;
import java.util.TreeMap;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.config.EasyDocConfig;
import com.star.easydoc.common.config.EasyDocConfig.TemplateConfig;
import com.star.easydoc.javadoc.listener.AppActivationListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = {@Storage("easyJavadoc.xml")})
public class EasyJavadocConfigComponent implements PersistentStateComponent<EasyDocConfig> {
    /** 配置 */
    private EasyDocConfig config;

    @Nullable
    @Override
    public EasyDocConfig getState() {
        if (config == null) {
            config = new EasyDocConfig();
            config.setAuthor(System.getProperty("user.name"));
            config.setDateFormat(Consts.DEFAULT_DATE_FORMAT);
            config.setSimpleFieldDoc(true);
            config.setMethodReturnType(EasyDocConfig.LINK_RETURN_TYPE);
            config.setWordMap(new TreeMap<>());
            config.setTranslator(Consts.YOUDAO_TRANSLATOR);
            config.setClassTemplateConfig(new TemplateConfig());
            config.setMethodTemplateConfig(new TemplateConfig());
            config.setFieldTemplateConfig(new TemplateConfig());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull EasyDocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));

        // 设置消息监听
        Application app = ApplicationManager.getApplication();
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(app, disposable);
        MessageBusConnection connection = app.getMessageBus().connect(disposable);
        connection.subscribe(ApplicationActivationListener.TOPIC, new AppActivationListener());
    }

}
