package com.star.easydoc.view.inner;

import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-10-28 00:30:00
 */
public class GenerateAllView extends DialogWrapper {
    private JCheckBox fieldCheckBox;
    private JCheckBox methodCheckBox;
    private JCheckBox classCheckBox;
    private JPanel panel;
    private JCheckBox innerClassCheckBox;

    public GenerateAllView() {
        super(false);
        init();
        setTitle("请选择生成内容");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public JCheckBox getFieldCheckBox() {
        return fieldCheckBox;
    }

    public JCheckBox getMethodCheckBox() {
        return methodCheckBox;
    }

    public JCheckBox getClassCheckBox() {
        return classCheckBox;
    }

    public JCheckBox getInnerClassCheckBox() {
        return innerClassCheckBox;
    }

    public JPanel getPanel() {
        return panel;
    }
}
