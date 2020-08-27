package com.star.easydoc.config;

import java.util.Objects;
import java.util.TreeMap;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.model.EasyJavadocConfiguration.TemplateConfig;
import org.apache.commons.lang3.StringUtils;
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
            configuration.setWordMap(new TreeMap<>());
            configuration.setTranslator(Consts.YOUDAO_TRANSLATOR);

            TemplateConfig config = new TemplateConfig();
            config.setIsDefault(true);
            config.setTemplate(StringUtils.EMPTY);
            config.setCustomMap(new TreeMap<>());
            configuration.setClassTemplateConfig(config);
            configuration.setMethodTemplateConfig(config);
            configuration.setFieldTemplateConfig(config);
        }
        return configuration;
    }

    @Override
    public void loadState(@NotNull EasyJavadocConfiguration state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }
}
