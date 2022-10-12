package com.star.easydoc.kdoc.service.generator.impl

import com.google.common.collect.Lists
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.javadoc.PsiDocTag
import com.star.easydoc.common.Consts
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang3.StringUtils

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class MethodDocGenerator : DocGenerator {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(
        VariableGeneratorService::class.java
    )

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is PsiMethod) {
            return StringUtils.EMPTY
        }
        val psiMethod = psiElement
        return if (config.methodTemplateConfig != null && java.lang.Boolean.TRUE == config.methodTemplateConfig.isDefault
        ) {
            defaultGenerate(psiMethod)
        } else {
            customGenerate(psiMethod)
        }
    }

    /**
     * 默认的生成
     *
     * @param psiMethod 当前方法
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psiMethod: PsiMethod): String {
        val paramNameList = psiMethod.parameterList.parameters
            .map { obj: PsiParameter -> obj.name!! }.toCollection(ArrayList())
        val returnName = if (psiMethod.returnTypeElement == null) "" else psiMethod.returnTypeElement!!.text
        val exceptionNameList = psiMethod.throwsList.referencedTypes
            .map { obj: PsiClassType -> obj.name }.toCollection(ArrayList())

        // 有注释，进行兼容处理
        if (psiMethod.docComment != null) {
            val elements: MutableList<PsiElement> = Lists.newArrayList(*psiMethod.docComment!!.children)
            val startList: MutableList<String?> = Lists.newArrayList()
            val endList: MutableList<String?> = Lists.newArrayList()
            // 注释
            val desc = translatorService.translate(psiMethod.name)
            startList.add(buildDesc(elements, desc))

            // 参数
            endList.addAll(buildParams(elements, paramNameList))

            // 返回
            endList.add(buildReturn(elements, returnName))

            // 异常
            endList.addAll(buildException(elements, exceptionNameList))
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
        val sb = StringBuilder()
        sb.append("/**\n")
        sb.append("* ").append(translatorService.translate(psiMethod.name)).append("\n")
        sb.append("*\n")
        for (paramName in paramNameList) {
            sb.append("* @param ").append(paramName).append(" ").append(translatorService.translate(paramName)).append("\n")
        }
        if (returnName.isNotEmpty() && "void" != returnName) {
            if (Consts.BASE_TYPE_SET.contains(returnName)) {
                sb.append("* @return ").append(returnName).append("\n")
            } else {
                if (config.isCodeMethodReturnType) {
                    sb.append("* @return {@code ").append(returnName).append("}").append("\n")
                } else if (config.isLinkMethodReturnType) {
                    sb.append(getLinkTypeReturnDoc(returnName))
                }
            }
        }
        for (exceptionName in exceptionNameList) {
            sb.append("* @throws ").append(exceptionName).append(" ")
                .append(translatorService.translate(exceptionName)).append("\n")
        }
        sb.append("*/\n")
        return sb.toString()
    }

    /**
     * 构建异常
     *
     * @param elements 元素
     * @param exceptionNameList 异常名称数组
     * @return [<]
     */
    private fun buildException(elements: MutableList<PsiElement>, exceptionNameList: ArrayList<String>): List<String> {
        val paramDocList: MutableList<String> = Lists.newArrayList()
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!"PsiDocTag:@throws".equals(element.toString(), ignoreCase = true)
                && !"PsiDocTag:@exception".equals(element.toString(), ignoreCase = true)
            ) {
                continue
            }
            var exceptionName: String? = null
            var exceptionData: String? = null
            for (child in element.children) {
                if (StringUtils.isBlank(exceptionName) && "PsiElement(DOC_TAG_VALUE_ELEMENT)" == child.toString()) {
                    exceptionName = StringUtils.trim(child.text)
                } else if (StringUtils.isBlank(exceptionData) && "PsiDocToken:DOC_COMMENT_DATA" == child.toString()) {
                    exceptionData = StringUtils.trim(child.text)
                }
            }
            if (StringUtils.isBlank(exceptionName) || StringUtils.isBlank(exceptionData)) {
                iterator.remove()
                continue
            }
            if (!exceptionNameList.contains(exceptionName)) {
                iterator.remove()
                continue
            }
            exceptionNameList.remove(exceptionName)
        }
        for (exceptionName in exceptionNameList) {
            paramDocList.add(
                """@throws $exceptionName ${translatorService.translate(exceptionName)}
"""
            )
        }
        return paramDocList
    }

    /**
     * 构建返回
     *
     * @param elements 元素
     * @param returnName 返回名称
     * @return [java.lang.String]
     */
    private fun buildReturn(elements: MutableList<PsiElement>, returnName: String): String? {
        var isInsert = true
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!"PsiDocTag:@return".equals(element.toString(), ignoreCase = true)) {
                continue
            }
            val value = (element as PsiDocTag).valueElement
            if (value == null || StringUtils.isBlank(value.text)) {
                iterator.remove()
            } else if (returnName.length <= 0 || "void" == returnName) {
                iterator.remove()
            } else {
                isInsert = false
            }
        }
        if (isInsert && returnName.length > 0 && "void" != returnName) {
            if (Consts.BASE_TYPE_SET.contains(returnName)) {
                return "@return $returnName\n"
            } else {
                if (config.isCodeMethodReturnType) {
                    return "@return {@code $returnName}\n"
                } else if (config.isLinkMethodReturnType) {
                    return getLinkTypeReturnDoc(returnName)
                }
            }
        }
        return null
    }

    /**
     * 获取link类型文档注释
     *
     * @param returnName 返回名
     * @return [String]
     */
    private fun getLinkTypeReturnDoc(returnName: String): String {
        return """
             * @return ${returnName.replace("[^<> ,]+".toRegex(), "{@link $0}")}
             
             """.trimIndent()
    }

    /**
     * 构建参数
     *
     * @param elements 元素
     * @param paramNameList 参数名称数组
     * @return [<]
     */
    private fun buildParams(elements: MutableList<PsiElement>, paramNameList: ArrayList<String>): List<String> {
        val paramDocList: MutableList<String> = Lists.newArrayList()
        val iterator = elements.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!"PsiDocTag:@param".equals(element.toString(), ignoreCase = true)) {
                continue
            }
            var paramName: String? = null
            var paramData: String? = null
            for (child in element.children) {
                if (StringUtils.isBlank(paramName) && "PsiElement(DOC_PARAMETER_REF)" == child.toString()) {
                    paramName = StringUtils.trim(child.text)
                } else if (StringUtils.isBlank(paramData) && "PsiDocToken:DOC_COMMENT_DATA" == child.toString()) {
                    paramData = StringUtils.trim(child.text)
                }
            }
            if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramData)) {
                iterator.remove()
                continue
            }
            if (!paramNameList.contains(paramName)) {
                iterator.remove()
                continue
            }
            paramNameList.remove(paramName)
        }
        for (paramName in paramNameList) {
            paramDocList.add(
                """@param $paramName ${translatorService.translate(paramName)}
"""
            )
        }
        return paramDocList
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
     * 自定义生成
     *
     * @param psiMethod 当前方法
     * @return [java.lang.String]
     */
    private fun customGenerate(psiMethod: PsiMethod): String {
        return variableGeneratorService.generate(psiMethod)
    }
}
