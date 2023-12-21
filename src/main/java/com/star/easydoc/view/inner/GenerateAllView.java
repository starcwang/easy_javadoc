package com.star.easydoc.view.inner;
import javax.swing.*;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * 生成全部视图类
 * 用于展示生成内容选项的对话框，继承自DialogWrapper类
 */
public class GenerateAllView extends DialogWrapper {
    private JCheckBox fieldCheckBox;          // 声明JCheckBox对象，用于选择生成字段
    private JCheckBox methodCheckBox;         // 声明JCheckBox对象，用于选择生成方法
    private JCheckBox classCheckBox;          // 声明JCheckBox对象，用于选择生成类
    private JPanel panel;                     // 声明JPanel对象，用于存放复选框-
    private JCheckBox innerClassCheckBox;     // 声明JCheckBox对象，用于选择生成内部类

    /**
     * 构造方法，初始化对话框
     */
    public GenerateAllView() {
        super(false);
        init();
        setTitle("请选择生成内容"); // 设置对话框标题
    }

    /**
     * 创建中心面板
     * @return JComponent对象，作为中心面板的组件
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    /**
     * 获取字段复选框
     * @return JCheckBox对象，用于选择生成字段
     */
    public JCheckBox getFieldCheckBox() {
        return fieldCheckBox;
    }

    /**
     * 获取方法复选框
     * @return JCheckBox对象，用于选择生成方法
     */
    public JCheckBox getMethodCheckBox() {
        return methodCheckBox;
    }

    /**
     * 获取类复选框
     * @return JCheckBox对象，用于选择生成类
     */
    public JCheckBox getClassCheckBox() {
        return classCheckBox;
    }

    /**
     * 获取内部类复选框
     * @return JCheckBox对象，用于选择生成内部类
     */
    public JCheckBox getInnerClassCheckBox() {
        return innerClassCheckBox;
    }

    /**
     * 获取面板
     * @return JPanel对象，用于存放复选框
     */
    public JPanel getPanel() {
        return panel;
    }
}
