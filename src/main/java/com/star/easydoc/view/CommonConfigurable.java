package com.star.easydoc.view;

import java.util.Objects;
import java.util.TreeMap;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class CommonConfigurable implements Configurable {

    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();
    private CommonConfigView view = new CommonConfigView(config);

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyJavadoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public boolean isModified() {
        if (!Objects.equals(config.getAuthor(), view.getAuthorTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getDateFormat(), view.getDateFormatTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getSimpleFieldDoc(), view.getSimpleDocButton().isSelected())) {
            return true;
        }
        if (!Objects.equals(config.getTranslator(), view.getTranslatorBox().getSelectedItem())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setAuthor(view.getAuthorTextField().getText());
        config.setDateFormat(view.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(view.getSimpleDocButton().isSelected());
        config.setTranslator(String.valueOf(view.getTranslatorBox().getSelectedItem()));
        if (config.getWordMap() == null) {
            config.setWordMap(new TreeMap<>());
        }
    }

    @Override
    public void reset() {
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            view.setSimpleDocButton(true);
            view.setNormalDocButton(false);
        } else {
            view.setSimpleDocButton(false);
            view.setNormalDocButton(true);
        }
        view.setAuthorTextField(config.getAuthor());
        view.setDateFormatTextField(config.getDateFormat());
        view.setTranslatorBox(config.getTranslator());
    }
}
