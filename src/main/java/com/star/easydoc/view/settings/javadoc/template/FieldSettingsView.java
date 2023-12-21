package com.star.easydoc.view.settings.javadoc.template;

import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.google.common.collect.Maps;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfig.CustomValue;
import com.star.easydoc.view.inner.CustomTemplateAddView;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public class FieldSettingsView extends AbstractTemplateSettingsView {

    // 定义私有变量
    private JPanel panel;
    private JTextArea templateTextArea;
    private JPanel innerVariablePanel;
    private JPanel customVariablePanel;
    private JPanel templatePanel;
    private JRadioButton defaultRadioButton;
    private JRadioButton customRadioButton;
    private JTable innerTable;
    private JScrollPane innerScrollPane;
    private JTable customTable;
    private static Map<String, String> innerMap;

    // 静态初始化块，为内置变量Map赋初值
    static {
        innerMap = Maps.newHashMap();
        innerMap.put("$DOC$", "注释信息");
        innerMap.put("$SEE$", "字段类型");
    }

    // 创建用户界面组件
    private void createUIComponents() {
        // 初始化内置变量表格数据
        Vector<Vector<String>> innerData = new Vector<>(innerMap.size());
        for (Entry<String, String> entry : innerMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Vector<String> row = new Vector<>(2);
            row.add(key);
            row.add(value);

            innerData.add(row);
        }
        // 创建内置变量表格
        DefaultTableModel innerModel = new DefaultTableModel(innerData, innerNames);
        innerTable = new JBTable(innerModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        innerScrollPane = new JBScrollPane(innerTable);

        // 设置表格显示的大小。
        innerTable.setPreferredScrollableViewportSize(new Dimension(-1, innerTable.getRowHeight() * innerTable.getRowCount()));
        innerTable.setFillsViewportHeight(true);

        // 创建自定义变量表格
        customTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshCustomTable();

        // 创建自定义变量表格工具栏
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(customTable);
        toolbarDecorator.setAddAction(button -> {
            // 添加自定义变量
            CustomTemplateAddView customTemplateAddView = new CustomTemplateAddView();
            if (customTemplateAddView.showAndGet()) {
                if (config != null) {
                    Entry<String, CustomValue> entry = customTemplateAddView.getEntry();
                    config.getFieldTemplateConfig().getCustomMap().put(entry.getKey(), entry.getValue());
                    refreshCustomTable();
                }
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            // 删除自定义变量
            if (config != null) {
                Map<String, CustomValue> customMap = config.getFieldTemplateConfig().getCustomMap();
                customMap.remove(customTable.getValueAt(customTable.getSelectedRow(), 0).toString());
                refreshCustomTable();
            }
        });
        customVariablePanel = toolbarDecorator.createPanel();
    }

    // 构造函数
    public FieldSettingsView(EasyDocConfig config) {
        super(config);
        // 添加单选按钮事件监听器
        defaultRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                customRadioButton.setSelected(false);
                templateTextArea.setEnabled(false);
                customTable.setEnabled(false);
                templatePanel.setEnabled(false);
                customVariablePanel.setEnabled(false);
                innerTable.setEnabled(false);
                innerScrollPane.setEnabled(false);
                innerVariablePanel.setEnabled(false);
            }
        });
        customRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                defaultRadioButton.setSelected(false);
                templateTextArea.setEnabled(true);
                customTable.setEnabled(true);
                templatePanel.setEnabled(true);
                customVariablePanel.setEnabled(true);
                innerTable.setEnabled(true);
                innerScrollPane.setEnabled(true);
                innerVariablePanel.setEnabled(true);
            }
        });
    }

    // 获取组件
    @Override
    public JComponent getComponent() {
        return panel;
    }

    // 刷新自定义变量表格
    private void refreshCustomTable() {
        // 初始化自定义变量表格数据
        Map<String, CustomValue> customMap = Maps.newHashMap();
        if (config != null && config.getFieldTemplateConfig() != null && config.getFieldTemplateConfig().getCustomMap() != null) {
            customMap = config.getFieldTemplateConfig().getCustomMap();
        }
        Vector<Vector<String>> customData = new Vector<>(customMap.size());
        for (Entry<String, CustomValue> entry : customMap.entrySet()) {
            String key = entry.getKey();
            CustomValue value = entry.getValue();
            Vector<String> row = new Vector<>(3);
            row.add(key);
            row.add(value.getType().getDesc());
            row.add(value.getValue());
            customData.add(row);
        }
        // 设置自定义变量表格模型
        DefaultTableModel customModel = new DefaultTableModel(customData, customNames);
        customTable.setModel(customModel);
        customTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customTable.getColumnModel().getColumn(0).setPreferredWidth((int)(customTable.getWidth() * 0.3));
    }

    // 判断是否为默认选项
    public boolean isDefault() {
        return defaultRadioButton.isSelected();
    }

    // 设置是否为默认选项
    public void setDefault(boolean isDefault) {
        if (isDefault) {
            defaultRadioButton.setSelected(true);
            customRadioButton.setSelected(false);
        } else {
            defaultRadioButton.setSelected(false);
            customRadioButton.setSelected(true);
        }
    }

    // 获取模板内容
    public String getTemplate() {
        return templateTextArea.getText();
    }

    // 设置模板内容
    public void setTemplate(String template) {
        templateTextArea.setText(template);
    }
}
