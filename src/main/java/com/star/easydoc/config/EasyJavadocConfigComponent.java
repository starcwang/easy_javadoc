package com.star.easydoc.config;

import java.util.HashMap;
import java.util.Objects;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.model.EasyJavadocConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = {@Storage("easyJavadoc.xml")})
public class EasyJavadocConfigComponent implements PersistentStateComponent<EasyJavadocConfiguration> {

    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
    private EasyJavadocConfiguration configuration;

    @Nullable
    @Override
    public EasyJavadocConfiguration getState() {
        if (configuration == null) {
            configuration = new EasyJavadocConfiguration();
            configuration.setAuthor(System.getProperty("user.name"));
            configuration.setDateFormat(DEFAULT_DATE_FORMAT);
            configuration.setSimpleFieldDoc(true);
            configuration.setWordMap(new HashMap<>(8));
            configuration.setTranslator("有道翻译");
        }
        return configuration;
    }

    @Override
    public void loadState(@NotNull EasyJavadocConfiguration state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }
}
