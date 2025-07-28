package com.star.easydoc.javadoc.service.variable.impl;

import com.intellij.psi.PsiElement;

/**
 * since 变量生成器
 *
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @date 2025/07/28
 */
public class SinceVariableGenerator extends AbstractVariableGenerator {

    @Override
    public String generate(PsiElement element) {
        return "1.0.0";
    }

}