package com.star.easydoc.kdoc.service.generator.impl

import com.google.common.collect.Lists
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils

/**
 * 属性文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class FieldDocGenerator : DocGenerator {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(VariableGeneratorService::class.java)
    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is PsiField) {
            return StringUtils.EMPTY
        }
        val psiField = psiElement
        return if (config.fieldTemplateConfig != null && java.lang.Boolean.TRUE == config.fieldTemplateConfig.isDefault
        ) {
            defaultGenerate(psiField)
        } else {
            customGenerate(psiField)
        }
    }

    /**
     * 默认的生成
     *
     * @param psiField 当前属性
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psiField: PsiField): String {
        return if (BooleanUtils.isTrue(config.simpleFieldDoc)) {
            genSimpleDoc(psiField.name)
        } else {
            genNormalDoc(psiField, psiField.name)
        }
    }

    /**
     * 自定义生成
     *
     * @param psiField 当前属性
     * @return [String]
     */
    private fun customGenerate(psiField: PsiField): String {
        return variableGeneratorService.generate(psiField)
    }

    /**
     * 生成正常的文档
     *
     * @param psiField 属性
     * @param name 名字
     * @return [java.lang.String]
     */
    private fun genNormalDoc(psiField: PsiField, name: String): String {
        val comment = psiField.docComment
        if (comment != null) {
            val elements: List<PsiElement> = Lists.newArrayList(*comment.children)

            // 注释
            val desc = translatorService.translate(name)
            val commentItems: MutableList<String?> = Lists.newLinkedList()
            for (element in elements) {
                commentItems.add(element.text)
            }
            commentItems.add(1, buildDesc(elements, desc))
            return commentItems.joinToString(StringUtils.EMPTY)
        }
        return String.format(
            "/**%s* %s%s */%s", "\n", translatorService.translate(name), "\n",
            "\n"
        )
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
     * 生成简单的文档
     *
     * @param name 的名字
     * @return [java.lang.String]
     */
    private fun genSimpleDoc(name: String): String {
        return String.format("/** %s */", translatorService.translate(name))
    }
}