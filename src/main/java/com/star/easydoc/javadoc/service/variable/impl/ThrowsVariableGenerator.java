package com.star.easydoc.javadoc.service.variable.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.service.translator.TranslatorService;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ThrowsVariableGenerator extends AbstractVariableGenerator {
    private final TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }
        List<PsiClassType> exceptionTypeList = Arrays.stream(((PsiMethod)element).getThrowsList().getReferencedTypes())
            .collect(Collectors.toList());
        if (exceptionTypeList.isEmpty()) {
            return "";
        }
        return exceptionTypeList.stream()
            .map(type -> "@throws " + type.getName() + " "
                + translatorService.translateWithClass(type.getName(), type.getCanonicalText(), element.getProject()))
            .collect(Collectors.joining("\n"));
    }
}