package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.util.containers.isNullOrEmpty
import com.star.easydoc.config.EasyDocConfig
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
class ParamsKdocVariableGenerator : AbstractKdocVariableGenerator() {
    private val translatorService = ServiceManager.getService(com.star.easydoc.service.translator.TranslatorService::class.java)
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
            var thisParam = param
            if (config.kdocParamType == EasyDocConfig.LINK_PARAM_TYPE) {
                thisParam = "[${param}]"
            }
            if (paramValues.containsKey(param)) {
                paramsDoc.add("${if (paramsDoc.isEmpty()) "" else "* "}@param $thisParam ${paramValues[param]}")
            } else {
                paramsDoc.add(
                    "${if (paramsDoc.isEmpty()) "" else "* "}@param $thisParam ${
                        translatorService.translate(
                            param
                        )
                    }"
                )
            }
        }

        return paramsDoc.joinToString("\n")
    }
}