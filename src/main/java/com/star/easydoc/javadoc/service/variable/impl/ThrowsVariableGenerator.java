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
 * 异常变量生成器
 *
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @date 2025/07/28
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
                + translatorService.translateWithClass(type.getName(), type.getCanonicalText(), element.getProject(), element))
            .collect(Collectors.joining("\n"));
    }
}