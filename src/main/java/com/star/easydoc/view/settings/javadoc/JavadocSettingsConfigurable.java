package com.star.easydoc.view.settings.javadoc;

import java.util.Objects;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class JavadocSettingsConfigurable implements Configurable {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    /** Lazy: Settings tree instantiates Configurable without building UI */
    private JavadocSettingsView view;

    private JavadocSettingsView getView() {
        if (view == null) {
            view = new JavadocSettingsView();
        }
        return view;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "EasyDocJavadoc";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return getView().getComponent();
    }

    @Override
    public boolean isModified() {
        if (view == null) {
            return false;
        }
        if (!Objects.equals(config.getAuthor(), view.getAuthorTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getDateFormat(), view.getDateFormatTextField().getText())) {
            return true;
        }
        if (!Objects.equals(config.getDocPriority(), view.getDocPriority())) {
            return true;
        }
        if (!Objects.equals(config.getSimpleFieldDoc(), view.getSimpleDocButton().isSelected())) {
            return true;
        }
        if (!Objects.equals(config.getMethodReturnType(), view.getMethodReturnType())) {
            return true;
        }
        if (!Objects.equals(config.getCoverMode(), String.valueOf(view.getCoverModeBox().getSelectedItem()))) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        JavadocSettingsView settingsView = getView();
        config.setAuthor(settingsView.getAuthorTextField().getText());
        config.setDateFormat(settingsView.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(settingsView.getSimpleDocButton().isSelected());
        config.setMethodReturnType(settingsView.getMethodReturnType());
        config.setDocPriority(settingsView.getDocPriority());
        config.setCoverMode(String.valueOf(settingsView.getCoverModeBox().getSelectedItem()));

        if (config.getAuthor() == null) {
            throw new ConfigurationException("作者不能为null");
        }
        if (config.getDateFormat() == null) {
            throw new ConfigurationException("日期格式不能为null");
        }
        if (config.getDocPriority() == null) {
            throw new ConfigurationException("类注释优先级不能为null");
        }
        if (config.getSimpleFieldDoc() == null) {
            throw new ConfigurationException("注释形式不能为null");
        }
        if (config.getCoverMode() == null) {
            throw new ConfigurationException("注释覆盖模式不能为null");
        }
        if (!EasyDocConfig.CODE_RETURN_TYPE.equals(config.getMethodReturnType())
            && !EasyDocConfig.LINK_RETURN_TYPE.equals(config.getMethodReturnType())
            && !EasyDocConfig.DOC_RETURN_TYPE.equals(config.getMethodReturnType())) {
            throw new ConfigurationException("方法返回模式不能为空");
        }
    }

    @Override
    public void reset() {
        getView().refresh();
    }
}
