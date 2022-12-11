package com.star.easydoc.kdoc.service.variable

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.star.easydoc.config.EasyDocConfig
import com.star.easydoc.config.EasyDocConfig.VariableType
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.javadoc.service.variable.VariableGenerator
import com.star.easydoc.kdoc.service.variable.impl.*
import groovy.lang.Binding
import groovy.lang.GroovyShell
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

/**
 * 变量生成器服务
 *
 * @author wangchao
 * @date 2019/12/08
 */
class KdocVariableGeneratorService {
    private val pattern = Pattern.compile("\\$[a-zA-Z0-9_-]*\\$")
    private val config = ServiceManager.getService(EasyDocConfigComponent::class.java).state

    /**
     * 变量生成器映射
     */
    private val variableGeneratorMap: Map<String, VariableGenerator> = mapOf(
        "author" to AuthorKdocVariableGenerator(),
        "date" to DateKdocVariableGenerator(),
        "doc" to DocKdocVariableGenerator(),
        "params" to ParamsKdocVariableGenerator(),
        "return" to ReturnKdocVariableGenerator(),
        "see" to SeeKdocVariableGenerator(),
        "since" to SinceKdocVariableGenerator(),
        "constructor" to ConstructorKdocVariableGenerator(),
        "version" to VersionKdocVariableGenerator())

    /**
     * 生成
     *
     * @param psiElement 当前元素
     * @return [java.lang.String]
     */
    fun generate(psiElement: PsiElement, template: String?,
                 customValueMap: Map<String?, EasyDocConfig.CustomValue?>,
                 innerVariableMap: Map<String?, Any?>): String {
        if (template.isNullOrBlank()) {
            return ""
        }

        // 匹配占位符
        val matcher = pattern.matcher(template)
        val variableMap: MutableMap<String, String> = Maps.newHashMap()
        while (matcher.find()) {
            val placeholder = matcher.group()
            val key = StringUtils.substring(placeholder, 1, -1)
            if (StringUtils.isBlank(key)) {
                return ""
            }
            val variableGenerator = variableGeneratorMap[key.toLowerCase()]
            if (variableGenerator == null) {
                variableMap[placeholder] = generateCustomVariable(customValueMap, innerVariableMap, placeholder)
            } else {
                variableMap[placeholder] = variableGenerator.generate(psiElement)
            }
        }

        // 占位符替换
        val keyList: MutableList<String> = Lists.newArrayList()
        val valueList: MutableList<String> = Lists.newArrayList()
        for ((key, value) in variableMap) {
            keyList.add(key)
            valueList.add(value)
        }
        return StringUtils.replaceEach(template, keyList.toTypedArray(), valueList.toTypedArray())
    }

    /**
     * 生成自定义变量
     *
     * @param customValueMap 自定义值
     * @param placeholder 占位符
     * @param innerVariableMap 内部变量映射
     * @return [String]
     */
    private fun generateCustomVariable(
        customValueMap: Map<String?, EasyDocConfig.CustomValue?>, innerVariableMap: Map<String?, Any?>,
        placeholder: String
    ): String {
        var value: EasyDocConfig.CustomValue? = null
        for (entry in customValueMap) {
            if (placeholder.equals(entry.key, ignoreCase = true)) {
                value = entry.value
            }
        }
        // 找不到自定义方法，返回原占位符
        if (value == null) {
            return placeholder
        }
        return when (value.type) {
            VariableType.STRING -> value.value
            VariableType.GROOVY -> {
                return try {
                    GroovyShell(Binding(innerVariableMap)).evaluate(value.value).toString()
                } catch (e: Exception) {
                    LOGGER.error(
                        String.format(
                            "自定义变量%s的groovy脚本执行异常，请检查语法是否正确且有正确返回值:%s", placeholder,
                            value.value
                        ), e
                    )
                    value.value
                }
            }

            else -> ""
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(KdocVariableGeneratorService::class.java)
    }
}