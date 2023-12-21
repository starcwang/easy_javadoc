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
public class MethodSettingsView extends AbstractTemplateSettingsView {
    // 定义私有属性
    private JPanel panel; // 包含所有组件的面板
    private JTextArea templateTextArea; // 文本域，用于显示和编辑模板
    private JPanel innerVariablePanel; // 用于显示和管理内置变量的面板
    private JPanel customVariablePanel; // 用于显示和管理自定义变量的面板
    private JPanel templatePanel; // 用于显示模板的面板
    private JRadioButton defaultRadioButton; // 默认模板的单选按钮
    private JRadioButton customRadioButton; // 自定义模板的单选按钮
    private JTable innerTable; // 显示内置变量的表格
    private JScrollPane innerScrollPane; // 内置变量表格的滚动面板
    private JTable customTable; // 显示自定义变量的表格
    private static Map<String, String> innerMap; // 存储内置变量的映射

    static {
        // 初始化内置变量映射
        innerMap = Maps.newHashMap();
        innerMap.put("$DOC$", "注释信息");
        innerMap.put("$PARAMS$", "遍历传入参数并添加注释");
        innerMap.put("$RETURN$", "返回值类型");
        innerMap.put("$THROWS$", "异常类型并注释");
        innerMap.put("$SEE$", "引用传入参数类型和返回值类型");
    }

    private void createUIComponents() {
        // 初始化内置变量表格
        Vector<Vector<String>> innerData = new Vector<>(innerMap.size());
        for (Entry<String, String> entry : innerMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Vector<String> row = new Vector<>(2);
            row.add(key);
            row.add(value);
            innerData.add(row);
        }
        DefaultTableModel innerModel = new DefaultTableModel(innerData, innerNames);
        innerTable = new JBTable(innerModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        innerScrollPane = new JBScrollPane(innerTable);

        // 设置内置变量表格的显示大小和填充视窗高度

        innerTable.setPreferredScrollableViewportSize(new Dimension(-1, innerTable.getRowHeight() * innerTable.getRowCount()));
        innerTable.setFillsViewportHeight(true);

        // 初始化自定义变量表格，并设置其不可编辑
        customTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshCustomTable(); // 更新自定义变量表格
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(customTable);
        toolbarDecorator.setAddAction(button -> {
            // 添加自定义变量事件
            CustomTemplateAddView customTemplateAddView = new CustomTemplateAddView();
            if (customTemplateAddView.showAndGet()) {
                if (config != null) {
                    Entry<String, CustomValue> entry = customTemplateAddView.getEntry();
                    config.getMethodTemplateConfig().getCustomMap().put(entry.getKey(), entry.getValue());
                    refreshCustomTable(); // 更新自定义变量表格
                }
            }
        });
        toolbarDecorator.setRemoveAction(anActionButton -> {
            // 删除自定义变量事件
            if (config != null) {
                Map<String, CustomValue> customMap = config.getMethodTemplateConfig().getCustomMap();
                customMap.remove(customTable.getValueAt(customTable.getSelectedRow(), 0).toString());
                refreshCustomTable(); // 更新自定义变量表格
            }
        });
        customVariablePanel = toolbarDecorator.createPanel();
    }

    public MethodSettingsView(EasyDocConfig config) {
        super(config);
        // 添加单选按钮事件
        defaultRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                customRadioButton.setSelected(false); // 取消自定义模板单选按钮的选中状态
                templateTextArea.setEnabled(false); // 禁用模板文本域
                customTable.setEnabled(false); // 禁用自定义变量表格
                templatePanel.setEnabled(false); // 禁用模板面板
                customVariablePanel.setEnabled(false); // 禁用自定义变量面板
                innerTable.setEnabled(false); // 禁用内置变量表格
                innerScrollPane.setEnabled(false); // 禁用内置变量表格的滚动面板
                innerVariablePanel.setEnabled(false); // 禁用内置变量面板
            }
        });
        customRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                defaultRadioButton.setSelected(false); // 取消默认模板单选按钮的选中状态
                templateTextArea.setEnabled(true); // 启用模板文本域
                customTable.setEnabled(true); // 启用自定义变量表格
                templatePanel.setEnabled(true); // 启用模板面板
                customVariablePanel.setEnabled(true); // 启用自定义变量面板
                innerTable.setEnabled(true); // 启用内置变量表格

                innerScrollPane.setEnabled(true); // 启用内置变量表格的滚动面板
                innerVariablePanel.setEnabled(true); // 启用内置变量面板
            }
        });
    }

    @Override
    public JComponent getComponent() {
        // 返回包含在该类中的 panel 组件
        return panel;
    }

    private void refreshCustomTable() {
        // 初始化自定义变量表格
        Map<String, CustomValue> customMap = Maps.newHashMap();
        if (config != null && config.getMethodTemplateConfig() != null && config.getMethodTemplateConfig().getCustomMap() != null) {
            // 如果配置信息不为空且自定义映射存在，则将其赋值给 customMap
            customMap = config.getMethodTemplateConfig().getCustomMap();
        }
        Vector<Vector<String>> customData = new Vector<>(customMap.size());
        for (Entry<String, CustomValue> entry : customMap.entrySet()) {
            // 根据自定义映射的键值对构建表格数据
            String key = entry.getKey();
            CustomValue value = entry.getValue();
            Vector<String> row = new Vector<>(2);
            row.add(key);
            row.add(value.getType().getDesc());
            row.add(value.getValue());
            customData.add(row);
        }
        DefaultTableModel customModel = new DefaultTableModel(customData, customNames);
        // 设置自定义变量表格的模型、选择模式和列的首选宽度
        customTable.setModel(customModel);
        customTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customTable.getColumnModel().getColumn(0).setPreferredWidth((int)(customTable.getWidth() * 0.3));
    }

    public boolean isDefault() {
        // 判断是否选择了默认选项
        return defaultRadioButton.isSelected();
    }

    public void setDefault(boolean isDefault) {
        if (isDefault) {
            // 如果 isDefault 为 true，则设置默认选项为选中状态，自定义选项为非选中状态
            defaultRadioButton.setSelected(true);
            customRadioButton.setSelected(false);
        } else {
            // 如果 isDefault 为 false，则设置默认选项为非选中状态，自定义选项为选中状态
            defaultRadioButton.setSelected(false);
            customRadioButton.setSelected(true);
        }
    }

    public String getTemplate() {
        // 返回模板文本区域的内容
        return templateTextArea.getText();
    }

    public void setTemplate(String template) {
        // 将传入的参数设置为模板文本区域的内容
        templateTextArea.setText(template);
    }
}


        templateTextArea.setText(template);
    }
}
