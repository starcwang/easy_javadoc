package com.star.easydoc.view.settings.javadoc.template;

import java.util.Vector;

import javax.swing.*;

import com.star.easydoc.config.EasyDocConfig;

/**
 * 用于生成模板设置视图
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
public abstract class AbstractTemplateSettingsView {
    protected static Vector<String> customNames;
    protected static Vector<String> innerNames;

    /**
     * 自定义设置项的名称列表。
     */
    static {
        customNames = new Vector<>(3);
        customNames.add("变量");
        customNames.add("类型");
        customNames.add("自定义值");
    }

    /**
     * 内部设置项的名称列表。
     */
    static {
        innerNames = new Vector<>(2);
        innerNames.add("变量");
        innerNames.add("含义");
    }

    protected EasyDocConfig config;

    /**
     * 使用给定的配置构造AbstractTemplateSettingsView的新实例。
     *
     * @param config EasyDoc配置。
     */
    public AbstractTemplateSettingsView(EasyDocConfig config) {
        this.config = config;
    }

    /**
     * 获取与设置视图关联的组件。
     *
     * @return 表示设置视图的组件。
     */
    public abstract JComponent getComponent();
}
