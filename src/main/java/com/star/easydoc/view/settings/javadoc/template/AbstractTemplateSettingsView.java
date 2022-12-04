package com.star.easydoc.view.settings.javadoc.template;

import java.util.Vector;

import javax.swing.*;

import com.star.easydoc.config.EasyDocConfig;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public abstract class AbstractTemplateSettingsView {
    protected static Vector<String> customNames;
    protected static Vector<String> innerNames;

    static {
        customNames = new Vector<>(3);
        customNames.add("变量");
        customNames.add("类型");
        customNames.add("自定义值");

        innerNames = new Vector<>(2);
        innerNames.add("变量");
        innerNames.add("含义");
    }

    protected EasyDocConfig config;

    public AbstractTemplateSettingsView(EasyDocConfig config) {
        this.config = config;
    }

    public abstract JComponent getComponent();
}
