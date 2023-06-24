package com.star.easydoc.javadoc.service.generator.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import com.star.easydoc.service.PackageInfoService;
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
