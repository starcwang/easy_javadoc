package com.star.easydoc.javadoc.service.variable.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.star.easydoc.common.Consts;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:15:00
 */
public class DateVariableGenerator extends AbstractVariableGenerator {
    private static final Logger LOGGER = Logger.getInstance(DateVariableGenerator.class);

    @Override
    public String generate(PsiElement element) {
        try {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(getConfig().getDateFormat()));
        } catch (Exception e) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！", e);
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(Consts.DEFAULT_DATE_FORMAT));
        }
    }

}