package com.star.easydoc.kdoc.service.variable.impl

import com.google.common.collect.Lists
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.star.easydoc.common.Consts
import java.util.*
import java.util.stream.Collectors

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:17:00
 */
class SeeVariableGenerator : AbstractVariableGenerator() {
    override fun generate(element: PsiElement): String {
        return if (element is PsiClass) {
            val superClass = element.superClass
            val interfaces = element.interfaces
            val superList: MutableList<String> = Lists.newArrayList()
            if (superClass != null && !"Object".equals(superClass.name, ignoreCase = true)) {
                superList.add(superClass.name!!)
            }
            if (interfaces.isNotEmpty()) {
                superList.addAll(interfaces.mapNotNull { obj: PsiClass -> obj.name }.toList())
            }
            superList.stream().map { sup: String -> "@see $sup" }.collect(Collectors.joining("\n"))
        } else if (element is PsiMethod) {
            val seeString = StringBuilder()
            val parameterList = element.parameterList
            for (parameter in parameterList.parameters) {
                if (parameter == null || parameter.typeElement == null) {
                    continue
                }
                seeString.append("@see ").append(parameter.typeElement!!.text).append("\n")
            }
            val returnTypeElement = element.returnTypeElement
            if (returnTypeElement != null && "void" != returnTypeElement.text) {
                seeString.append("@see ").append(returnTypeElement.text).append("\n")
            }
            seeString.toString()
        } else if (element is PsiField) {
            val type = element.type.presentableText
            if (Consts.BASE_TYPE_SET.contains(type)) {
                ""
            } else "@see $type"
        } else {
            ""
        }
    }
}