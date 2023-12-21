package com.star.easydoc.view.settings.javadoc.template;

import java.util.Objects;
import java.util.TreeMap;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfig.TemplateConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

/**
 * FieldSettingsConfigurable 是一个用于设置文档生成字段模板的可配置类，
 * 继承自 AbstractTemplateConfigurable 并实现了对应的方法。
 */
public class FieldSettingsConfigurable extends AbstractTemplateConfigurable<FieldSettingsView> {
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private FieldSettingsView view = new FieldSettingsView(config);

    /**
     * 获取可配置类显示的名称。
     * @return 可配置类显示的名称
     */
    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyDocFieldTemplate";
    }

    /**
     * 获取可配置类对应的视图。
     * @return 可配置类对应的视图
     */
    @Override
    public FieldSettingsView getView() {
        return view;
    }

    /**
     * 检查配置是否被修改。
     * @return 配置是否被修改
     */
    @Override
    public boolean isModified() {
        TemplateConfig templateConfig = config.getFieldTemplateConfig();
        if (!Objects.equals(templateConfig.getIsDefault(), view.isDefault())) {
            return true;
        }
        if (!Objects.equals(templateConfig.getTemplate(), view.getTemplate())) {
            return true;
        }
        return false;
    }

    /**
     * 应用配置更改。将视图中修改的配置应用到实际配置中。
     * @throws ConfigurationException 配置异常，如自定义模板为空或格式不正确
     */
    @Override
    public void apply() throws ConfigurationException {
        TemplateConfig templateConfig = config.getFieldTemplateConfig();
        templateConfig.setIsDefault(view.isDefault());
        templateConfig.setTemplate(view.getTemplate());
        if (templateConfig.getCustomMap() == null) {
            templateConfig.setCustomMap(new TreeMap<>());
        }
        if (!view.isDefault()) {
            if (StringUtils.isBlank(view.getTemplate())) {
                throw new ConfigurationException("使用自定义模板，模板不能为空");
            }
            String temp = StringUtils.strip(view.getTemplate());
            if (!temp.startsWith("/**") || !temp.endsWith("*/")) {
                throw new ConfigurationException("模板格式不正确，正确的javadoc应该以\"/**\"开头，以\"*/\"结束");
            }
        }
    }

    /**
     * 将配置重置为默认值。将实际配置中的值恢复到视图的默认状态。
     */
    @Override
    public void reset() {
        TemplateConfig templateConfig = config.getFieldTemplateConfig();
        if (BooleanUtils.isTrue(templateConfig.getIsDefault())) {
            view.setDefault(true);
        } else {
            view.setDefault(false);
        }
        view.setTemplate(templateConfig.getTemplate());
    }
}
