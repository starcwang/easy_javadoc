package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.common.util.VcsUtil
import com.star.easydoc.config.EasyDocConfig
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.javadoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.KdocVariableGeneratorService
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
    private val kdocVariableGeneratorService = ServiceManager.getService(KdocVariableGeneratorService::class.java)
    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtProperty) {
            return StringUtils.EMPTY
        }
        return if (config.kdocFieldTemplateConfig != null && config.kdocFieldTemplateConfig.isDefault
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
        return if (BooleanUtils.isTrue(config.kdocSimpleFieldDoc)) {
            kdocVariableGeneratorService.generate(
                psi, "/** \$DOC\$ */",
                config.kdocFieldTemplateConfig.customMap, getFieldInnerVariable(psi)
            )
        } else {
            kdocVariableGeneratorService.generate(
                psi, "/**\n * \$DOC\$\n */",
                config.kdocFieldTemplateConfig.customMap, getFieldInnerVariable(psi)
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
        return kdocVariableGeneratorService.generate(
            psi, config.kdocFieldTemplateConfig.template,
            config.kdocFieldTemplateConfig.customMap, getFieldInnerVariable(psi)
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
        map["author"] = config.kdocAuthor
        map["fieldName"] = psiField.name
        map["fieldType"] = StringUtils.strip(psiField.typeReference?.typeElement?.text, "?")
        map["branch"] = VcsUtil.getCurrentBranch(psiField.project)
        return map
    }

}