package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import org.jetbrains.kotlin.idea.util.CommentSaver.Companion.tokenType
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.util.*

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
class DocKdocVariableGenerator : AbstractKdocVariableGenerator() {
    private val translatorService = ServiceManager.getService(com.star.easydoc.service.translator.TranslatorService::class.java)
    override fun generate(element: PsiElement): String {
        if (element is KtDeclaration) {
            val doc = translatorService.translate((element as PsiNamedElement).name).trim()
            val docComment = (element as KtDeclaration).docComment
            if (docComment != null) {
                val docElements = docComment.children
                val kDocSection = docElements.findLast { e -> e is KDocSection }
                if (kDocSection != null) {
                    val docs = kDocSection.allChildren
                        .filter { e -> e.tokenType.toString() == "KDOC_TEXT" }
                        .map { e -> e.text.trim() }
                        .filter { e -> e.isNotBlank() }
                        .toList()
                    if (docs.isEmpty()) {
                        return doc
                    }
                    if (docs.any { e -> e == doc }) {
                        return docs.joinToString("\n* ")
                    } else {
                        val allDocs = mutableListOf<String>()
                        allDocs.addAll(docs)
                        allDocs.add(doc)
                        return allDocs.joinToString("\n* ")
                    }
                }
            }
            return doc
        }
        return ""
    }
}