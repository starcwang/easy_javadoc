package com.star.easydoc.javadoc.service.generator.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import com.star.easydoc.service.translator.TranslatorService;
import org.apache.commons.lang3.StringUtils;

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class MethodDocGenerator implements DocGenerator {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(
        JavadocVariableGeneratorService.class);

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
        List<String> paramNameList = Arrays.stream(psiMethod.getParameterList().getParameters())
            .map(PsiParameter::getName).collect(Collectors.toList());
        PsiTypeElement returns = psiMethod.getReturnTypeElement() == null ? null : psiMethod.getReturnTypeElement();
        String returnName = returns == null ? "" : returns.getText();
        List<PsiClassType> exceptionTypeList = Arrays.stream(psiMethod.getThrowsList().getReferencedTypes())
            .collect(Collectors.toList());

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
            endList.add(buildReturn(elements, returns));

            // 异常
            endList.addAll(buildException(elements, exceptionTypeList, psiMethod.getProject()));

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
                if (config.isCodeMethodReturnType()) {
                    sb.append("* @return {@code ").append(returnName).append("}").append("\n");
                } else if (config.isLinkMethodReturnType()) {
                    sb.append(getLinkTypeReturnDoc(returnName));
                } else if (config.isDocMethodReturnType()) {
                    sb.append("* @return " +
                        translatorService.translateWithClass(returnName, returns.getType().getCanonicalText(), psiMethod.getProject()) + "\n");
                }
            }
        }
        for (PsiClassType exceptionType : exceptionTypeList) {
            sb.append("* @throws ").append(exceptionType.getName()).append(" ")
                .append(translatorService.translateWithClass(
                    exceptionType.getName(), exceptionType.getCanonicalText(), psiMethod.getProject())).append("\n");
        }
        sb.append("*/");
        return sb.toString();
    }

    /**
     * 构建异常
     *
     * @param elements 元素
     * @param exceptionTypeList 异常类型数组
     * @return {@link java.util.List<java.lang.String>}
     */
    private List<String> buildException(List<PsiElement> elements, List<PsiClassType> exceptionTypeList, Project project) {
        List<String> paramDocList = Lists.newArrayList();
        Set<String> exceptionNameSet = exceptionTypeList.stream().map(PsiClassType::getName).collect(Collectors.toSet());
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
            if (!exceptionNameSet.contains(exceptionName)) {
                iterator.remove();
                continue;
            }
            exceptionNameSet.remove(exceptionName);
            for (Iterator<PsiClassType> iter = exceptionTypeList.iterator(); iter.hasNext(); ) {
                PsiClassType psiClassType = iter.next();
                if (psiClassType.getName().equals(exceptionName)) {
                    iter.remove();
                }
            }
        }
        for (PsiClassType exceptionType : exceptionTypeList) {
            paramDocList.add("@throws " + exceptionType.getName()
                + " " + translatorService.translateWithClass(
                    exceptionType.getName(), exceptionType.getCanonicalText(), project) + "\n");
        }
        return paramDocList;
    }

    /**
     * 构建返回
     *
     * @param elements 元素
     * @param returns 返回名称
     * @return {@link java.lang.String}
     */
    private String buildReturn(List<PsiElement> elements, PsiTypeElement returns) {
        boolean isInsert = true;
        if (returns == null) {
            return "";
        }
        String returnName = returns.getText();
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@return".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag)element).getValueElement();
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
                if (config.isCodeMethodReturnType()) {
                    return "@return {@code " + returnName + "}\n";
                } else if (config.isLinkMethodReturnType()) {
                    return getLinkTypeReturnDoc(returnName);
                } else if (config.isDocMethodReturnType()) {
                    return "* @return " +
                        translatorService.translateWithClass(returnName, returns.getType().getCanonicalText(),
                            returns.getProject()) + "\n";
                }
            }
        }
        return null;
    }

    /**
     * 获取link类型文档注释
     *
     * @param returnName 返回名
     * @return {@link String}
     */
    private String getLinkTypeReturnDoc(String returnName) {
        return "* @return " + returnName.replaceAll("[^<> ,]+", "{@link $0}") + "\n";
    }

    /**
     * 构建参数
     *
     * @param elements 元素
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
     * 自定义生成
     *
     * @param psiMethod 当前方法
     * @return {@link java.lang.String}
     */
    private String customGenerate(PsiMethod psiMethod) {
        return javadocVariableGeneratorService.generate(psiMethod, config.getMethodTemplateConfig().getTemplate(),
            config.getMethodTemplateConfig().getCustomMap(), getMethodInnerVariable(psiMethod));
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
        map.put("methodReturnType", psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getCanonicalText());
        map.put("methodParamTypes",
            Arrays.stream(psiMethod.getTypeParameters()).map(PsiTypeParameter::getQualifiedName).toArray(String[]::new));
        map.put("methodParamNames",
            Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameter::getName).toArray(String[]::new));
        map.put("branch", VcsUtil.getCurrentBranch(psiMethod.getProject()));
        return map;
    }
}
