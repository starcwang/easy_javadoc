package com.star.easydoc.javadoc.config;

import java.util.Objects;
import java.util.TreeMap;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.config.EasyDocConfig.TemplateConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = {@Storage("easyJavadoc.xml")})
public class EasyJavadocConfigComponent implements PersistentStateComponent<EasyJavadocConfig> {
    /** 配置 */
    private EasyJavadocConfig config;

    @Nullable
    @Override
    public EasyJavadocConfig getState() {
        if (config == null) {
            config = new EasyJavadocConfig();
            config.setAuthor(System.getProperty("user.name"));
            config.setDateFormat(Consts.DEFAULT_DATE_FORMAT);
            config.setSimpleFieldDoc(true);
            config.setMethodReturnType(EasyJavadocConfig.LINK_RETURN_TYPE);
            config.setWordMap(new TreeMap<>());
            config.setTranslator(Consts.YOUDAO_TRANSLATOR);
            config.setClassTemplateConfig(new TemplateConfig());
            config.setMethodTemplateConfig(new TemplateConfig());
            config.setFieldTemplateConfig(new TemplateConfig());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull EasyJavadocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
