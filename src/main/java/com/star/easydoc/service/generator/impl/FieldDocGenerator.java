package com.star.easydoc.service.generator.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/31
 */
public class FieldDocGenerator implements DocGenerator {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiField)) {
            return StringUtils.EMPTY;
        }
        PsiField psiField = (PsiField)psiElement;
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            return genSimpleDoc(psiField.getName());
        } else {
            return genNormalDoc(psiField.getName());
        }
    }

    private String genNormalDoc(String name) {
        return String.format("/**\n* %s\n */\n", translatorService.translate(name));
    }

    private String genSimpleDoc(String name) {
        return String.format("/** %s */", translatorService.translate(name));
    }

}
