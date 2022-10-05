package com.star.easydoc.javadoc.service.variable.impl;

import com.intellij.psi.PsiElement;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:17:00
 */
public class VersionVariableGenerator extends AbstractVariableGenerator {

    @Override
    public String generate(PsiElement element) {
        return "1.0.0";
    }

}
