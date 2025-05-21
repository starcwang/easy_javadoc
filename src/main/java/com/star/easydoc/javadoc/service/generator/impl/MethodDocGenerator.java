package com.star.easydoc.javadoc.service.generator.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ResourceUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import com.star.easydoc.service.gpt.GptService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class MethodDocGenerator extends AbstractDocGenerator {

    /** 配置 */
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private GptService gptService = ServiceManager.getService(GptService.class);
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(
        JavadocVariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;
        PsiDocComment docComment = psiMethod.getDocComment();
        if (EasyDocConfig.COVER_MODE_IGNORE.equals(config.getCoverMode()) && docComment != null) {
            return null;
        }

        // AI
        if (Consts.AI_TRANSLATOR.contains(config.getTranslator())) {
            return generateWithAI(psiElement);
        }

        String template = getDefaultTemplate(psiMethod);
        if (config.getMethodTemplateConfig() != null
            && Boolean.FALSE.equals(config.getMethodTemplateConfig().getIsDefault())) {
            template = config.getMethodTemplateConfig().getTemplate();
        }

        String targetJavadoc = javadocVariableGeneratorService.generate(psiMethod, template,
            config.getMethodTemplateConfig().getCustomMap(), getMethodInnerVariable(psiMethod));
        return merge(psiMethod, targetJavadoc);
    }

    /**
     * 获取默认模板
     *
     * @param psiMethod PSI 方法
     * @return {@link String }
     */
    private String getDefaultTemplate(PsiMethod psiMethod) {
        String paramsKey = " * $PARAMS$\n";
        if (psiMethod.getParameterList().getParameters().length == 0) {
            paramsKey = "";
        }
        String returnKey = " * $RETURN$\n";
        if (psiMethod.getReturnType() == null || psiMethod.getReturnType().getCanonicalText().equals("void")) {
            returnKey = "";
        }
        String throwsKey = " * $THROWS$\n";
        if (psiMethod.getThrowsList().getReferencedTypes().length == 0) {
            throwsKey = "";
        }
        return "/**\n"
            + " * $DOC$\n"
            + " *\n"
            + paramsKey
            + returnKey
            + throwsKey
            + " */";
    }

    /**
     * 使用 AI 生成
     *
     * @param psiElement PSI 元件
     * @return {@link String }
     */
    private String generateWithAI(PsiElement psiElement) {
        String prompt;
        try {
            prompt = IOUtils.toString(ResourceUtil.getResource(getClass(), "prompts/chatglm", "method.prompt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        prompt = prompt.replace("{code}", psiElement.getText());
        return gptService.chat(prompt);
    }

    /**
     * 获取方法内部的变量
     *
     * @param psiMethod psi方法
     * @return {@link java.util.Map<java.lang.String,java.lang.Object>}
     */
    private Map<String, Object> getMethodInnerVariable(PsiMethod psiMethod) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", config.getAuthor());
        map.put("methodName", psiMethod.getName());
        map.put("methodReturnType",
            psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getCanonicalText());
        map.put("methodParamTypes",
            Arrays.stream(psiMethod.getTypeParameters()).map(PsiTypeParameter::getQualifiedName)
                .toArray(String[]::new));
        map.put("methodParamNames",
            Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameter::getName)
                .toArray(String[]::new));
        map.put("branch", VcsUtil.getCurrentBranch(psiMethod.getProject()));
        map.put("projectName", psiMethod.getProject().getName());
        return map;
    }

    @Override
    protected EasyDocConfig getConfig() {
        return config;
    }
}
