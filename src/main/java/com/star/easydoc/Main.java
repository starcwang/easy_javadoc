package com.star.easydoc;

import com.google.common.collect.Maps;
import com.intellij.ide.ui.EditorOptionsTopHitProvider.Ex;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 20:48:00
 */
public class Main extends JFrame {



    public static void main(String[] args) throws Exception {
        // 显示应用 GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 确保一个漂亮的外观风格

                // 创建及设置窗口
                JFrame frame = new JFrame("HelloWorldSwing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                 Map<String, String> innerList;

                innerList = new HashMap<>();
                innerList.put("1", "a");
                innerList.put("2", "b");
                innerList.put("3", "c");

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
                JTable innerTable = new JTable(data, names);
                JTableHeader tableHeader = innerTable.getTableHeader();// 获得表格头对象
                JTextArea jTextArea = new JTextArea();
                jTextArea.append("xxxxxxxxxxxxxxxxxxxxxx");
                jTextArea.setEnabled(false);
                // 将表格头添加到边界布局的上方
                frame.add(tableHeader, BorderLayout.NORTH);


                frame.getContentPane().add(innerTable);
                frame.getContentPane().add(jTextArea);

                // 显示窗口
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
