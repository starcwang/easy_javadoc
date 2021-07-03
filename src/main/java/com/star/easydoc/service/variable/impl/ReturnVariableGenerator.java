package com.star.easydoc.service.variable.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.config.Consts;
import com.star.easydoc.service.variable.VariableGenerator;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ReturnVariableGenerator implements VariableGenerator {

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }
        PsiMethod psiMethod = (PsiMethod) element;
        String returnName = psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getPresentableText();

        if (Consts.BASE_TYPE_SET.contains(returnName)) {
            return "@return " + returnName;
        } else if ("void".equalsIgnoreCase(returnName)) {
            return "";
        } else {
            return String.format("@return {@link %s }", returnName);
        }
    }
}