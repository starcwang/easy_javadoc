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
import org.jetbrains.kotlin.psi.KtProperty

/**
 * 属性文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class KtPropertyDocGenerator : DocGenerator {
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(VariableGeneratorService::class.java)
    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtProperty) {
            return StringUtils.EMPTY
        }
        return if (config.fieldTemplateConfig != null && java.lang.Boolean.TRUE == config.fieldTemplateConfig.isDefault
        ) {
            defaultGenerate(psiElement)
        } else {
            customGenerate(psiElement)
        }
    }

    /**
     * 默认的生成
     *
     * @param psi 当前属性
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psi: KtProperty): String {
        return if (BooleanUtils.isTrue(config.simpleFieldDoc)) {
            variableGeneratorService.generate(psi, "/** \$DOC\$ */")
        } else {
            variableGeneratorService.generate(psi, "/**\n * \$DOC\$\n */")
        }
    }

    /**
     * 自定义生成
     *
     * @param psi 当前属性
     * @return [String]
     */
    private fun customGenerate(psi: KtProperty): String {
        return variableGeneratorService.generate(psi, null)
    }

}