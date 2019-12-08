package com.star.easydoc.service.generator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.VariableGeneratorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

/**
 * 类文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class ClassDocGenerator implements DocGenerator {
    private static final Logger LOGGER = Logger.getInstance(ClassDocGenerator.class);

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();
    private VariableGeneratorService variableGeneratorService = ServiceManager.getService(VariableGeneratorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiClass)) {
            return StringUtils.EMPTY;
        }
        PsiClass psiClass = (PsiClass)psiElement;
        if (config != null && config.getClassTemplateConfig() != null
            && Boolean.TRUE.equals(config.getClassTemplateConfig().getIsDefault())) {
            return defaultGenerate(psiClass);
        } else {
            return customGenerate(psiClass);
        }

    }

    /**
     * 默认的生成
     *
     * @param psiClass 当前类
     * @return {@link java.lang.String}
     */
    private String defaultGenerate(PsiClass psiClass) {
        String dateString;
        try {
            dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.getDateFormat()));
        } catch (Exception e) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！");
            dateString = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(EasyJavadocConfigComponent.DEFAULT_DATE_FORMAT));
        }
        // 有注释，进行兼容处理
        if (psiClass.getDocComment() != null) {
            List<PsiElement> elements = Lists.newArrayList(psiClass.getDocComment().getChildren());

            List<String> startList = Lists.newArrayList();
            List<String> endList = Lists.newArrayList();
            // 注释
            String desc = translatorService.translate(psiClass.getName());
            startList.add(buildDesc(elements, desc));

            // 作者
            endList.add(buildAuthor(elements));

            // 日期
            endList.add(buildDate(elements));

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
        // 编译后会自动优化成StringBuilder
        return "/**" + System.lineSeparator()
            + "* " + translatorService.translate(psiClass.getName()) + System.lineSeparator()
            + "*" + System.lineSeparator()
            + "* @author " + config.getAuthor() + System.lineSeparator()
            + "* @date " + dateString + System.lineSeparator()
            + "*/" + System.lineSeparator();
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
     * 构建作者
     *
     * @param elements 元素
     * @return {@link java.lang.String}
     */
    private String buildAuthor(List<PsiElement> elements) {
        boolean isInsert = true;
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@author".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag)element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert) {
            return "@author " + config.getAuthor() + System.lineSeparator();
        } else {
            return null;
        }
    }

    /**
     * 构建日期
     *
     * @param elements 元素
     * @return {@link java.lang.String}
     */
    private String buildDate(List<PsiElement> elements) {
        String dateString;
        try {
            dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.getDateFormat()));
        } catch (Exception e) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！");
            dateString = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(EasyJavadocConfigComponent.DEFAULT_DATE_FORMAT));
        }
        boolean isInsert = true;
        for (Iterator<PsiElement> iterator = elements.iterator(); iterator.hasNext(); ) {
            PsiElement element = iterator.next();
            if (!"PsiDocTag:@date".equalsIgnoreCase(element.toString())) {
                continue;
            }
            PsiDocTagValue value = ((PsiDocTag)element).getValueElement();
            if (value == null || StringUtils.isBlank(value.getText())) {
                iterator.remove();
            } else {
                isInsert = false;
            }
        }
        if (isInsert) {
            return "@date " + dateString + System.lineSeparator();
        } else {
            return null;
        }
    }

    /**
     * 自定义生成
     *
     * @param psiClass 当前类
     * @return {@link java.lang.String}
     */
    private String customGenerate(PsiClass psiClass) {
        return variableGeneratorService.generate(psiClass);
    }
}
