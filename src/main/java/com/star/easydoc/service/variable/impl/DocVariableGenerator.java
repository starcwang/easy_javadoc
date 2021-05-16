package com.star.easydoc.service.variable.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.variable.VariableGenerator;
import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
public class DocVariableGenerator implements VariableGenerator {
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (element instanceof PsiNamedElement) {
            PsiDocComment docComment = ((PsiJavaDocumentedElement)element).getDocComment();
            if (docComment != null) {
                PsiElement[] descriptionElements = docComment.getDescriptionElements();
                List<String> descTextList = Arrays.stream(descriptionElements).map(PsiElement::getText).collect(Collectors.toList());
                String result = Joiner.on(StringUtils.EMPTY).skipNulls().join(descTextList);
                return StringUtils.isNotBlank(result) ? result : translatorService.translate(((PsiNamedElement)element).getName());
            }
            return translatorService.translate(((PsiNamedElement)element).getName());
        }
        return "";
    }
}
