package com.star.easydoc.kdoc.service.variable

import com.intellij.psi.PsiElement
import com.star.easydoc.common.config.EasyDocConfig

/**
 * 变量生成器
 *
 * @author wangchao
 * @date 2019/12/07
 */
interface VariableGenerator {
    /**
     * 生成
     *
     * @param element 元素
     * @return [String]
     */
    fun generate(element: PsiElement): String

    /**
     * 获取配置
     *
     * @return [EasyDocConfig]
     */
    val config: EasyDocConfig
}