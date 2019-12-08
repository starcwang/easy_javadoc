package com.star.easydoc.service.variable.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.variable.VariableGenerator;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ThrowsVariableGenerator implements VariableGenerator {
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }
        List<String> exceptionNameList = Arrays.stream(((PsiMethod)element).getThrowsList().getReferencedTypes())
            .map(PsiClassType::getName).collect(Collectors.toList());
        if (exceptionNameList.isEmpty()) {
            return "";
        }
        return exceptionNameList.stream()
            .map(name -> "@throws " + name + " " + translatorService.translate(name))
            .collect(Collectors.joining(System.lineSeparator()));
    }
}