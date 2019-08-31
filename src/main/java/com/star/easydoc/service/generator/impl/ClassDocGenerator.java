package com.star.easydoc.service.generator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/31
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

        String dateString;
        try {
            dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.getDateFormat()));
        } catch (Exception e) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！");
            dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(EasyJavadocConfigComponent.DEFAULT_DATE_FORMAT));
        }
        // 编译后会自动优化成StringBuilder
        String sb = "/**\n"
            + "* " + translatorService.translate(psiClass.getName()) + "\n"
            + "*\n"
            + "* @author " + config.getAuthor() + "\n"
            + "* @date " + dateString + "\n"
            + "*/\n";
        return sb;
    }
}
