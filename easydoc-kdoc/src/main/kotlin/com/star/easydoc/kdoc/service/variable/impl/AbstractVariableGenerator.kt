package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.components.ServiceManager
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.variable.VariableGenerator

/**
 * 变量生成器
 *
 * @author wangchao
 * @date 2022/10/01
 */
abstract class AbstractVariableGenerator : VariableGenerator {
    override val config: EasyDocConfig
        get() = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
}