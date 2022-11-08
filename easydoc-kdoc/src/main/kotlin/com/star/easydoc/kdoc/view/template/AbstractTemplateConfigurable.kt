package com.star.easydoc.kdoc.view.template

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-10 18:14:00
 */
abstract class AbstractTemplateConfigurable : Configurable {

    override fun createComponent(): JComponent {
        return view.component
    }

    abstract val view: AbstractTemplateConfigView
}