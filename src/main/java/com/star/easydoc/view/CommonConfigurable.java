package com.star.easydoc.view;

import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.*;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class CommonConfigurable implements Configurable {

    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();
    private CommonConfigView view = new CommonConfigView(config);
    private static final Set<String> ENABLE_TRANSLATOR_SET = ImmutableSet.of("有道翻译", "关闭（只使用自定义翻译）");


    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyJavadoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(config.getAuthor(), view.getAuthorTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getDateFormat(), view.getDateFormatTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSimpleFieldDoc(), view.getSimpleDocButton().isSelected())) {
            return true;
        }
        if (!Objects.equals(config.getTranslator(), view.getTranslatorBox().getSelectedItem())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setAuthor(view.getAuthorTextField().getText());
        config.setDateFormat(view.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(view.getSimpleDocButton().isSelected());
        config.setTranslator(String.valueOf(view.getTranslatorBox().getSelectedItem()));
        if (config.getWordMap() == null) {
            config.setWordMap(new TreeMap<>());
        }

        if (config.getAuthor() == null) {
            throw new ConfigurationException("作者不能为null");
        }
        if (config.getDateFormat() == null) {
            throw new ConfigurationException("日期格式不能为null");
        }
        if (config.getSimpleFieldDoc() == null) {
            throw new ConfigurationException("注释形式不能为null");
        }
        if (config.getTranslator() == null || !ENABLE_TRANSLATOR_SET.contains(config.getTranslator())) {
            throw new ConfigurationException("请选择正确的翻译方式");
        }
    }

    @Override
    public void reset() {
        view.refresh();
    }
}
