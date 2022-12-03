package com.star.easydoc.javadoc.view.settings.javadoc;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter.Feature;

import com.google.common.collect.Lists;
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
import com.star.easydoc.javadoc.view.inner.SupportView;
import com.star.easydoc.javadoc.view.inner.WordMapAddView;
import com.star.easydoc.javadoc.view.settings.CommonSettingsView;
import com.star.easydoc.service.translator.TranslatorService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author wangchao
 * @date 2022/12/04
 */
public class JavadocSettingsView {

    private static final Logger LOGGER = Logger.getInstance(CommonSettingsView.class);
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

    private JPanel panel;
    private JPanel wordMapPanel;
    private JTextField authorTextField;
    private JTextField dateFormatTextField;
    private JPanel classPanel;
    private JPanel fieldPanel;
    private JLabel authorLabel;
    private JLabel dataFormatLabel;
    private JRadioButton simpleDocButton;
    private JRadioButton normalDocButton;
    private JLabel fieldDocLabel;
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
    private JPanel methodPanel;
    private JLabel methodReturnTypeLabel;
    private JRadioButton methodReturnCodeTypeButton;
    private JRadioButton methodReturnLinkTypeButton;
    private JTextField accessKeyIdTextField;
    private JTextField accessKeySecretTextField;
    private JLabel accessKeyIdLabel;
    private JLabel accessKeySecretLabel;
    private JBList<Entry<String, String>> typeMapList;

    public JavadocSettingsView() {
        refreshWordMap();
        setVisible(translatorBox.getSelectedItem());

        simpleDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                normalDocButton.setSelected(false);
            } else {
                normalDocButton.setSelected(true);
            }
        });

        normalDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                simpleDocButton.setSelected(false);
            } else {
                simpleDocButton.setSelected(true);
            }
        });

        methodReturnCodeTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                methodReturnLinkTypeButton.setSelected(false);
            } else {
                methodReturnLinkTypeButton.setSelected(true);
            }
        });

        methodReturnLinkTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                methodReturnCodeTypeButton.setSelected(false);
            } else {
                methodReturnCodeTypeButton.setSelected(true);
            }
        });

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

        payButton.addActionListener(event -> {
            SupportView supportView = new SupportView();
            supportView.show();
        });

        translatorBox.addItemListener(e -> {
            JComboBox<?> jComboBox = (JComboBox<?>)e.getSource();
            setVisible(jComboBox.getSelectedItem());
        });
    }

    private void setVisible(Object selectedItem) {
        if (Consts.BAIDU_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(true);
            tokenLabel.setVisible(true);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);

            appIdTextField.setVisible(true);
            tokenTextField.setVisible(true);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
        } else if (Consts.TENCENT_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(true);
            secretKeyLabel.setVisible(true);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(true);
            secretKeyTextField.setVisible(true);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
        } else if (Consts.ALIYUN_TRANSLATOR.equals(selectedItem)) {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(true);
            accessKeySecretLabel.setVisible(true);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(true);
            accessKeySecretTextField.setVisible(true);
        } else {
            appIdLabel.setVisible(false);
            tokenLabel.setVisible(false);
            secretIdLabel.setVisible(false);
            secretKeyLabel.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeySecretLabel.setVisible(false);

            appIdTextField.setVisible(false);
            tokenTextField.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyTextField.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretTextField.setVisible(false);
        }
    }

    private void createUIComponents() {
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
                if (config != null) {
                    Entry<String, String> entry = wordMapAddView.getMapping();
                    config.getWordMap().put(entry.getKey(), entry.getValue());
                    refreshWordMap();
                }
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            if (config != null) {
                Map<String, String> typeMap = config.getWordMap();
                typeMap.remove(typeMapList.getSelectedValue().getKey());
                refreshWordMap();
            }
        });
        wordMapPanel = toolbarDecorator.createPanel();
    }

    public void refresh() {
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            setSimpleDocButton(true);
            setNormalDocButton(false);
        } else {
            setSimpleDocButton(false);
            setNormalDocButton(true);
        }
        if (EasyDocConfig.CODE_RETURN_TYPE.equals(config.getMethodReturnType())) {
            setMethodReturnCodeTypeButton(true);
            setMethodReturnLinkTypeButton(false);
        } else if (EasyDocConfig.LINK_RETURN_TYPE.equals(config.getMethodReturnType())) {
            setMethodReturnCodeTypeButton(false);
            setMethodReturnLinkTypeButton(true);
        }
        setAuthorTextField(config.getAuthor());
        setDateFormatTextField(config.getDateFormat());
        setTranslatorBox(config.getTranslator());
        setAppIdTextField(config.getAppId());
        setTokenTextField(config.getToken());
        setSecretIdTextField(config.getSecretId());
        setSecretKeyTextField(config.getSecretKey());
        setAccessKeyIdTextField(config.getAccessKeyId());
        setAccessKeySecretTextField(config.getAccessKeySecret());
        refreshWordMap();
    }

    private void refreshWordMap() {
        if (null != config && config.getWordMap() != null) {
            typeMapList.setModel(new CollectionListModel<>(Lists.newArrayList(config.getWordMap().entrySet())));
        }
    }

    public JComboBox getTranslatorBox() {
        return translatorBox;
    }

    public JComponent getComponent() {
        return panel;
    }

    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    public JTextField getDateFormatTextField() {
        return dateFormatTextField;
    }

    public JRadioButton getSimpleDocButton() {
        return simpleDocButton;
    }

    public JRadioButton getNormalDocButton() {
        return normalDocButton;
    }

    public void setSimpleDocButton(boolean b) {
        simpleDocButton.setSelected(b);
    }

    public void setNormalDocButton(boolean b) {
        normalDocButton.setSelected(b);
    }

    public void setAuthorTextField(String author) {
        authorTextField.setText(author);
    }

    public void setTranslatorBox(String translator) {
        translatorBox.setSelectedItem(translator);
    }

    public void setDateFormatTextField(String dateFormat) {
        dateFormatTextField.setText(dateFormat);
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

    public void setMethodReturnCodeTypeButton(boolean selecetd) {
        methodReturnCodeTypeButton.setSelected(selecetd);
    }

    public void setMethodReturnLinkTypeButton(boolean selecetd) {
        methodReturnLinkTypeButton.setSelected(selecetd);
    }

    public String getMethodReturnType() {
        return methodReturnCodeTypeButton.isSelected() ?
            EasyDocConfig.CODE_RETURN_TYPE : EasyDocConfig.LINK_RETURN_TYPE;
    }

}
