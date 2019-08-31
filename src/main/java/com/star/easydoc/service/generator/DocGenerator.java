package com.star.easydoc.service.generator;

import com.intellij.psi.PsiElement;

/**
 * @author wangchao
 * @date 2019/08/31
 */
public interface DocGenerator {
    /**
     * 生成
     *
     * @param psiElement psiElement
     * @return java.lang.String
     */
    String generate(PsiElement psiElement);
}
