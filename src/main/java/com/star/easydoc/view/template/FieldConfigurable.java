package com.star.easydoc.view.template;

import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:35:00
 */
public class FieldConfigurable extends AbstractTemplateConfigurable {
    private FieldConfigView view = new FieldConfigView(config);

    @Override
    public AbstractTemplateConfigView getView() {
        return view;
    }

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "属性注释模板配置";
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}
