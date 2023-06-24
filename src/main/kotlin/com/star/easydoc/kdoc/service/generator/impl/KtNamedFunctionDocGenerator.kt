package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.common.util.VcsUtil
import com.star.easydoc.config.EasyDocConfig
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.javadoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.KdocVariableGeneratorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class KtNamedFunctionDocGenerator : DocGenerator {
    private val config: EasyDocConfig = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    private val kdocVariableGeneratorService = ServiceManager.getService(
        KdocVariableGeneratorService::class.java
    )

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtNamedFunction) {
            return StringUtils.EMPTY
        }
        return if (config.kdocMethodTemplateConfig != null && config.kdocMethodTemplateConfig.isDefault
        ) {
            defaultGenerate(psiElement)
        } else {
            customGenerate(psiElement)
        }
    }

    /**
     * 默认的生成
     *
     * @param psi 当前方法
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psi: KtNamedFunction): String {
        val template = """
            /**
             * ${'$'}DOC$
             * 
             * ${'$'}PARAMS$
             * ${'$'}RETURN$
             */
          """.trimIndent()
        return kdocVariableGeneratorService.generate(
            psi, template, config.kdocMethodTemplateConfig.customMap, getMethodInnerVariable(psi)
        )
    }

    /**
     * 自定义生成
     *
     * @param psi 当前方法
     * @return [java.lang.String]
     */
    private fun customGenerate(psi: KtNamedFunction): String {
        return kdocVariableGeneratorService.generate(
            psi, config.kdocMethodTemplateConfig.template,
            config.kdocMethodTemplateConfig.customMap, getMethodInnerVariable(psi)
        )
    }

    /**
     * 获取方法内部的变量
     *
     * @param psiMethod psi方法
     * @return [,][<]
     */
    private fun getMethodInnerVariable(psiMethod: KtNamedFunction): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = mutableMapOf()
        map["author"] = config.kdocAuthor
        map["methodName"] = psiMethod.name
        map["methodReturnType"] = psiMethod.typeReference?.text
        map["methodParamTypes"] = psiMethod.valueParameters.map { StringUtils.strip(it.typeReference?.text, "?") }
            .toTypedArray()
        map["methodParamNames"] = psiMethod.valueParameters.map { it.name }.toTypedArray()
        map["branch"] = VcsUtil.getCurrentBranch(psiMethod.project)
        return map
    }
}