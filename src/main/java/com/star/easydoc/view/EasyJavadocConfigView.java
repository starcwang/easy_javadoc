package com.star.easydoc.view;

import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import com.google.common.collect.Lists;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.star.easydoc.model.EasyJavadocConfiguration;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class EasyJavadocConfigView {

    private EasyJavadocConfiguration config;
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
    private JComboBox translatorBox;
    private JLabel translatorLabel;
    private JBList<Entry<String, String>> typeMapList;

    public EasyJavadocConfigView(EasyJavadocConfiguration config) {
        this.config = config;
        refreshWordMap();

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
    }

    private void createUIComponents() {
        typeMapList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        typeMapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        typeMapList.setCellRenderer(new SimpleListCellRenderer<Entry<String, String>>() {
            @Override
            public void customize(JList<? extends Entry<String, String>> list, Entry<String, String> value,
                int index, boolean selected, boolean hasFocus) {
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
}
