package com.star.easydoc.javadoc.service.generator.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import com.star.easydoc.service.PackageInfoService;
import org.apache.commons.lang3.StringUtils;

/**
 * 包信息文档生成器
 *
 * @author mixley
 * @date 2022/07/12
 */
public class PackageInfoDocGenerator implements DocGenerator {

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiPackage)) {
            return StringUtils.EMPTY;
        }
        PsiPackage psiPackage = (PsiPackage)psiElement;

        return defaultGenerate(psiPackage);
    }

    /**
     * 默认生成
     *
     * @param psiPackage PSI 封装
     * @return {@link String }
     */
    private String defaultGenerate(PsiPackage psiPackage) {
        return "/**\n" +
            " * ${" + PackageInfoService.PACKAGE_INFO_DESCRIBE + "} \n" +
            "**/\n";
    }
}
