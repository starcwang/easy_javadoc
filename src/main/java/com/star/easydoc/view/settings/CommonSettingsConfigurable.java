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
import org.jetbrains.annotations.Nullable;

/**
 * 通用设置 可配置
 *
 * @author wangchao
 * @date 2019/08/25
 */
public class CommonSettingsConfigurable implements Configurable {

    /** 配置 */
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    /** 视图（懒加载：Settings 树 isBeta/asPromo 会实例化 Configurable，不应在此时构建 UI） */
    private CommonSettingsView view;

    private CommonSettingsView getView() {
        if (view == null) {
            view = new CommonSettingsView();
        }
        return view;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "EasyDoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return getView().getComponent();
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(config.getTranslator(), getView().getTranslatorBox().getSelectedItem())) {
            return true;
        }
        if (!Objects.equals(String.valueOf(config.getTimeout()), getView().getTimeoutTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getAppId(), getView().getAppIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getToken(), getView().getTokenTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSecretKey(), getView().getSecretKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSecretId(), getView().getSecretIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getAccessKeyId(), getView().getAccessKeyIdTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getAccessKeySecret(), getView().getAccessKeySecretTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getYoudaoAppKey(), getView().getYoudaoAppKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getYoudaoAppSecret(), getView().getYoudaoAppSecretTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getMicrosoftKey(), getView().getMicrosoftKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getMicrosoftRegion(), getView().getMicrosoftRegionTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getGoogleKey(), getView().getGoogleKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getChatGlmApiKey(), getView().getChatGlmApiKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getOpenAiApiKey(), getView().getOpenAiApiKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getOpenAiModel(), getView().getOpenAiModelTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getCustomUrl(), getView().getCustomUrlTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getCustomHttpMethod(),
            String.valueOf(getView().getCustomHttpMethodBox().getSelectedItem()))) {
            return true;
        }
        if (!Objects.equals(config.getOpenAiApiKey(), getView().getOpenAiApiKeyTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getOpenAiApiUrl(), getView().getOpenAiApiUrlTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getOpenAiModel(), getView().getOpenAiModelTextField().getText())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setTranslator(String.valueOf(getView().getTranslatorBox().getSelectedItem()));
        config.setAppId(getView().getAppIdTextField().getText());
        config.setToken(getView().getTokenTextField().getText());
        config.setSecretKey(getView().getSecretKeyTextField().getText());
        config.setSecretId(getView().getSecretIdTextField().getText());
        config.setAccessKeyId(getView().getAccessKeyIdTextField().getText());
        config.setAccessKeySecret(getView().getAccessKeySecretTextField().getText());
        config.setYoudaoAppKey(getView().getYoudaoAppKeyTextField().getText());
        config.setYoudaoAppSecret(getView().getYoudaoAppSecretTextField().getText());
        config.setMicrosoftKey(getView().getMicrosoftKeyTextField().getText());
        config.setMicrosoftRegion(getView().getMicrosoftRegionTextField().getText());
        config.setGoogleKey(getView().getGoogleKeyTextField().getText());
        config.setChatGlmApiKey(getView().getChatGlmApiKeyTextField().getText());
        config.setCustomUrl(StringUtils.strip(getView().getCustomUrlTextField().getText()));
        config.setCustomHttpMethod(String.valueOf(getView().getCustomHttpMethodBox().getSelectedItem()));
        config.setOpenAiApiKey(getView().getOpenAiApiKeyTextField().getText());
        config.setOpenAiApiUrl(getView().getOpenAiApiUrlTextField().getText());
        config.setOpenAiModel(getView().getOpenAiModelTextField().getText());
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
        if (Consts.CHATGLM_GPT.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getChatGlmApiKey())) {
                throw new ConfigurationException("apiKey不能为空");
            }
        }
        if (Consts.OPENAI_GPT.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getOpenAiApiKey())) {
                throw new ConfigurationException("API密钥不能为空");
            }
            if (StringUtils.isBlank(config.getOpenAiApiUrl())) {
                throw new ConfigurationException("API地址不能为空");
            }
            if (StringUtils.isBlank(config.getOpenAiModel())) {
                throw new ConfigurationException("API模型不能为空");
            }
        }
        if (Consts.CUSTOM_URL.equals(config.getTranslator())) {
            if (StringUtils.isBlank(config.getCustomUrl())) {
                throw new ConfigurationException("自定义地址不能为空");
            }
            if (!config.getCustomUrl().startsWith("http")) {
                throw new ConfigurationException("自定义地址只支持http或https接口");
            }
            if (!config.getCustomUrl().contains("{from}")) {
                throw new ConfigurationException("自定义地址需要包含{from}占位符，请查看说明文档");
            }
            if (!config.getCustomUrl().contains("{to}")) {
                throw new ConfigurationException("自定义地址需要包含{to}占位符，请查看说明文档");
            }
            if (!config.getCustomUrl().contains("{query}")) {
                throw new ConfigurationException("自定义地址需要包含{query}占位符，请查看说明文档");
            }
        }
        if (StringUtils.isBlank(getView().getTimeoutTextField().getText())
            || !getView().getTimeoutTextField().getText().matches("^[1-9][0-9]*$")) {
            throw new ConfigurationException("超时时间必须为数字");
        }
        config.setTimeout(Integer.parseInt(getView().getTimeoutTextField().getText()));
    }

    @Override
    public void reset() {
        getView().refresh();
    }
}