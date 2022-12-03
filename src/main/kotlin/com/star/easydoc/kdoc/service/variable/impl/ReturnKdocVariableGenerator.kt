package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement
import com.star.easydoc.config.EasyDocConfig
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ReturnKdocVariableGenerator : AbstractKdocVariableGenerator() {
    override fun generate(element: PsiElement): String {
        if (element !is KtNamedFunction) {
            return ""
        }
        return if (element.hasDeclaredReturnType()) {
            if (config.kdocParamType == EasyDocConfig.LINK_PARAM_TYPE) {
                "@return [${element.typeReference!!.text}]"
            } else {
                "@return ${element.typeReference!!.text}"
            }
        } else {
            ""
        }
    }
}