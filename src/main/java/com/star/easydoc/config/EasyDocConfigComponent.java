package com.star.easydoc.config;

import java.util.Objects;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.config.EasyDocConfig.TemplateConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
@State(name = "easyJavadoc", storages = {@Storage("easyJavadoc.xml")})
public class EasyDocConfigComponent implements PersistentStateComponent<EasyDocConfig> {
    /** 配置 */
    private EasyDocConfig config;

    @Nullable
    @Override
    public EasyDocConfig getState() {
        if (config == null) {
            config = new EasyDocConfig();
            config.setAuthor(System.getProperty("user.name"));
            config.setKdocAuthor(System.getProperty("user.name"));
            config.setDateFormat(Consts.DEFAULT_DATE_FORMAT);
            config.setDocPriority(EasyDocConfig.DOC_FIRST);
            config.setKdocDateFormat(Consts.DEFAULT_DATE_FORMAT);
            config.setSimpleFieldDoc(true);
            config.setKdocSimpleFieldDoc(true);
            config.setKdocParamType(EasyDocConfig.LINK_PARAM_TYPE);
            config.setMethodReturnType(EasyDocConfig.LINK_RETURN_TYPE);
            config.setWordMap(Maps.newTreeMap());
            config.setProjectWordMap(Maps.newTreeMap());
            config.setTranslator(Consts.YOUDAO_TRANSLATOR);
            config.setClassTemplateConfig(new TemplateConfig());
            config.setKdocClassTemplateConfig(new TemplateConfig());
            config.setMethodTemplateConfig(new TemplateConfig());
            config.setKdocMethodTemplateConfig(new TemplateConfig());
            config.setFieldTemplateConfig(new TemplateConfig());
            config.setKdocFieldTemplateConfig(new TemplateConfig());
        }
        return config;
    }

    @Override
    public void loadState(@NotNull EasyDocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
