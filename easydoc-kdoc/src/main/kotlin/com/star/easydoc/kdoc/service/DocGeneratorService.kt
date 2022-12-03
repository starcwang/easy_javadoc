package com.star.easydoc.kdoc.service

import com.intellij.psi.PsiElement
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.generator.impl.KtClassDocGenerator
import com.star.easydoc.kdoc.service.generator.impl.KtNamedFunctionDocGenerator
import com.star.easydoc.kdoc.service.generator.impl.KtObjectDocGenerator
import com.star.easydoc.kdoc.service.generator.impl.KtPropertyDocGenerator
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import java.util.*

/**
 * @author wangchao
 * @date 2019/08/25
 */
class DocGeneratorService {
    private val docGeneratorMap = mapOf(
        KtClass::class to KtClassDocGenerator(),
        KtObjectDeclaration::class to KtObjectDocGenerator(),
        KtNamedFunction::class to KtNamedFunctionDocGenerator(),
        KtProperty::class to KtPropertyDocGenerator()
    )

    fun generate(psiElement: PsiElement): String {
        var docGenerator: DocGenerator? = null
        for ((key, value) in docGeneratorMap) {
            if (psiElement::class == key) {
                docGenerator = value
                break
            }
        }
        return if (Objects.isNull(docGenerator)) {
            StringUtils.EMPTY
        } else docGenerator!!.generate(psiElement)
    }
}