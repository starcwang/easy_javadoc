package com.star.easydoc.service.generator.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 属性文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
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
        if (config != null && config.getFieldTemplateConfig() != null
                && Boolean.TRUE.equals(config.getFieldTemplateConfig().getIsDefault())) {
            return defaultGenerate(psiField);
        } else {
            return customGenerate(psiField);
        }

    }

    /**
     * 默认的生成
     *
     * @param psiField 当前属性
     * @return {@link java.lang.String}
     */
    private String defaultGenerate(PsiField psiField) {
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            return genSimpleDoc(psiField.getName());
        } else {
            return genNormalDoc(psiField.getName());
        }
    }

    /**
     * 自定义生成
     *
     * @param psiField 当前属性
     * @return {@link String}
     */
    private String customGenerate(PsiField psiField) {
        // TODO: 2019-11-12
        return null;
    }

    /**
     * 生成正常的文档
     *
     * @param name 的名字
     * @return {@link java.lang.String}
     */
    private String genNormalDoc(String name) {
        return String.format("/**\n* %s\n */\n", translatorService.translate(name));
    }

    /**
     * 生成简单的文档
     *
     * @param name 的名字
     * @return {@link java.lang.String}
     */
    private String genSimpleDoc(String name) {
        return String.format("/** %s */", translatorService.translate(name));
    }

}
