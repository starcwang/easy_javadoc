package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyKdocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * 方法文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
class KtNamedFunctionDocGenerator : DocGenerator {
    private val config: EasyDocConfig = ServiceManager.getService(EasyKdocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(
        VariableGeneratorService::class.java
    )

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtNamedFunction) {
            return StringUtils.EMPTY
        }
        return if (config.methodTemplateConfig != null && java.lang.Boolean.TRUE == config.methodTemplateConfig.isDefault
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
        return variableGeneratorService.generate(
            psi, "/**\n" +
                    " * \$DOC\$\n" +
                    " *\n" +
                    " * \$PARAMS\$\n" +
                    " * \$THROWS\$\n" +
                    " * \$RETURN\$\n" +
                    " */", config.methodTemplateConfig.customMap, getMethodInnerVariable(psi)
        )
    }

    /**
     * 自定义生成
     *
     * @param psi 当前方法
     * @return [java.lang.String]
     */
    private fun customGenerate(psi: KtNamedFunction): String {
        return variableGeneratorService.generate(
            psi, config.methodTemplateConfig.template,
            config.methodTemplateConfig.customMap, getMethodInnerVariable(psi)
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
        map["author"] = config.author
        map["methodName"] = psiMethod.name
        map["methodReturnType"] = if (psiMethod.hasDeclaredReturnType()) "" else psiMethod.typeReference!!.text
        map["methodParamTypes"] = psiMethod.valueParameters.map { StringUtils.strip(it.typeReference?.text, "?") }
            .toTypedArray()
        map["methodParamNames"] = psiMethod.valueParameters.map { it.name }.toTypedArray()
        return map
    }
}
