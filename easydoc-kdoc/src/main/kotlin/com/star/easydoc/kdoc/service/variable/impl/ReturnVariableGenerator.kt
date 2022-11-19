package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.star.easydoc.common.Consts
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ReturnVariableGenerator : AbstractVariableGenerator() {
    override fun generate(element: PsiElement): String {
        if (element !is KtNamedFunction) {
            return ""
        }
        return if (element.hasDeclaredReturnType()) {
            if (config.paramType == config.LINK_PARAM_TYPE) {
                "@return [${element.typeReference!!.text}]"
            } else {
                "@return ${element.typeReference!!.text}"
            }
        } else {
            ""
        }
    }
}