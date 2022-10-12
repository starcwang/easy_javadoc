package com.star.easydoc.kdoc.service.variable.impl

import com.google.common.base.Joiner
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaDocumentedElement
import com.intellij.psi.PsiNamedElement
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang.StringUtils
import java.util.*
import java.util.stream.Collectors

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
class DocVariableGenerator : AbstractVariableGenerator() {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    override fun generate(element: PsiElement): String {
        if (element is PsiNamedElement) {
            val docComment = (element as PsiJavaDocumentedElement).docComment
            if (docComment != null) {
                val descriptionElements = docComment.descriptionElements
                val descTextList = Arrays.stream(descriptionElements).map { obj: PsiElement -> obj.text }.collect(Collectors.toList())
                val result = Joiner.on(StringUtils.EMPTY).skipNulls().join(descTextList)
                return if (StringUtils.isNotBlank(result)) result else translatorService.translate((element as PsiNamedElement).name)
            }
            return translatorService.translate((element as PsiNamedElement).name)
        }
        return ""
    }
}