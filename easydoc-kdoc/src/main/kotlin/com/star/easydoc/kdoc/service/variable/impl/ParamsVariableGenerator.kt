package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.util.containers.isNullOrEmpty
import com.star.easydoc.service.translator.TranslatorService
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getValueParameters
import java.util.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ParamsVariableGenerator : AbstractVariableGenerator() {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    override fun generate(element: PsiElement): String {
        if (element !is KtNamedDeclaration) {
            return ""
        }
        val params = element.getValueParameters().map { e -> e.name }.toList()
        if (params.isNullOrEmpty()) {
            return ""
        }
        val kdoc = element.docComment
        val paramValues = mutableMapOf<String, String>()
        if (kdoc != null) {
            val docElements = kdoc.children
            val kDocSection = docElements.findLast { e -> e is KDocSection }
            if (kDocSection != null) {
                val paramTags = kDocSection.children.filterIsInstance<KDocTag>()
                    .filter { e -> e.name == "param" }
                    .filter { e -> e.getSubjectName() != null }.toList()
                for (paramTag in paramTags) {
                    if (paramTag.getContent().isNotBlank()) {
                        paramValues[paramTag.getSubjectName()!!] = paramTag.getContent().trim()
                    }
                }
            }
        }
        val paramsDoc = mutableListOf<String>()
        for (param in params) {
            if (paramValues.containsKey(param)) {
                if (paramsDoc.isEmpty()) {
                    paramsDoc.add("@param " + param + " " + paramValues[param])
                } else {
                    paramsDoc.add("* @param " + param + " " + paramValues[param])
                }
            } else {
                if (paramsDoc.isEmpty()) {
                    paramsDoc.add("@param " + param + " " + translatorService.translate(param))
                } else {
                    paramsDoc.add("* @param " + param + " " + translatorService.translate(param))
                }
            }
        }

        return paramsDoc.joinToString("\n")
    }
}