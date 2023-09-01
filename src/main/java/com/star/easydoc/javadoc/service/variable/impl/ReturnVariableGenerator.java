package com.star.easydoc.javadoc.service.variable.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.star.easydoc.common.Consts;
import com.star.easydoc.service.translator.TranslatorService;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ReturnVariableGenerator extends AbstractVariableGenerator {
    private final TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }
        PsiMethod psiMethod = (PsiMethod)element;
        PsiTypeElement returns = psiMethod.getReturnTypeElement() == null ? null : psiMethod.getReturnTypeElement();
        if (returns == null) {
            return "";
        }
        String returnName = returns.getText();

        if (Consts.BASE_TYPE_SET.contains(returnName)) {
            return "@return " + returnName;
        } else if ("void".equalsIgnoreCase(returnName)) {
            return "";
        } else {
            if (getConfig().isCodeMethodReturnType()) {
                return "@return {@code " + returnName + " }";
            } else if (getConfig().isLinkMethodReturnType()) {
                return "@return " + returnName.replaceAll("[^<> ,]+", "{@link $0 }");
            } else if (getConfig().isDocMethodReturnType()) {
                return "@return " + translatorService.translateWithClass(returnName, returns.getType().getCanonicalText(), element.getProject());
            }
            return String.format("@return {@link %s }", returnName);
        }
    }
}