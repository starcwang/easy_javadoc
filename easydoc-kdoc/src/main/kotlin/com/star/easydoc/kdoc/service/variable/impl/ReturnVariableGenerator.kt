package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.star.easydoc.common.Consts

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ReturnVariableGenerator : AbstractVariableGenerator() {
    override fun generate(element: PsiElement): String {
        if (element !is PsiMethod) {
            return ""
        }
        val psiMethod = element
        val returnName = if (psiMethod.returnTypeElement == null) "" else psiMethod.returnTypeElement!!.text
        return if (Consts.BASE_TYPE_SET.contains(returnName)) {
            "@return $returnName"
        } else if ("void".equals(returnName, ignoreCase = true)) {
            ""
        } else {
            if (config.isCodeMethodReturnType) {
                return "@return {@code $returnName }"
            } else if (config.isLinkMethodReturnType) {
                return "@return " + returnName.replace("[^<> ,]+".toRegex(), "{@link $0 }")
            }
            String.format("@return {@link %s }", returnName)
        }
    }
}