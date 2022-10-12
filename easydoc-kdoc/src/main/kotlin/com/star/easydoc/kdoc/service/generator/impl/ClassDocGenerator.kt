package com.star.easydoc.kdoc.service.generator.impl

import com.google.common.collect.Lists
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.javadoc.PsiDocTag
import com.star.easydoc.common.Consts
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang3.StringUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 类文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class ClassDocGenerator : DocGenerator {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(VariableGeneratorService::class.java)

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is PsiClass) {
            return StringUtils.EMPTY
        }
        return if (config.classTemplateConfig != null && java.lang.Boolean.TRUE == config.classTemplateConfig.isDefault
        ) {
            defaultGenerate(psiElement)
        } else {
            customGenerate(psiElement)
        }
    }

    /**
     * 默认的生成
     *
     * @param psiClass 当前类
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psiClass: PsiClass): String {
        val dateString: String
        dateString = try {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.dateFormat))
        } catch (e: Exception) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！")
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.DEFAULT_DATE_FORMAT))
        }
        // 有注释，进行兼容处理
        if (psiClass.docComment != null) {
            val elements: MutableList<PsiElement> = Lists.newArrayList(*psiClass.docComment!!.children)
            val startList: MutableList<String?> = Lists.newArrayList()
            val endList: MutableList<String?> = Lists.newArrayList()
            // 注释
            val desc = translatorService.translate(psiClass.name)
            startList.add(buildDesc(elements, desc))

            // 作者
            endList.add(buildAuthor(elements))

            // 日期
            endList.add(buildDate(elements))
            val commentItems: MutableList<String?> = Lists.newLinkedList()
            for (element in elements) {
                commentItems.add(element.text)
            }
            for (s in startList) {
                commentItems.add(1, s)
            }
            for (s in endList) {
                commentItems.add(commentItems.size - 1, s)
            }
            return commentItems.joinToString(StringUtils.EMPTY)
        }
        // 编译后会自动优化成StringBuilder
        return """
             /**
             * ${translatorService.translate(psiClass.name)}
             *
             * @author ${config.author}
             * @date $dateString
             */
             
             """.trimIndent()
    }

    /**
     * 构建描述
     *
     * @param elements 元素
     * @param desc 描述
     * @return [java.lang.String]
     */
    private fun buildDesc(elements: List<PsiElement>, desc: String): String? {
        for (element in elements) {
            if (!"PsiDocToken:DOC_COMMENT_DATA".equals(element.toString(), ignoreCase = true)) {
                continue
            }
            val source = element.text.replace("[/* \n]+".toRegex(), StringUtils.EMPTY)
            if (source == desc) {
                return null
            }
        }
        return desc
    }

    /**
     * 构建作者
     *
     * @param elements 元素
     * @return [java.lang.String]
     */
    private fun buildAuthor(elements: MutableList<PsiElement>): String? {
        var isInsert = true
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!"PsiDocTag:@author".equals(element.toString(), ignoreCase = true)) {
                continue
            }
            val value = (element as PsiDocTag).valueElement
            if (value == null || StringUtils.isBlank(value.text)) {
                iterator.remove()
            } else {
                isInsert = false
            }
        }
        return if (isInsert) {
            """
     @author ${config.author}
     
     """.trimIndent()
        } else {
            null
        }
    }

    /**
     * 构建日期
     *
     * @param elements 元素
     * @return [java.lang.String]
     */
    private fun buildDate(elements: MutableList<PsiElement>): String? {
        val dateString: String = try {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.dateFormat))
        } catch (e: Exception) {
            LOGGER.error("您输入的日期格式不正确，请到配置中修改类相关日期格式！")
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Consts.DEFAULT_DATE_FORMAT))
        }
        var isInsert = true
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!"PsiDocTag:@date".equals(element.toString(), ignoreCase = true)) {
                continue
            }
            val value = (element as PsiDocTag).valueElement
            if (value == null || StringUtils.isBlank(value.text)) {
                iterator.remove()
            } else {
                isInsert = false
            }
        }
        return if (isInsert) {
            "@date $dateString\n"
        } else {
            null
        }
    }

    /**
     * 自定义生成
     *
     * @param psiClass 当前类
     * @return [java.lang.String]
     */
    private fun customGenerate(psiClass: PsiClass): String {
        return variableGeneratorService.generate(psiClass)
    }

    companion object {
        private val LOGGER = Logger.getInstance(ClassDocGenerator::class.java)
    }
}