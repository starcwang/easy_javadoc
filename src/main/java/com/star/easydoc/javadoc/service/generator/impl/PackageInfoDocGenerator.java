package com.star.easydoc.javadoc.service.generator.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.service.PackageInfoService;
import com.star.easydoc.service.translator.TranslatorService;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

public class PackageInfoDocGenerator implements DocGenerator {

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
            " * ${" + PackageInfoService.PACKAGE_INFO_DESCRIBE + "} \n" +
            "**/\n";
    }
}
