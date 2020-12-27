package com.star.easydoc.service.generator.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.star.easydoc.config.Consts;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.VariableGeneratorService;
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
    private VariableGeneratorService variableGeneratorService = ServiceManager.getService(VariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiMethod psiMethod = (PsiMethod) psiElement;

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
        List<String> paramNameList = Arrays.stream(psiMethod.getParameterList().getParameters())
            .map(PsiParameter::getName).collect(Collectors.toList());
        String returnName = psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getPresentableText();
        List<String> exceptionNameList = Arrays.stream(psiMethod.getThrowsList().getReferencedTypes())
            .map(PsiClassType::getName).collect(Collectors.toList());

        // 有注释，进行兼容处理
        if (psiMethod.getDocComment() != null) {
            List<PsiElement> elements = Lists.newArrayList(psiMethod.getDocComment().getChildren());

            List<String> startList = Lists.newArrayList();
            List<String> endList = Lists.newArrayList();
            // 注释
            String desc = translatorService.translate(psiMethod.getName());
            startList.add(buildDesc(elements, desc));

            // 参数
            endList.addAll(buildParams(elements, paramNameList));

            // 返回
            endList.add(buildReturn(elements, returnName));

            // 异常
            endList.addAll(buildException(elements, exceptionNameList));

            List<String> commentItems = Lists.newLinkedList();
            for (PsiElement element : elements) {
                commentItems.add(element.getText());
            }
            for (String s : startList) {
                commentItems.add(1, s);
            }
            for (String s : endList) {
                commentItems.add(commentItems.size() - 1, s);
            }
            return Joiner.on(StringUtils.EMPTY).skipNulls().join(commentItems);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append("* ").append(translatorService.translate(psiMethod.getName())).append("\n");
        sb.append("*\n");
        for (String paramName : paramNameList) {
            sb.append("* @param ").append(paramName).append(" ").append(translatorService.translate(paramName)).append("\n");
        }
        if (returnName.length() > 0 && !"void".equals(returnName)) {
            if (Consts.BASE_TYPE_SET.contains(returnName)) {
                sb.append("* @return ").append(returnName).append("\n");
            } else {
                sb.append("* @return {@link ").append(returnName).append("}").append("\n");
            }
        }
        for (String exceptionName : exceptionNameList) {
            sb.append("* @throws ").append(exceptionName).append(" ")
                .append(translatorService.translate(exceptionName)).append("\n");
        }
        sb.append("*/\n");
        return sb.toString();
    }

    /**
     * 构建异常
     *
     * @param elements          元素
     * @param exceptionNameList 异常名称数组
     * @return {@link java.util.List<java.lang.String>}
     */
    private List<String> buildException(List<PsiElement> elements, List<String> exceptionNameList) {
        List<String> paramDocList = Lists.newArrayList();
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@throws".equalsIgnoreCase(element.toString())
                    && !"PsiDocTag:@exception".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String exceptionName = null;
            String exceptionData = null;
            for (PsiElement child : element.getChildren()) {
                if (StringUtils.isBlank(exceptionName) && "PsiElement(DOC_TAG_VALUE_ELEMENT)".equals(child.toString())) {
                    exceptionName = StringUtils.trim(child.getText());
                } else if (StringUtils.isBlank(exceptionData) && "PsiDocToken:DOC_COMMENT_DATA".equals(child.toString())) {
                    exceptionData = StringUtils.trim(child.getText());
                }
            }
            if (StringUtils.isBlank(exceptionName) || StringUtils.isBlank(exceptionData)) {
                iterator.remove();
                continue;
            }
            if (!exceptionNameList.contains(exceptionName)) {
                iterator.remove();
                continue;
            }
            exceptionNameList.remove(exceptionName);
        }
        for (String exceptionName : exceptionNameList) {
            paramDocList.add("@throws " + exceptionName + " " + translatorService.translate(exceptionName) + "\n");
        }
        return paramDocList;
    }

    /**
     * 构建返回
     *
     * @param elements   元素
     * @param returnName 返回名称
     * @return {@link java.lang.String}
     */
    private String buildReturn(List<PsiElement> elements, String returnName) {
        boolean isInsert = true;
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@return".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag) element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else if (returnName.length() <= 0 || "void".equals(returnName)) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert && returnName.length() > 0 && !"void".equals(returnName)) {
            if (Consts.BASE_TYPE_SET.contains(returnName)) {
                return "@return " + returnName + "\n";
            } else {
                return "@return {@link " + returnName + "}\n";
            }
        }
        return null;
    }

    /**
     * 构建参数
     *
     * @param elements      元素
     * @param paramNameList 参数名称数组
     * @return {@link java.util.List<java.lang.String>}
     */
    private List<String> buildParams(List<PsiElement> elements, List<String> paramNameList) {
        List<String> paramDocList = Lists.newArrayList();
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@param".equalsIgnoreCase(element.toString())) {
                continue;
            }
            String paramName = null;
            String paramData = null;
            for (PsiElement child : element.getChildren()) {
                if (StringUtils.isBlank(paramName) && "PsiElement(DOC_PARAMETER_REF)".equals(child.toString())) {
                    paramName = StringUtils.trim(child.getText());
                } else if (StringUtils.isBlank(paramData) && "PsiDocToken:DOC_COMMENT_DATA".equals(child.toString())) {
                    paramData = StringUtils.trim(child.getText());
                }
            }
            if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramData)) {
                iterator.remove();
                continue;
            }
            if (!paramNameList.contains(paramName)) {
                iterator.remove();
                continue;
            }
            paramNameList.remove(paramName);
        }
        for (String paramName : paramNameList) {
            paramDocList.add("@param " + paramName + " " + translatorService.translate(paramName) + "\n");
        }
        return paramDocList;
    }

    /**
     * 构建描述
     *
     * @param elements 元素
     * @param desc     描述
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
     * 自定义生成
     *
     * @param psiMethod 当前方法
     * @return {@link java.lang.String}
     */
    private String customGenerate(PsiMethod psiMethod) {
        return variableGeneratorService.generate(psiMethod);
    }
}
