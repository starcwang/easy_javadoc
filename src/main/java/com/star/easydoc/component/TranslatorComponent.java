package com.star.easydoc.component;

import java.util.HashMap;
import java.util.Objects;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.config.EasyJavadocConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = {@Storage("$APP_CONFIG$/easyJavadoc.xml")})
public class TranslatorComponent implements PersistentStateComponent<EasyJavadocConfiguration> {

    private EasyJavadocConfiguration configuration;

    @Nullable
    @Override
    public EasyJavadocConfiguration getState() {
        if (configuration == null) {
            configuration = new EasyJavadocConfiguration();
            configuration.setWordMap(new HashMap<>());
        }
        return configuration;
    }

    @Override
    public void loadState(@NotNull EasyJavadocConfiguration state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

    public EasyJavadocConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(EasyJavadocConfiguration configuration) {
        this.configuration = configuration;
    }
}
