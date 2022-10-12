package com.star.easydoc.kdoc.service.generator

import com.intellij.psi.PsiElement

/**
 * 文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
interface DocGenerator {
    /**
     * 生成
     *
     * @param psiElement psiElement
     * @return java.lang.String
     */
    fun generate(psiElement: PsiElement): String
}