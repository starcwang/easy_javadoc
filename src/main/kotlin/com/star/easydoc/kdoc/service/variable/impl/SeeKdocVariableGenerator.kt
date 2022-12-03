package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement
import com.star.easydoc.config.EasyDocConfig
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import java.util.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:17:00
 */
class SeeKdocVariableGenerator : AbstractKdocVariableGenerator() {
    override fun generate(element: PsiElement): String {
        return if (element is KtClass) {
            if (EasyDocConfig.LINK_PARAM_TYPE == config.kdocParamType) {
                return "@see [${element.name}]"
            } else {
                return "@see ${element.name}"
            }
        } else if (element is KtNamedFunction) {
            if (element.hasDeclaredReturnType()) {
                if (EasyDocConfig.LINK_PARAM_TYPE == config.kdocParamType) {
                    return "@see [${element.typeReference!!.text}]"
                } else {
                    return "@see ${element.typeReference!!.text}"
                }
            } else {
                return ""
            }
        } else if (element is KtProperty) {
            if (EasyDocConfig.LINK_PARAM_TYPE == config.kdocParamType) {
                return "@see [${StringUtils.strip(element.typeReference?.typeElement?.text, "?")}]"
            } else {
                return "@see ${StringUtils.strip(element.typeReference?.typeElement?.text, "?")}"
            }
        } else {
            ""
        }
    }
}