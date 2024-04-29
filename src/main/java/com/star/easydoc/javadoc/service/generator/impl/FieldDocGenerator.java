package com.star.easydoc.javadoc.service.generator.impl;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ResourceUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import com.star.easydoc.service.gpt.GptService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 属性文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class FieldDocGenerator extends AbstractDocGenerator {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private GptService gptService = ServiceManager.getService(GptService.class);
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(
        JavadocVariableGeneratorService.class);

    private static final String SIMPLE_TEMPLATE = "/** $DOC$ */";
    private static final String DOC_TEMPLATE = "/**\n"
        + " * $DOC$\n"
        + " */";

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiField)) {
            return StringUtils.EMPTY;
        }
        PsiField psiField = (PsiField)psiElement;
        PsiDocComment docComment = psiField.getDocComment();
        if (EasyDocConfig.COVER_MODE_IGNORE.equals(config.getCoverMode()) && docComment != null) {
            return null;
        }

        // AI
        if (Consts.AI_TRANSLATOR.contains(config.getTranslator())) {
            return generateWithAI(psiField);
        }
        
        String template;
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            template = SIMPLE_TEMPLATE;
        } else {
            template = DOC_TEMPLATE;
        }
        if (config.getFieldTemplateConfig() != null
            && Boolean.FALSE.equals(config.getFieldTemplateConfig().getIsDefault())) {
            template = config.getFieldTemplateConfig().getTemplate();
        }
        String targetJavadoc = javadocVariableGeneratorService.generate(psiField, template,
            config.getFieldTemplateConfig().getCustomMap(), getFieldInnerVariable(psiField));
        return merge(psiField, targetJavadoc);
    }

    private String generateWithAI(PsiElement psiElement) {
        String prompt;
        try {
            prompt = IOUtils.toString(ResourceUtil.getResource(getClass(), "prompts/chatglm", "field.prompt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        prompt = prompt.replace("{code}", psiElement.getText());
        return gptService.chat(prompt);
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

    @Override
    protected EasyDocConfig getConfig() {
        return config;
    }
}
