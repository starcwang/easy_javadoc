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
 * 实现了Configurable接口。该类的作用是提供EasyDocJavadoc插件的设置界面
 */
public class JavadocSettingsConfigurable implements Configurable {
    // 获取EasyDocConfigComponent的状态
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    // 创建JavadocSettingsView对象
    private JavadocSettingsView view = new JavadocSettingsView();
    // 获取显示名称
    @Nls(capitalization = Capitalization.Title)
    @Override

    public String getDisplayName() {
        return "EasyDocJavadoc";
    }
    // 创建设置界面
    @Nullable
    @Override
    public JComponent createComponent() {
        return view.getComponent();
    }
    // 判断设置是否被修改
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
    // 应用设置
    @Override
    public void apply() throws ConfigurationException {
        config.setAuthor(view.getAuthorTextField().getText());
        config.setDateFormat(view.getDateFormatTextField().getText());
        config.setSimpleFieldDoc(view.getSimpleDocButton().isSelected());
        config.setMethodReturnType(view.getMethodReturnType());
        config.setDocPriority(view.getDocPriority());
        // 检查设置是否合法
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
    // 重置设置
    @Override
    public void reset() {
        view.refresh();
    }
}
