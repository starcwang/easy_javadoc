package com.star.easydoc.view.settings;

import java.awt.*;
import java.io.File;
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
import com.intellij.json.JsonFileType;
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
    private JBList<Entry<String, String>> typeMapList;
    private JBList<String> projectList;
    private JBList<Entry<String, String>> projectTypeMapList;

    /**
     * 晚于{@link #createUIComponents}执行
     */
    public CommonSettingsView() {
        refreshWordMap();
        refreshProjectWordMap();
        setVisible(translatorBox.getSelectedItem());

        importButton.addActionListener(event -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(JsonFileType.INSTANCE);
            descriptor.setForcedToUseIdeaFileChooser(true);
            VirtualFile file = FileChooser.chooseFile(descriptor, null, null);
            if (file == null) {
                return;
            }
            if (!file.exists()) {
                LOGGER.error("文件不存在:{}", file.getPath());
                return;
            }
            try {
                String json = FileUtils.readFileToString(new File(file.getPath()), StandardCharsets.UTF_8.name());
                EasyDocConfig configuration = JSON.parseObject(json, EasyDocConfig.class);
                if (configuration == null) {
                    throw new IllegalArgumentException("文件中内容格式不正确，请确认是否是json格式");
                }
                ServiceManager.getService(EasyDocConfigComponent.class).loadState(configuration);
                refresh();
            } catch (Exception e) {
                LOGGER.error("读取文件异常", e);
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
                LOGGER.error("文件夹不存在:{}", file.getPath());
                return;
            }
            try {
                File targetFile = new File(file.getPath() + "/easy_javadoc.json");
                FileUtils.write(targetFile, JSON.toJSONString(this.config, Feature.PrettyFormat), StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                LOGGER.error("写入文件异常", e);
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
                LOGGER.error("打开链接失败:https://github.com/starcwang/easy_javadoc", e);
            }
        });

        reviewsButton.addActionListener(event -> {
            try {
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create("https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews"));
                }
            } catch (Exception e) {
                LOGGER.error("打开链接失败:https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews", e);
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

        projectList.addListSelectionListener(e -> refreshProjectWordMap());
    }

    private void setVisible(Object selectedItem) {
        if (Consts.BAIDU_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(true);
            tokenLabel.setVisible(true);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(true);
            tokenTextField.setVisible(true);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(false);
        } else if (Consts.TENCENT_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(true);
            secretKeyLabel.setVisible(true);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(true);
            secretKeyTextField.setVisible(true);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(false);
        } else if (Consts.ALIYUN_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(true);
            accessKeySecretLabel.setVisible(true);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(true);
            accessKeySecretTextField.setVisible(true);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(false);
        } else if (Consts.YOUDAO_AI_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(true);
            youdaoAppSecretLabel.setVisible(true);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(true);
            youdaoAppSecretTextField.setVisible(true);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(false);
        } else if (Consts.MICROSOFT_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(true);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(true);
            googleKeyTextField.setVisible(false);
        } else if (Consts.GOOGLE_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(true);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(true);
        } else {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            youdaoAppKeyLabel.setVisible(false);
            youdaoAppSecretLabel.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            googleKeyLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            youdaoAppKeyTextField.setVisible(false);
            youdaoAppSecretTextField.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            googleKeyTextField.setVisible(false);
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
        setAppIdTextField(config.getAppId());
        setTokenTextField(config.getToken());
        setSecretIdTextField(config.getSecretId());
        setSecretKeyTextField(config.getSecretKey());
        setAccessKeyIdTextField(config.getAccessKeyId());
        setAccessKeySecretTextField(config.getAccessKeySecret());
        setYoudaoAppKeyTextField(config.getYoudaoAppKey());
        setYoudaoAppSecretTextField(config.getYoudaoAppSecret());
        setMicrosoftKeyTextField(config.getMicrosoftKey());
        setGoogleKeyTextField(config.getGoogleKey());
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

    public void setMicrosoftKeyTextField(String microsoftKey) {
        this.microsoftKeyTextField.setText(microsoftKey);
    }

    public JTextField getGoogleKeyTextField() {
        return googleKeyTextField;
    }

    public void setGoogleKeyTextField(String googleKey) {
        this.googleKeyTextField.setText(googleKey);
    }
}
