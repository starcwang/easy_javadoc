package com.star.easydoc.javadoc.service.variable.impl;

import com.intellij.psi.PsiElement;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
public class AuthorVariableGenerator extends AbstractVariableGenerator {

    @Override
    public String generate(PsiElement element) {
        return getConfig().getAuthor();
    }

}