package com.star.easydoc.service;

import com.intellij.psi.PsiElement;

/**
 * 文档生成服务
 *
 * @author wangchao
 * @date 2022/12/03
 */
public interface DocGeneratorService {

    /**
     * 生成
     *
     * @param psiElement 元素
     * @return {@link String }
     */
    String generate(PsiElement psiElement);
}
