package com.star.easydoc.view.inner;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

/**
 * WordMapAddView 是一个继承自 DialogWrapper 的对话框类，
 * 用于添加单词映射的界面显示和数据交互。
 */
public class WordMapAddView extends DialogWrapper {

    private JPanel panel;
    private JTextField sourceTextField;
    private JTextField targetTextField;
    private JLabel source;
    private JLabel target;

    /**
     * 构造方法，用于初始化对话框的界面和标题。
     */
    public WordMapAddView() {
        super(false); // 设置为非模态对话框
        init(); // 初始化对话框
        setTitle("添加单词映射"); // 设置对话框标题
    }

    /**
     * 创建对话框的中心面板，用于显示对话框的内容。
     * @return 对话框的中心面板
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel; // 返回包含组件的面板
    }

    /**
     * 验证用户输入的方法，用于在提交前校验表单数据的有效性。
     * @return 验证结果，返回 null 表示验证通过，非空 ValidationInfo 表示验证失败并提供错误信息和焦点元素
     */
    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (sourceTextField.getText() == null || sourceTextField.getText().length() <= 0) {
            return new ValidationInfo("请输入原单词", sourceTextField); // 返回输入错误信息和焦点元素
        }
        if (targetTextField.getText() == null || targetTextField.getText().length() <= 0) {
            return new ValidationInfo("请输入转换后的单词", targetTextField); // 返回输入错误信息和焦点元素
        }
        return super.doValidate(); // 返回 null 表示验证通过
    }

    /**
     * 获取用户输入的单词映射。
     * @return 单词映射，使用 AbstractMap.SimpleEntry 封装源单词和目标单词
     */
    public Map.Entry<String, String> getMapping() {
        return new SimpleEntry<>(sourceTextField.getText().toLowerCase(), targetTextField.getText()); // 返回封装的单词映射
    }
}
