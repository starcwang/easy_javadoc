package com.star.easydoc.javadoc.view.settings;

import java.util.Objects;
import java.util.TreeMap;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.common.Consts;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class CommonConfigurable implements Configurable {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private CommonSettingsView view = new CommonSettingsView();

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
        if (!Objects.equals(config.getMethodReturnType(), view.getMethodReturnType())) {
            return true;
        }
        if (!Objects.equals(config.getKdocParamType(), view.getParamTypeBox().getSelectedItem())) {
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
        if (!Objects.equals(config.getAccessKeyId(), view.getAccessKeyIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getAccessKeySecret(), view.getAccessKeySecretTextField().getText())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setAuthor(view.getAuthorTextField().getText());
        config.setDateFormat(view.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(view.getSimpleDocButton().isSelected());
        config.setMethodReturnType(view.getMethodReturnType());
        config.setTranslator(String.valueOf(view.getTranslatorBox().getSelectedItem()));
        config.setAppId(view.getAppIdTextField().getText());
        config.setToken(view.getTokenTextField().getText());
        config.setSecretKey(view.getSecretKeyTextField().getText());
        config.setSecretId(view.getSecretIdTextField().getText());
        config.setAccessKeyId(view.getAccessKeyIdTextField().getText());
        config.setAccessKeySecret(view.getAccessKeySecretTextField().getText());
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
        if (config.getTranslator() == null || !Consts.ENABLE_TRANSLATOR_SET.contains(config.getTranslator())) {
            throw new ConfigurationException("请选择正确的翻译方式");
        }
        if (Consts.BAIDU_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getAppId())) {
                throw new ConfigurationException("appId不能为空");
            }
            if (StringUtils.isBlank(config.getToken())) {
                throw new ConfigurationException("密钥不能为空");
            }
        }
        if (Consts.TENCENT_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getSecretKey())) {
                throw new ConfigurationException("secretKey不能为空");
            }
            if (StringUtils.isBlank(config.getSecretId())) {
                throw new ConfigurationException("secretId不能为空");
            }
        }
        if (Consts.ALIYUN_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getAccessKeyId())) {
                throw new ConfigurationException("accessKeyId不能为空");
            }
            if (StringUtils.isBlank(config.getAccessKeySecret())) {
                throw new ConfigurationException("accessKeySecret不能为空");
            }
        }
    }

    @Override
    public void reset() {
        view.refresh();
    }
}
