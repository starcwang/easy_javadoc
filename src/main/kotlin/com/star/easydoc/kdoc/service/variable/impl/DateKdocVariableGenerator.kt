package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.star.easydoc.common.Consts
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:15:00
 */
class DateKdocVariableGenerator : AbstractKdocVariableGenerator() {
    override fun generate(element: PsiElement): String {
        return try {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.kdocDateFormat))
        } catch (e: Exception) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！", e)
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Consts.DEFAULT_DATE_FORMAT))
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(DateKdocVariableGenerator::class.java)
    }
}