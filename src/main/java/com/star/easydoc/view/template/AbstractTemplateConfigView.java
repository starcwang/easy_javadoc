package com.star.easydoc.view.template;

import com.star.easydoc.model.EasyJavadocConfiguration;

import javax.swing.*;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public abstract class AbstractTemplateConfigView {
    protected EasyJavadocConfiguration config;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public AbstractTemplateConfigView(EasyJavadocConfiguration config) {
        this.config = config;
    }

    public abstract JComponent getComponent();
}
