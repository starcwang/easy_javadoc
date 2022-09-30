package com.star.easydoc.view.inner;

import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2020/02/12
 */
public class TranslateResultView extends DialogWrapper {

    private JTextArea textArea;
    private JPanel panel;
    private JScrollPane scrollPane;

    public TranslateResultView(String text) {
        super(false);
        init();
        setTitle("翻译结果");
        textArea.setSize(800, 800);
        textArea.setText(text);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }
}
