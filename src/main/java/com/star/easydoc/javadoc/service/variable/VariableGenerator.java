package com.star.easydoc.javadoc.service.variable;

import com.intellij.psi.PsiElement;
import com.star.easydoc.config.EasyDocConfig;

/**
 * 变量生成器
 *
 * @author wangchao
 * @date 2019/12/07
 */
public interface VariableGenerator {
    /**
     * 生成
     *
     * @param element 元素
     * @return {@link String}
     */
    String generate(PsiElement element);

    /**
     * 获取配置
     *
     * @return {@link EasyDocConfig}
     */
    EasyDocConfig getConfig();
}
