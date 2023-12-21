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

//这个类是一个配置类，用于处理 EasyDoc 插件的一些通用设置。


public class CommonSettingsConfigurable implements Configurable {
    // 获取配置信息
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    // 获取视图
    private CommonSettingsView view = new CommonSettingsView();
    // 获取显示名称
    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyDoc";
    }
    // 创建组件
    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getComponent();
    }
    // 判断是否修改
    @Override
    public boolean isModified() {
        // 判断是否修改了翻译方式
        if (!Objects.equals(config.getTranslator(), view.getTranslatorBox().getSelectedItem())) {
            return true;
        }
        // 判断是否修改了appId
        if (!Objects.equals(config.getAppId(), view.getAppIdTextField().getText())) {
            return true;
        }
        // 判断是否修改了密钥
        if (!Objects.equals(config.getToken(), view.getTokenTextField().getText())) {
            return true;
        }
        // 判断是否修改了secretKey
        if (!Objects.equals(config.getSecretKey(), view.getSecretKeyTextField().getText())) {
            return true;
        }
        // 判断是否修改了secretId
        if (!Objects.equals(config.getSecretId(), view.getSecretIdTextField().getText())) {
            return true;
        }
        // 判断是否修改了accessKeyId
        if (!Objects.equals(config.getAccessKeyId(), view.getAccessKeyIdTextField().getText())) {
            return true;
        }
        // 判断是否修改了accessKeySecret
        if (!Objects.equals(config.getAccessKeySecret(), view.getAccessKeySecretTextField().getText())) {
            return true;
        }
        // 判断是否修改了有道appKey
        if (!Objects.equals(config.getYoudaoAppKey(), view.getYoudaoAppKeyTextField().getText())) {
            return true;
        }
        // 判断是否修改了有道appSecret
        if (!Objects.equals(config.getYoudaoAppSecret(), view.getYoudaoAppSecretTextField().getText())) {
            return true;
        }
        // 判断是否修改了微软key
        if (!Objects.equals(config.getMicrosoftKey(), view.getMicrosoftKeyTextField().getText())) {
            return true;
        }
        // 判断是否修改了谷歌key
        if (!Objects.equals(config.getGoogleKey(), view.getGoogleKeyTextField().getText())) {
            return true;
        }
        return false;
    }
    // 应用修改
    @Override
    public void apply() throws ConfigurationException {
        config.setTranslator(String.valueOf(view.getTranslatorBox().getSelectedItem()));// 设置翻译方式
        config.setAppId(view.getAppIdTextField().getText());// 设置appId
        config.setToken(view.getTokenTextField().getText());// 设置密钥
        config.setSecretKey(view.getSecretKeyTextField().getText());// 设置secretKey
        config.setSecretId(view.getSecretIdTextField().getText());// 设置secretId
        config.setAccessKeyId(view.getAccessKeyIdTextField().getText());// 设置accessKeyId
        config.setAccessKeySecret(view.getAccessKeySecretTextField().getText());// 设置accessKeySecret
        config.setYoudaoAppKey(view.getYoudaoAppKeyTextField().getText());// 设置有道appKey
        config.setYoudaoAppSecret(view.getYoudaoAppSecretTextField().getText());// 设置有道appSecret
        config.setMicrosoftKey(view.getMicrosoftKeyTextField().getText());// 设置微软key
        config.setGoogleKey(view.getGoogleKeyTextField().getText());// 设置谷歌key
        // 如果wordMap为空，则新建一个TreeMap
        if (config.getWordMap() == null) {
            config.setWordMap(new TreeMap<>());
        }
        // 如果projectWordMap为空，则新建一个TreeMap
        if (config.getProjectWordMap() == null) {
            config.setProjectWordMap(Maps.newTreeMap());
        }
        // 判断翻译方式是否为空或者是否正确
        if (config.getTranslator() == null || !Consts.ENABLE_TRANSLATOR_SET.contains(config.getTranslator())) {
            throw new ConfigurationException("请选择正确的翻译方式");
        }
        // 判断翻译方式是否为百度翻译
        if (Consts.BAIDU_TRANSLATOR.equals(config.getTranslator())) {
            // 判断appId是否为空
            if (StringUtils.isBlank(config.getAppId())) {
                throw new ConfigurationException("appId不能为空");
            }
            // 判断密钥是否为空
            if (StringUtils.isBlank(config.getToken())) {
                throw new ConfigurationException("密钥不能为空");
            }
        }
        // 判断翻译方式是否为腾讯翻译
        if (Consts.TENCENT_TRANSLATOR.equals(config.getTranslator())) {
            // 判断secretKey是否为空
            if (StringUtils.isBlank(config.getSecretKey())) {
                throw new ConfigurationException("secretKey不能为空");
            }
            // 判断secretId是否为空
            if (StringUtils.isBlank(config.getSecretId())) {
                throw new ConfigurationException("secretId不能为空");
            }
        }
        // 判断翻译方式是否为阿里云翻译
        if (Consts.ALIYUN_TRANSLATOR.equals(config.getTranslator())) {
            // 判断accessKeyId是否为空
            if (StringUtils.isBlank(config.getAccessKeyId())) {
                throw new ConfigurationException("accessKeyId不能为空");
            }
            // 判断accessKeySecret是否为空
            if (StringUtils.isBlank(config.getAccessKeySecret())) {
                throw new ConfigurationException("accessKeySecret不能为空");
            }
        }
        // 判断翻译方式是否为有道AI翻译
        if (Consts.YOUDAO_AI_TRANSLATOR.equals(config.getTranslator())) {
            // 判断appKey是否为空
            if (StringUtils.isBlank(config.getYoudaoAppKey())) {
                throw new ConfigurationException("appKey不能为空");
            }
            // 判断appSecret是否为空
            if (StringUtils.isBlank(config.getYoudaoAppSecret())) {
                throw new ConfigurationException("appSecret不能为空");
            }
        }
        // 判断翻译方式是否为微软翻译
        if (Consts.MICROSOFT_TRANSLATOR.equals(config.getTranslator())) {
            // 判断microsoftKey是否为空
            if (StringUtils.isBlank(config.getMicrosoftKey())) {
                throw new ConfigurationException("microsoftKey不能为空");
            }
        }
        // 判断翻译方式是否为谷歌翻译
        if (Consts.GOOGLE_TRANSLATOR.equals(config.getTranslator())) {
            // 判断googleKey是否为空
            if (StringUtils.isBlank(config.getGoogleKey())) {
                throw new ConfigurationException("googleKey不能为空");
            }
        }
    }
    // 重置
    @Override
    public void reset() {
        view.refresh();
    }
}
