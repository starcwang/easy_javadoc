package com.star.easydoc.view.template;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:35:00
 */
public class ClassConfigurable extends AbstractTemplateConfigurable {
    private ClassConfigView view = new ClassConfigView(config);

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "类注释模板配置";
    }

    @Override
    public AbstractTemplateConfigView getView() {
        return view;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
