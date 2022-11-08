package com.star.easydoc.kdoc.view.template

import com.star.easydoc.common.config.EasyDocConfig
import java.util.*
import javax.swing.JComponent

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
abstract class AbstractTemplateConfigView(protected var config: EasyDocConfig) {
    abstract val component: JComponent

    companion object {
        val customNames: Vector<String> = Vector(3)

        val innerNames: Vector<String> = Vector(2)

        init {
            customNames.add("变量")
            customNames.add("类型")
            customNames.add("自定义值")
            innerNames.add("变量")
            innerNames.add("含义")
        }
    }
}