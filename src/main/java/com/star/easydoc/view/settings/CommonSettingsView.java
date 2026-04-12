package com.star.easydoc.view.settings;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.*;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.star.easydoc.common.Consts;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.service.translator.TranslatorService;
import com.star.easydoc.view.inner.SupportView;
import com.star.easydoc.view.inner.WordMapAddView;
import org.apache.commons.io.FileUtils;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class CommonSettingsView {
    private static final Logger LOGGER = Logger.getInstance(CommonSettingsView.class);
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

    private JPanel panel;
    private JPanel wordMapPanel;
    private JPanel commonPanel;
    private JComboBox<?> translatorBox;
    private JLabel translatorLabel;
    private JButton importButton;
    private JButton exportButton;
    private JTextField appIdTextField;
    private JTextField tokenTextField;
    private JButton resetButton;
    private JButton clearButton;
    private JLabel appIdLabel;
    private JLabel tokenLabel;
    private JTextField secretIdTextField;
    private JTextField secretKeyTextField;
    private JLabel secretIdLabel;
    private JLabel secretKeyLabel;
    private JButton starButton;
    private JButton payButton;
    private JTextField accessKeyIdTextField;
    private JTextField accessKeySecretTextField;
    private JLabel accessKeyIdLabel;
    private JLabel accessKeySecretLabel;
    private JPanel projectPanel;
    private JPanel projectListPanel;
    private JPanel projectWordMapPanel;
    private JButton reviewsButton;
    private JPanel supportPanel;
    private JTextField youdaoAppKeyTextField;
    private JTextField youdaoAppSecretTextField;
    private JLabel youdaoAppKeyLabel;
    private JLabel youdaoAppSecretLabel;
    private JTextField microsoftKeyTextField;
    private JTextField googleKeyTextField;
    private JLabel microsoftKeyLabel;
    private JLabel googleKeyLabel;
    private JTextField chatGlmApiKeyTextField;
    private JLabel chatGlmApiKeyLabel;
    private JTextField openAiApiKeyTextField;
    private JLabel openAiApiKeyLabel;
    private JTextField openAiApiUrlTextField;
    private JLabel openAiApiUrlLabel;
    private JTextField openAiModelTextField;
    private JLabel openAiModelLabel;
    private JTextField openAiApiKeyTextField;
    private JLabel openAiApiKeyLabel;
    private JTextField openAiModelTextField;
    private JLabel openAiModelLabel;
    private JTextField openAiBaseUrlTextField;
    private JLabel openAiBaseUrlLabel;

    private JTextField deepLxBaseUrlTextField;
    private JLabel deepLxBaseUrlLabel;
    private JTextField deepLxTokenTextField;
    private JLabel deepLxTokenLabel;

    private JTextField microsoftRegionTextField;
    private JLabel microsoftRegionLabel;
    private JTextField timeoutTextField;
    private JLabel timeoutLabel;
    private JTextField customUrlTextField;
    private JLabel customUrlLabel;
    private JButton customUrlHelpButton;
    private JComboBox<String> customHttpMethodBox;
    private JLabel customHttpMethodLabel;
    private JBList<Entry<String, String>> typeMapList;
    private JBList<String> projectList;
    private JBList<Entry<String, String>> projectTypeMapList;

    private static final String CUSTOM_HELP_URL
        = "https://github.com/starcwang/easy_javadoc/blob/master/doc/%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E.md";

    /**
     * 晚于{@link #createUIComponents}执行
     */
    public CommonSettingsView() {
        refreshWordMap();
        refreshProjectWordMap();
        setVisible(translatorBox.getSelectedItem());

        importButton.addActionListener(event -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
            descriptor.setForcedToUseIdeaFileChooser(true);
            VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
            if (file == null) {
                return;
            }
            if (!file.exists()) {
                LOGGER.error("file not exists:{}", file.getPath());
                return;
            }
            try {
                String json = FileUtils.readFileToString(new File(file.getPath()), StandardCharsets.UTF_8.name());
                EasyDocConfig configuration = JSON.parseObject(json, EasyDocConfig.class);
                if (configuration == null) {
                    throw new IllegalArgumentException("json error, please make sure the file format is json.");
                }
                ServiceManager.getService(EasyDocConfigComponent.class).loadState(configuration);
                refresh();
            } catch (Exception e) {
                LOGGER.error("read file error", e);
            }
        });

        exportButton.addActionListener(event -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            descriptor.setForcedToUseIdeaFileChooser(true);
            VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
            if (file == null) {
                return;
            }
            if (!file.exists()) {
                LOGGER.error("folder not exists:{}", file.getPath());
                return;
            }
            try {
                File targetFile = new File(file.getPath() + "/easy_javadoc.json");
                FileUtils.write(targetFile, JSON.toJSONString(this.config, Feature.PrettyFormat), StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                LOGGER.error("write file error", e);
            }
        });

        resetButton.addActionListener(event -> {
            int result = JOptionPane.showConfirmDialog(null, "重置将删除所有配置，确认重置?", "确认", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                config.reset();
                refresh();
            }
        });

        clearButton.addActionListener(event -> {
            int result = JOptionPane.showConfirmDialog(null, "确认清空缓存?", "确认", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                translatorService.clearCache();
            }
        });

        starButton.addActionListener(event -> {
            try {
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create("https://github.com/starcwang/easy_javadoc"));
                }
            } catch (Exception e) {
                LOGGER.error("open url failed:https://github.com/starcwang/easy_javadoc", e);
            }
        });

        reviewsButton.addActionListener(event -> {
            try {
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create("https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews"));
                }
            } catch (Exception e) {
                LOGGER.error("open url failed:https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews", e);
            }
        });

        payButton.addActionListener(event -> {
            SupportView supportView = new SupportView();
            supportView.show();
        });

        translatorBox.addItemListener(e -> {
            JComboBox<?> jComboBox = (JComboBox<?>)e.getSource();
            setVisible(jComboBox.getSelectedItem());
        });

        customUrlHelpButton.addActionListener(event -> {
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE)) {
                try {
                    dp.browse(URI.create(CUSTOM_HELP_URL));
                } catch (IOException e) {
                    LOGGER.error("open url failed: " + CUSTOM_HELP_URL, e);
                }
            }
        });

        projectList.addListSelectionListener(e -> refreshProjectWordMap());
    }

    private void setVisible(Object selectedItem) {
        appIdLabel.setVisible(false);
        tokenLabel.setVisible(false);
        secretIdLabel.setVisible(false);
        secretKeyLabel.setVisible(false);
        accessKeyIdLabel.setVisible(false);
        accessKeySecretLabel.setVisible(false);
        youdaoAppKeyLabel.setVisible(false);
        youdaoAppSecretLabel.setVisible(false);
        microsoftKeyLabel.setVisible(false);
        microsoftRegionLabel.setVisible(false);
        googleKeyLabel.setVisible(false);
        chatGlmApiKeyLabel.setVisible(false);
        openAiApiUrlLabel.setVisible(false);
        openAiApiKeyLabel.setVisible(false);
        openAiModelLabel.setVisible(false);
        customUrlLabel.setVisible(false);
        appIdTextField.setVisible(false);
        tokenTextField.setVisible(false);
        secretIdTextField.setVisible(false);
        secretKeyTextField.setVisible(false);
        accessKeyIdTextField.setVisible(false);
        accessKeySecretTextField.setVisible(false);
        youdaoAppKeyTextField.setVisible(false);
        youdaoAppSecretTextField.setVisible(false);
        microsoftKeyTextField.setVisible(false);
        microsoftRegionTextField.setVisible(false);
        googleKeyTextField.setVisible(false);
        chatGlmApiKeyTextField.setVisible(false);
        openAiApiUrlTextField.setVisible(false);
        openAiApiKeyTextField.setVisible(false);
        openAiModelTextField.setVisible(false);
        customUrlTextField.setVisible(false);
        customUrlHelpButton.setVisible(false);
        customHttpMethodBox.setVisible(false);
        customHttpMethodLabel.setVisible(false);

        if (Consts.BAIDU_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(true);
            tokenLabel.setVisible(true);
            appIdTextField.setVisible(true);
            tokenTextField.setVisible(true);
        } else if (Consts.TENCENT_TRANSLATOR.equals(selectedItem)) {
            secretIdLabel.setVisible(true);
            secretKeyLabel.setVisible(true);
            secretIdTextField.setVisible(true);
            secretKeyTextField.setVisible(true);
        } else if (Consts.ALIYUN_TRANSLATOR.equals(selectedItem)) {
            accessKeyIdLabel.setVisible(true);
            accessKeySecretLabel.setVisible(true);
            accessKeyIdTextField.setVisible(true);
            accessKeySecretTextField.setVisible(true);
        } else if (Consts.YOUDAO_AI_TRANSLATOR.equals(selectedItem)) {
            youdaoAppKeyLabel.setVisible(true);
            youdaoAppSecretLabel.setVisible(true);
            youdaoAppKeyTextField.setVisible(true);
            youdaoAppSecretTextField.setVisible(true);
        } else if (Consts.MICROSOFT_TRANSLATOR.equals(selectedItem)) {
            microsoftKeyLabel.setVisible(true);
            microsoftRegionLabel.setVisible(true);
            microsoftKeyTextField.setVisible(true);
            microsoftRegionTextField.setVisible(true);
        } else if (Consts.GOOGLE_TRANSLATOR.equals(selectedItem)) {
            googleKeyLabel.setVisible(true);
            googleKeyTextField.setVisible(true);
        } else if (Consts.CHATGLM_GPT.equals(selectedItem)) {
            chatGlmApiKeyLabel.setVisible(true);
            chatGlmApiKeyTextField.setVisible(true);
        } else if (Consts.OPENAI_GPT.equals(selectedItem)) {
            openAiApiUrlLabel.setVisible(true);
            openAiApiUrlTextField.setVisible(true);
            openAiApiKeyLabel.setVisible(true);
            openAiApiKeyTextField.setVisible(true);
            openAiModelLabel.setVisible(true);
            openAiModelTextField.setVisible(true);
        } else if (Consts.CUSTOM_URL.equals(selectedItem)) {
            customUrlLabel.setVisible(true);
            customUrlHelpButton.setVisible(true);
            customHttpMethodLabel.setVisible(true);
            customHttpMethodBox.setVisible(true);
        }
    }

    /**
     * 早于构造方法{@link #CommonSettingsView}执行
     */
    private void createUIComponents() {
        config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
        assert config != null;
        config.mergeProject();

        typeMapList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        typeMapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typeMapList.setCellRenderer(new ListCellRendererWrapper<Entry<String, String>>() {
            @Override
            public void customize(JList list, Entry<String, String> value, int index, boolean selected, boolean hasFocus) {
                setText(value.getKey() + " -> " + value.getValue());
            }
        });

        typeMapList.setEmptyText("请添加单词映射");
        typeMapList.setSelectedIndex(0);
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(typeMapList);
        toolbarDecorator.setAddAction(button -> {
            WordMapAddView wordMapAddView = new WordMapAddView();
            if (wordMapAddView.showAndGet()) {
                Entry<String, String> entry = wordMapAddView.getMapping();
                config.getWordMap().put(entry.getKey(), entry.getValue());
                refreshWordMap();
            }
        });
        toolbarDecorator.disableUpDownActions();
        toolbarDecorator.setRemoveAction(anActionButton -> {
            Map<String, String> typeMap = config.getWordMap();
            typeMap.remove(typeMapList.getSelectedValue().getKey());
            refreshWordMap();
        });
        wordMapPanel = toolbarDecorator.createPanel();

        projectTypeMapList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        projectTypeMapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectTypeMapList.setCellRenderer(new ListCellRendererWrapper<Entry<String, String>>() {
            @Override
            public void customize(JList list, Entry<String, String> value, int index, boolean selected, boolean hasFocus) {
                setText(value.getKey() + " -> " + value.getValue());
            }
        });

        projectTypeMapList.setEmptyText("请添加单词映射");
        projectTypeMapList.setSelectedIndex(0);
        ToolbarDecorator projectWordToolbarDecorator = ToolbarDecorator.createDecorator(projectTypeMapList);
        projectWordToolbarDecorator.setAddAction(button -> {
            String projectName = projectList.getSelectedValue();
            if (projectName == null || projectName.isEmpty()) {
                return;
            }
            WordMapAddView wordMapAddView = new WordMapAddView();
            if (wordMapAddView.showAndGet()) {
                Entry<String, String> entry = wordMapAddView.getMapping();
                config.getProjectWordMap().computeIfAbsent(projectName, f -> Maps.newTreeMap())
                    .put(entry.getKey(), entry.getValue());
                refreshProjectWordMap();
            }
        });
        projectWordToolbarDecorator.disableUpDownActions();
        projectWordToolbarDecorator.setRemoveAction(anActionButton -> {
            Map<String, String> typeMap = config.getProjectWordMap().get(projectList.getSelectedValue());
            typeMap.remove(projectTypeMapList.getSelectedValue().getKey());
            refreshProjectWordMap();
        });
        projectWordMapPanel = projectWordToolbarDecorator.createPanel();

        projectList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        projectList.setModel(new CollectionListModel<>(config.getProjectWordMap().keySet()));
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.setCellRenderer(new ListCellRendererWrapper<String>() {
            @Override
            public void customize(JList list, String value, int index, boolean selected, boolean hasFocus) {
                setText(value);
            }
        });
        projectList.setSelectedIndex(0);

        ToolbarDecorator projectToolbarDecorator = ToolbarDecorator.createDecorator(projectList);
        projectToolbarDecorator.disableRemoveAction();
        projectToolbarDecorator.disableAddAction();
        projectToolbarDecorator.disableUpDownActions();
        projectListPanel = projectToolbarDecorator.createPanel();
    }

    public void refresh() {
        setTranslatorBox(config.getTranslator());
        setTimeout(config.getTimeout());
        setAppIdTextField(config.getAppId());
        setTokenTextField(config.getToken());
        setSecretIdTextField(config.getSecretId());
        setSecretKeyTextField(config.getSecretKey());
        setAccessKeyIdTextField(config.getAccessKeyId());
        setAccessKeySecretTextField(config.getAccessKeySecret());
        setYoudaoAppKeyTextField(config.getYoudaoAppKey());
        setYoudaoAppSecretTextField(config.getYoudaoAppSecret());
        setMicrosoftKeyTextField(config.getMicrosoftKey());
        setMicrosoftRegionTextField(config.getMicrosoftRegion());
        setGoogleKeyTextField(config.getGoogleKey());
        setChatGlmApiKeyTextField(config.getChatGlmApiKey());
        setOpenAiApiKeyTextField(config.getOpenAiApiKey());
        setOpenAiApiUrlTextField(config.getOpenAiApiUrl());
        setOpenAiModelTextField(config.getOpenAiModel());
        setCustomUrlTextField(config.getCustomUrl());
        setCustomHttpMethod(config.getCustomHttpMethod());
        refreshWordMap();
        projectList.clearSelection();
        refreshProjectWordMap();
    }

    private void refreshWordMap() {
        if (null != config && config.getWordMap() != null) {
            typeMapList.setModel(new CollectionListModel<>(Lists.newArrayList(config.getWordMap().entrySet())));
        }
    }

    private void refreshProjectWordMap() {
        String projectName = projectList.getSelectedValue();
        SortedMap<String, TreeMap<String, String>> projectWordMap = config.getProjectWordMap();
        if (projectWordMap == null) {
            projectWordMap = Maps.newTreeMap();
        }

        // 没选择，默认页面
        if (projectName == null || projectName.isEmpty()) {
            projectList.setModel(new CollectionListModel<>(projectWordMap.keySet()));
            projectTypeMapList.setModel(new CollectionListModel<>(Lists.newArrayList()));
            return;
        }

        // 有选择，但配置中没有 -> 尝试初始化一次
        SortedMap<String, String> wordMap = projectWordMap.get(projectName);
        if (wordMap == null || wordMap.isEmpty()) {
            config.mergeProject();
        }
        wordMap = projectWordMap.get(projectName);
        // 还是没有
        if (wordMap == null) {
            wordMap = Maps.newTreeMap();
        }
        projectTypeMapList.setModel(new CollectionListModel<>(Lists.newArrayList(wordMap.entrySet())));
    }

    public JComboBox getTranslatorBox() {
        return translatorBox;
    }

    public JComponent getComponent() {
        return panel;
    }

    public void setTranslatorBox(String translator) {
        translatorBox.setSelectedItem(translator);
    }

    public JTextField getAppIdTextField() {
        return appIdTextField;
    }

    public void setAppIdTextField(String appId) {
        this.appIdTextField.setText(appId);
    }

    public JTextField getTokenTextField() {
        return tokenTextField;
    }

    public void setTokenTextField(String token) {
        this.tokenTextField.setText(token);
    }

    public JTextField getSecretIdTextField() {
        return secretIdTextField;
    }

    public void setSecretIdTextField(String secretId) {
        this.secretIdTextField.setText(secretId);
    }

    public JTextField getSecretKeyTextField() {
        return secretKeyTextField;
    }

    public void setSecretKeyTextField(String secretKey) {
        this.secretKeyTextField.setText(secretKey);
    }

    public JTextField getAccessKeyIdTextField() {
        return accessKeyIdTextField;
    }

    public void setAccessKeyIdTextField(String accessKeyId) {
        this.accessKeyIdTextField.setText(accessKeyId);
    }

    public JTextField getAccessKeySecretTextField() {
        return accessKeySecretTextField;
    }

    public void setAccessKeySecretTextField(String accessKeySecret) {
        this.accessKeySecretTextField.setText(accessKeySecret);
    }

    public JTextField getYoudaoAppKeyTextField() {
        return youdaoAppKeyTextField;
    }

    public JTextField getYoudaoAppSecretTextField() {
        return youdaoAppSecretTextField;
    }

    public void setYoudaoAppKeyTextField(String youdaoAppKey) {
        this.youdaoAppKeyTextField.setText(youdaoAppKey);
    }

    public void setYoudaoAppSecretTextField(String youdaoAppSecret) {
        this.youdaoAppSecretTextField.setText(youdaoAppSecret);
    }

    public JTextField getMicrosoftKeyTextField() {
        return microsoftKeyTextField;
    }

    public JTextField getMicrosoftRegionTextField() {
        return microsoftRegionTextField;
    }

    public void setMicrosoftKeyTextField(String microsoftKey) {
        this.microsoftKeyTextField.setText(microsoftKey);
    }

    public void setMicrosoftRegionTextField(String microsoftRegion) {
        this.microsoftRegionTextField.setText(microsoftRegion);
    }

    public JTextField getGoogleKeyTextField() {
        return googleKeyTextField;
    }

    public void setGoogleKeyTextField(String googleKey) {
        this.googleKeyTextField.setText(googleKey);
    }

    public JTextField getChatGlmApiKeyTextField() {
        return chatGlmApiKeyTextField;
    }

    public void setChatGlmApiKeyTextField(String apiKey) {
        this.chatGlmApiKeyTextField.setText(apiKey);
    }

    public JTextField getOpenAiApiKeyTextField() {
        return openAiApiKeyTextField;
    }

    public void setOpenAiApiKeyTextField(String apiKey) {
        this.openAiApiKeyTextField.setText(apiKey);
    }

    public JTextField getOpenAiApiUrlTextField() {
        return openAiApiUrlTextField;
    }

    public void setOpenAiApiUrlTextField(String apiUrl) {
        this.openAiApiUrlTextField.setText(apiUrl);
    }

    public JTextField getOpenAiModelTextField() {
        return openAiModelTextField;
    }

    public void setOpenAiModelTextField(String model) {
        this.openAiModelTextField.setText(model);
    }

    public JTextField getTimeoutTextField() {
        return timeoutTextField;
    }

    public void setTimeout(int timeout) {
        this.timeoutTextField.setText(String.valueOf(timeout));
    }

    public JTextField getCustomUrlTextField() {
        return customUrlTextField;
    }

    public void setCustomUrlTextField(String customUrl) {
        this.customUrlTextField.setText(customUrl);
    }

    public JComboBox<String> getCustomHttpMethodBox() {
        return customHttpMethodBox;
    }

    public void setCustomHttpMethod(String httpMethod) {
        if (httpMethod == null) {
            httpMethod = "GET";
        }
        this.customHttpMethodBox.setSelectedItem(httpMethod);
    }
}

