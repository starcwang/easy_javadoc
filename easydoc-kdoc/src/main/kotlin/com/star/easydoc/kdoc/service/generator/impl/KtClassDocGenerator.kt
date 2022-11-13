package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass


class KtClassDocGenerator : DocGenerator {
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(VariableGeneratorService::class.java)

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtClass) {
            return StringUtils.EMPTY
        }
        return if (config.classTemplateConfig != null && true == config.classTemplateConfig.isDefault
        ) {
            defaultGenerate(psiElement)
        } else {
            customGenerate(psiElement)
        }
    }

    /**
     * 默认的生成
     *
     * @param psi 当前类
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psi: KtClass): String {
        return variableGeneratorService.generate(
            psi, "/**\n" +
                    " * \$DOC\$\n" +
                    " *\n" +
                    " * @author \$AUTHOR\$\n" +
                    " * @date \$DATE\$\n" +
                    " * \$PARAMS\$\n" +
                    " */".trimIndent()
        )
    }

    /**
     * 自定义生成
     *
     * @param psi 当前类
     * @return [java.lang.String]
     */
    private fun customGenerate(psi: KtClass): String {
        return variableGeneratorService.generate(psi, null)
    }

}