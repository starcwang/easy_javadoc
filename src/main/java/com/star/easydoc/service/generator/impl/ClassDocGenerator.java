package com.star.easydoc.service.generator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.tree.IElementType;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
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
            dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(EasyJavadocConfigComponent.DEFAULT_DATE_FORMAT));
        }

        // 有注释，进行兼容处理
        if (psiClass.getDocComment() != null) {
            PsiDocComment comment = psiClass.getDocComment();
            PsiElement[] elements = comment.getDescriptionElements();
            for (PsiElement element : elements) {
                if (!(element instanceof PsiDocToken)
                        || element.toString().endsWith("DOC_COMMENT_DATA")) {
                    continue;
                }
                PsiDocTokenImpl psiDocToken = new PsiDocTokenImpl(new IElementType("DOC_COMMENT_DATA", JavaLanguage.INSTANCE), "这是我自己的注释");
                comment.addAfter(element, psiDocToken);
                break;
            }
            return comment.getText();
        } else {
            // 编译后会自动优化成StringBuilder
            return "/**\n"
                    + "* " + translatorService.translate(psiClass.getName()) + "\n"
                    + "*\n"
                    + "* @author " + config.getAuthor() + "\n"
                    + "* @date " + dateString + "\n"
                    + "*/\n";
        }
    }

    /**
     * 自定义生成
     *
     * @param psiClass 当前类
     * @return {@link java.lang.String}
     */
    private String customGenerate(PsiClass psiClass) {
        // TODO: 2019-11-12
        return null;
    }
}
