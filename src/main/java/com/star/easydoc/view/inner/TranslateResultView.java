package com.star.easydoc.view.inner;

import javax.swing.*;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * 翻译结果显示窗口
 */
public class TranslateResultView extends DialogWrapper {

    // 文本区域组件
    private JTextArea textArea;
    // 面板组件
    private JPanel panel;
    // 滚动面板组件
    private JScrollPane scrollPane;

    /**
     * 构造方法
     * @param text 翻译结果文本
     */
    public TranslateResultView(String text) {
        // 调用 DialogWrapper 构造方法
        super(false);
        // 初始化对话框
        init();
        // 设置对话框标题
        setTitle("翻译结果");
        // 设置文本区域组件尺寸
        textArea.setSize(800, 800);
        // 设置文本区域组件文本
        textArea.setText(text);
    }

    /**
     * 创建中央面板
     * @return 面板组件
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    /**
     * 获取文本区域组件
     * @return 文本区域组件
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * 获取面板组件
     * @return 面板组件
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * 获取滚动面板组件
     * @return 滚动面板组件
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
