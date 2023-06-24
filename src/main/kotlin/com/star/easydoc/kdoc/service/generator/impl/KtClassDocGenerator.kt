package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.common.util.VcsUtil
import com.star.easydoc.config.EasyDocConfig
import com.star.easydoc.config.EasyDocConfigComponent
import com.star.easydoc.javadoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.KdocVariableGeneratorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass

/**
 * @constructor
 */
class KtClassDocGenerator : DocGenerator {
    private val config: EasyDocConfig = ServiceManager.getService(EasyDocConfigComponent::class.java).state!!
    private val kdocVariableGeneratorService = ServiceManager.getService(KdocVariableGeneratorService::class.java)

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtClass) {
            return StringUtils.EMPTY
        }
        return if (config.kdocClassTemplateConfig != null && config.kdocClassTemplateConfig.isDefault
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
        return kdocVariableGeneratorService.generate(
            psi, "/**\n" +
                    " * \$DOC\$\n" +
                    " *\n" +
                    " * @author \$AUTHOR\$\n" +
                    " * @date \$DATE\$\n" +
                    " * @constructor \$CONSTRUCTOR\$\n" +
                    " * \$PARAMS\$\n" +
                    " */".trimIndent(),
            config.kdocClassTemplateConfig.customMap, getClassInnerVariable(psi)
        )
    }

    /**
     * 自定义生成
     *
     * @param psi 当前类
     * @return [java.lang.String]
     */
    private fun customGenerate(psi: KtClass): String {
        return kdocVariableGeneratorService.generate(
            psi, config.kdocClassTemplateConfig.template,
            config.kdocClassTemplateConfig.customMap, getClassInnerVariable(psi)
        )
    }

    /**
     * 获取类内部变量
     *
     * @param psiClass psi类
     * @return [,][<]
     */
    private fun getClassInnerVariable(psiClass: KtClass): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = mutableMapOf()
        map["author"] = config.kdocAuthor
        map["className"] = psiClass.fqName
        map["simpleClassName"] = psiClass.name
        map["branch"] = VcsUtil.getCurrentBranch(psiClass.project)
        return map
    }

}