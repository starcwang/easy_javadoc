package com.star.easydoc.view.settings;

import java.util.Objects;
import java.util.TreeMap;

import javax.swing.*;

import com.google.common.collect.Maps;
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
public class CommonSettingsConfigurable implements Configurable {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private CommonSettingsView view = new CommonSettingsView();

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyDoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public boolean isModified() {
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
        if (!Objects.equals(config.getYoudaoAppKey(), view.getYoudaoAppKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getYoudaoAppSecret(), view.getYoudaoAppSecretTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getMicrosoftKey(), view.getMicrosoftKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getGoogleKey(), view.getGoogleKeyTextField().getText())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setTranslator(String.valueOf(view.getTranslatorBox().getSelectedItem()));
        config.setAppId(view.getAppIdTextField().getText());
        config.setToken(view.getTokenTextField().getText());
        config.setSecretKey(view.getSecretKeyTextField().getText());
        config.setSecretId(view.getSecretIdTextField().getText());
        config.setAccessKeyId(view.getAccessKeyIdTextField().getText());
        config.setAccessKeySecret(view.getAccessKeySecretTextField().getText());
        config.setYoudaoAppKey(view.getYoudaoAppKeyTextField().getText());
        config.setYoudaoAppSecret(view.getYoudaoAppSecretTextField().getText());
        config.setMicrosoftKey(view.getMicrosoftKeyTextField().getText());
        config.setGoogleKey(view.getGoogleKeyTextField().getText());
        if (config.getWordMap() == null) {
            config.setWordMap(new TreeMap<>());
        }
        if (config.getProjectWordMap() == null) {
            config.setProjectWordMap(Maps.newTreeMap());
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
        if (Consts.YOUDAO_AI_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getYoudaoAppKey())) {
                throw new ConfigurationException("appKey不能为空");
            }
            if (StringUtils.isBlank(config.getYoudaoAppSecret())) {
                throw new ConfigurationException("appSecret不能为空");
            }
        }
        if (Consts.MICROSOFT_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getMicrosoftKey())) {
                throw new ConfigurationException("microsoftKey不能为空");
            }
        }
        if (Consts.GOOGLE_TRANSLATOR.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getGoogleKey())) {
                throw new ConfigurationException("googleKey不能为空");
            }
        }
    }

    @Override
    public void reset() {
        view.refresh();
    }
}
