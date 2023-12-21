package com.star.easydoc.view.inner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nullable;

public class PackageDescribeView extends DialogWrapper {
    private JPanel panel1;
    private JTable packageInfoTable;

    public PackageDescribeView(Map<PsiPackage, String> packMap) {
        super(false);

        this.packMap = packMap;
        createMap(packMap);

        init();
        setTitle("包信息生成");
    }

    // 存储传入的包信息
    private Map<PsiPackage, String> packMap;
    private Map<Integer, PsiPackage> packIndexMap;

    // 获取用户输入的最终包信息列表
    public Map<PsiPackage, String> getFinalMap() {
        TableCellEditor editor = packageInfoTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }
        Map<PsiPackage, String> finalPackMap = new HashMap<>();
        // 遍历包信息表格中的每一行
        for (Entry<Integer, PsiPackage> entry : packIndexMap.entrySet()) {
            PsiPackage psiPackage = entry.getValue();
            String value = (String)packageInfoTable.getValueAt(entry.getKey(), 1);
            finalPackMap.put(psiPackage, value);
        }
        return finalPackMap;
    }

    // 根据传入的包信息创建表格内容并初始化表格

    public void createMap(Map<PsiPackage, String> packMap) {
        List<Map.Entry<PsiPackage, String>> list = new ArrayList<>(packMap.entrySet());
        String[][] objs = new String[list.size()][2];
        packIndexMap = new HashMap<>();
        // 遍历包信息列表
        for (int i = 0; i < list.size(); i++) {
            PsiPackage aPackage = list.get(i).getKey();
            objs[i][0] = aPackage.getQualifiedName(); // 包的全限定名称作为第一列
            objs[i][1] = list.get(i).getValue(); // 包的注释作为第二列
            packIndexMap.put(i, aPackage);
        }
        DefaultTableModel innerModel = new DefaultTableModel(objs, new String[] {"包名称", "注释"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // 第二列可编辑
            }
        };
        packageInfoTable.setModel(innerModel); // 将创建的表格模型设置给表格
    }

    // 创建对话框的中心内容面板
    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        return panel1;
    }
}
