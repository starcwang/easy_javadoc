package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement
import com.star.easydoc.config.EasyDocConfig
import org.jetbrains.kotlin.psi.KtClass

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
class ConstructorKdocVariableGenerator : AbstractKdocVariableGenerator() {
    override fun generate(element: PsiElement): String {
        if (element !is KtClass) {
            return ""
        }

        return if (config.kdocParamType == EasyDocConfig.LINK_PARAM_TYPE) {
            "创建[${element.name}]"
        } else {
            "创建${element.name}"
        }
    }
}