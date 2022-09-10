package com.star.easydoc.service.variable.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.config.Consts;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.variable.VariableGenerator;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ReturnVariableGenerator implements VariableGenerator {

    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }
        PsiMethod psiMethod = (PsiMethod)element;
        String returnName = psiMethod.getReturnTypeElement() == null ? "" : psiMethod.getReturnTypeElement().getText();

        if (Consts.BASE_TYPE_SET.contains(returnName)) {
            return "@return " + returnName;
        } else if ("void".equalsIgnoreCase(returnName)) {
            return "";
        } else {
            if (config.isCodeMethodReturnType()) {
                return "@return {@code " + returnName + " }";
            } else if (config.isLinkMethodReturnType()) {
                return "@return " + returnName.replaceAll("[^<> ,]+", "{@link $0 }");
            }
            return String.format("@return {@link %s }", returnName);
        }
    }
}