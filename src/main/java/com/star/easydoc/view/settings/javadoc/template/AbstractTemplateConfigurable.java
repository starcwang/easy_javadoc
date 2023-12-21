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

    /**
     * 这个方法返回一个 JComponent 组件，用于显示配置界面。具体实现是通过调用 getView().getComponent() 来获取视图组件
     * @return
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        return getView().getComponent();
    }

    /**
     * 这个抽象方法需要在子类中实现，用于获取配置界面的视图对象
     * @return
     */
    public abstract T getView();
}
