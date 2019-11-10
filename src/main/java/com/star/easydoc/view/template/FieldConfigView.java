package com.star.easydoc.view.template;

import com.star.easydoc.model.EasyJavadocConfiguration;

import javax.swing.*;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public class FieldConfigView extends AbstractTemplateConfigView {

    private JPanel panel;
    private JTextArea textArea1;
    private JPanel innerVariablePanel;
    private JPanel customVariablePanel;
    private JPanel template;
    private JRadioButton defaultRadioButton;
    private JRadioButton customRadioButton;

    public FieldConfigView(EasyJavadocConfiguration config) {
        super(config);
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }
}
