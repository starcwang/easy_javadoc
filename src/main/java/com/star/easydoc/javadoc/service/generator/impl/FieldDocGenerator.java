package com.star.easydoc.javadoc.service.generator.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import com.star.easydoc.service.translator.TranslatorService;
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
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(JavadocVariableGeneratorService.class);

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
            return genNormalDoc(psiField, psiField.getName());
        }
    }

    /**
     * 自定义生成
     *
     * @param psiField 当前属性
     * @return {@link String}
     */
    private String customGenerate(PsiField psiField) {
        return javadocVariableGeneratorService.generate(psiField, config.getFieldTemplateConfig().getTemplate(),
            config.getFieldTemplateConfig().getCustomMap(), getFieldInnerVariable(psiField));
    }

    /**
     * 生成正常的文档
     *
     * @param psiField 属性
     * @param name 名字
     * @return {@link java.lang.String}
     */
    private String genNormalDoc(PsiField psiField, String name) {
        PsiDocComment comment = psiField.getDocComment();
        if (comment != null) {
            List<PsiElement> elements = Lists.newArrayList(comment.getChildren());

            // 注释
            String desc = translatorService.translate(name);
            List<String> commentItems = Lists.newLinkedList();
            for (PsiElement element : elements) {
                commentItems.add(element.getText());
            }
            commentItems.add(1, buildDesc(elements, desc));
            return Joiner.on(StringUtils.EMPTY).skipNulls().join(commentItems);
        }
        return String.format("/**%s* %s%s */", "\n", translatorService.translate(name), "\n");
    }

    /**
     * 构建描述
     *
     * @param elements 元素
     * @param desc 描述
     * @return {@link java.lang.String}
     */
    private String buildDesc(List<PsiElement> elements, String desc) {
        for (PsiElement element : elements) {
            if (!"PsiDocToken:DOC_COMMENT_DATA".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String source = element.getText().replaceAll("[/* \n]+", StringUtils.EMPTY);
            if (Objects.equals(source, desc)) {
                return null;
            }
        }
        return desc;
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

    /**
     * 获取字段内部的变量
     *
     * @param psiField psi属性
     * @return {@link java.util.Map<java.lang.String,java.lang.Object>}
     */
    private Map<String, Object> getFieldInnerVariable(PsiField psiField) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", config.getAuthor());
        map.put("fieldName", psiField.getName());
        map.put("fieldType", psiField.getType().getCanonicalText());
        map.put("branch", VcsUtil.getCurrentBranch(psiField.getProject()));
        return map;
    }
}
