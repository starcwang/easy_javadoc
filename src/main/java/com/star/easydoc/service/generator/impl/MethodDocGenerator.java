package com.star.easydoc.service.generator.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class MethodDocGenerator implements DocGenerator {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    private static final Set<String> BASE_TYPE_SET = Sets.newHashSet("byte", "short", "int", "long", "char", "float",
            "double", "boolean");

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;

        if (config != null && config.getMethodTemplateConfig() != null
                && Boolean.TRUE.equals(config.getMethodTemplateConfig().getIsDefault())) {
            return defaultGenerate(psiMethod);
        } else {
            return customGenerate(psiMethod);
        }
    }

    /**
     * 默认的生成
     *
     * @param psiMethod 当前方法
     * @return {@link java.lang.String}
     */
    private String defaultGenerate(PsiMethod psiMethod) {
        List<String> paramNameList = Arrays.stream(psiMethod.getParameters())
                .map(JvmParameter::getName).collect(Collectors.toList());
        String returnName = psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getCanonicalText();

        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append("* ").append(translatorService.translate(psiMethod.getName())).append("\n");
        sb.append("*\n");
        for (String paramName : paramNameList) {
            sb.append("* @param ").append(paramName).append(" ").append(translatorService.translate(paramName))
                    .append("\n");
        }
        if (returnName.length() > 0 && !"void".equals(returnName)) {
            if (BASE_TYPE_SET.contains(returnName)) {
                sb.append("* @return ").append(returnName);
            } else {
                sb.append("* @return {@link ").append(returnName).append("}");
            }
        }
        sb.append("*/\n");
        return sb.toString();
    }

    /**
     * 自定义生成
     *
     * @param psiMethod 当前方法
     * @return {@link java.lang.String}
     */
    private String customGenerate(PsiMethod psiMethod) {
        // TODO: 2019-11-12
        return null;
    }
}
