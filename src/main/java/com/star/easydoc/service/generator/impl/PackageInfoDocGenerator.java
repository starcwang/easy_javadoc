package com.star.easydoc.service.generator.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.action.PackageInfoHandle;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

public class PackageInfoDocGenerator implements DocGenerator {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiPackage)) {
            return StringUtils.EMPTY;
        }
        PsiPackage psiPackage = (PsiPackage)psiElement;

        return defaultGenerate(psiPackage);
    }

    private String defaultGenerate(PsiPackage psiPackage) {
        return "/**\n" +
            " * ${" + PackageInfoHandle.PACKAGE_INFO_DESCRIBE + "} \n" +
            "**/\n";
    }
}
