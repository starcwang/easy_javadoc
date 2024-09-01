package com.star.easydoc.javadoc.service.variable.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.service.translator.TranslatorService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
public class DocVariableGenerator extends AbstractVariableGenerator {
    private final TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiNamedElement)) {
            return "";
        }
        PsiDocComment docComment = ((PsiJavaDocumentedElement)element).getDocComment();

        // force模式
        if (docComment == null || EasyDocConfig.COVER_MODE_FORCE.equals(getConfig().getCoverMode())) {
            return translatorService.translate(((PsiNamedElement)element).getName());
        }

        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        List<String> descTextList = Arrays.stream(descriptionElements).map(PsiElement::getText)
            .map(StringUtils::strip)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        String result = Joiner.on("\n* ").skipNulls().join(descTextList);
        return StringUtils.isNotBlank(result) ? result : translatorService.translate(((PsiNamedElement)element).getName());
    }
}
