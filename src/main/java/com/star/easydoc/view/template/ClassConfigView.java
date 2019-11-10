package com.star.easydoc.view.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.codeInspection.ui.ListTable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.view.inner.WordMapAddView;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public class ClassConfigView extends AbstractTemplateConfigView {

    private JPanel panel;
    private JTextArea templateTextArea;
    private JPanel innerVariablePanel;
    private JPanel customVariablePanel;
    private JPanel template;
    private JRadioButton defaultRadioButton;
    private JRadioButton customRadioButton;
    private JTable innerTable;
    private JScrollPane innerScrollPane;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JBList<Entry<JLabel, JTextField>> customList;
    private static Map<String, String> innerList;

    static {
        innerList = Maps.newHashMap();
        innerList.put("1", "a");
        innerList.put("2", "b");
        innerList.put("3", "c");
    }

    private void createUIComponents() {
        Vector names = new Vector();
        names.add("变量");
        names.add("含义");

        Vector data = new Vector();
        for (Entry<String, String> entry : innerList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Vector row = new Vector();
            row.add(key);
            row.add(value);
            data.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(data, names);
        model.setColumnIdentifiers(names);
        model.setDataVector(data, names);
        innerTable = new JBTable(model);
        innerTable.setSize(60, innerTable.getRowHeight() * innerTable.getRowCount());
        innerScrollPane = new JBScrollPane(innerTable);
        innerScrollPane.setSize(60, innerTable.getRowHeight() * innerTable.getRowCount());
    }

    public ClassConfigView(EasyJavadocConfiguration config) {
        super(config);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}
