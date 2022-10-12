package com.star.easydoc.kdoc.service

import com.google.common.collect.ImmutableMap
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.generator.impl.ClassDocGenerator
import com.star.easydoc.kdoc.service.generator.impl.FieldDocGenerator
import com.star.easydoc.kdoc.service.generator.impl.MethodDocGenerator
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * @author wangchao
 * @date 2019/08/25
 */
class DocGeneratorService {
    private val docGeneratorMap: Map<Class<out PsiElement>, DocGenerator> = ImmutableMap.builder<Class<out PsiElement>, DocGenerator>()
        .put(PsiClass::class.java, ClassDocGenerator())
        .put(PsiMethod::class.java, MethodDocGenerator())
        .put(PsiField::class.java, FieldDocGenerator())
        .build()

    fun generate(psiElement: PsiElement): String {
        var docGenerator: DocGenerator? = null
        for ((key, value) in docGeneratorMap) {
            if (key.isAssignableFrom(psiElement.javaClass)) {
                docGenerator = value
                break
            }
        }
        return if (Objects.isNull(docGenerator)) {
            StringUtils.EMPTY
        } else docGenerator!!.generate(psiElement)
    }
}