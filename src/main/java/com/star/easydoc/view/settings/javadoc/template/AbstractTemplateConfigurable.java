package com.star.easydoc.view.settings.javadoc.template;

import javax.swing.*;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 18:14:00
 */
public abstract class AbstractTemplateConfigurable<T extends AbstractTemplateSettingsView> implements Configurable {

    @Nullable
    @Override
    public JComponent createComponent() {
        return getView().getComponent();
    }

    public abstract T getView();
}
