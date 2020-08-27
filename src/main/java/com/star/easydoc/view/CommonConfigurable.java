package com.star.easydoc.view;

import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.*;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.Consts;
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
    private CommonConfigView view = new CommonConfigView();
    private static final Set<String> ENABLE_TRANSLATOR_SET = ImmutableSet.of(Consts.YOUDAO_TRANSLATOR,
        Consts.BAIDU_TRANSLATOR, Consts.TENCENT_TRANSLATOR, Consts.CLOSE_TRANSLATOR);


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
        if (!Objects.equals(config.getAppId(), view.getAppIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getToken(), view.getTokenTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSecretKey(), view.getSecretKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSecretId(), view.getSecretIdTextField().getText())) {
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
        config.setAppId(view.getAppIdTextField().getText());
        config.setToken(view.getTokenTextField().getText());
        config.setSecretKey(view.getSecretKeyTextField().getText());
        config.setSecretId(view.getSecretIdTextField().getText());
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
        if (Consts.BAIDU_TRANSLATOR.equals(config.getTranslator())) {
            if (config.getAppId() == null) {
                throw new ConfigurationException("appId不能为null");
            }
            if (config.getToken() == null) {
                throw new ConfigurationException("密钥不能为null");
            }
        }
        if (Consts.TENCENT_TRANSLATOR.equals(config.getTranslator())) {
            if (config.getSecretKey() == null) {
                throw new ConfigurationException("secretKey不能为null");
            }
            if (config.getSecretId() == null) {
                throw new ConfigurationException("secretId不能为null");
            }
        }
    }

    @Override
    public void reset() {
        view.refresh();
    }
}
