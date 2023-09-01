package com.star.easydoc.view.settings.javadoc;

import java.util.Objects;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class JavadocSettingsConfigurable implements Configurable {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private JavadocSettingsView view = new JavadocSettingsView();

    @Nls(capitalization = Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EasyDocJavadoc";
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
        if (!Objects.equals(config.getDocPriority(), view.getDocPriority())) {
            return true;
        }
        if (!Objects.equals(config.getSimpleFieldDoc(), view.getSimpleDocButton().isSelected())) {
            return true;
        }
        if (!Objects.equals(config.getMethodReturnType(), view.getMethodReturnType())) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        config.setAuthor(view.getAuthorTextField().getText());
        config.setDateFormat(view.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(view.getSimpleDocButton().isSelected());
        config.setMethodReturnType(view.getMethodReturnType());
        config.setDocPriority(view.getDocPriority());

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
        if (!EasyDocConfig.CODE_RETURN_TYPE.equals(config.getMethodReturnType())
            && !EasyDocConfig.LINK_RETURN_TYPE.equals(config.getMethodReturnType())
            && !EasyDocConfig.DOC_RETURN_TYPE.equals(config.getMethodReturnType())) {
            throw new ConfigurationException("方法返回模式不能为空");
        }
    }

    @Override
    public void reset() {
        view.refresh();
    }
}
