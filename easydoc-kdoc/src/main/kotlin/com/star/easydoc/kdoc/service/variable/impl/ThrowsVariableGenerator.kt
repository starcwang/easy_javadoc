package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.star.easydoc.service.translator.TranslatorService
import java.util.*
import java.util.stream.Collectors

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ThrowsVariableGenerator : AbstractVariableGenerator() {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    override fun generate(element: PsiElement): String {
        if (element !is PsiMethod) {
            return ""
        }
        val exceptionNameList = Arrays.stream(element.throwsList.referencedTypes)
            .map { obj: PsiClassType -> obj.name }.collect(Collectors.toList())
        return if (exceptionNameList.isEmpty()) {
            ""
        } else exceptionNameList.stream()
            .map { name: String -> "@throws " + name + " " + translatorService.translate(name) }
            .collect(Collectors.joining("\n"))
    }
}