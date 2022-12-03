package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.config.EasyDocConfig
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.javadoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
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
    private val config: EasyDocConfig = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
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
            variableGeneratorService.generate(
                psi, "/** \$DOC\$ */",
                config.fieldTemplateConfig.customMap, getFieldInnerVariable(psi)
            )
        } else {
            variableGeneratorService.generate(
                psi, "/**\n * \$DOC\$\n */",
                config.fieldTemplateConfig.customMap, getFieldInnerVariable(psi)
            )
        }
    }

    /**
     * 自定义生成
     *
     * @param psi 当前属性
     * @return [String]
     */
    private fun customGenerate(psi: KtProperty): String {
        return variableGeneratorService.generate(
            psi, config.fieldTemplateConfig.template,
            config.fieldTemplateConfig.customMap, getFieldInnerVariable(psi)
        )
    }

    /**
     * 获取字段内部的变量
     *
     * @param psiField psi属性
     * @return [,][<]
     */
    private fun getFieldInnerVariable(psiField: KtProperty): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = mutableMapOf()
        map["author"] = config.author
        map["fieldName"] = psiField.name
        map["fieldType"] = StringUtils.strip(psiField.typeReference?.typeElement?.text, "?")
        return map
    }

}