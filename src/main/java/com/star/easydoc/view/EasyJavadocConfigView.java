package com.star.easydoc.view;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.*;

import com.google.common.collect.Lists;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.star.easydoc.config.EasyJavadocConfiguration;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class EasyJavadocConfigView {

    private EasyJavadocConfiguration config;
    private JPanel panel;
    private JPanel wordMapPanel;
    private JBList<Entry<String, String>> typeMapList;

    public EasyJavadocConfigView(EasyJavadocConfiguration config) {
        this.config = config;
        refreshWordMap();
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
                Entry<String, String> entry = wordMapAddView.getMapping();
                Objects.requireNonNull(config).getWordMap().put(entry.getKey(), entry.getValue());
                refreshWordMap();
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            Map<String, String> typeMap = Objects.requireNonNull(config).getWordMap();
            typeMap.remove(typeMapList.getSelectedValue().getKey());
            refreshWordMap();
        });
        wordMapPanel = toolbarDecorator.createPanel();
    }

    private void refreshWordMap() {
        if (null != config && config.getWordMap() != null) {
            typeMapList.setModel(new CollectionListModel<>(Lists.newArrayList(config.getWordMap().entrySet())));
        }
    }

    public JComponent getComponent() {
        return panel;
    }
}
