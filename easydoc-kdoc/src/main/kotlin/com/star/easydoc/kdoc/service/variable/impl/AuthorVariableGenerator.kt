package com.star.easydoc.kdoc.service.variable.impl

import com.intellij.psi.PsiElement

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:16:00
 */
class AuthorVariableGenerator : AbstractVariableGenerator() {
    override fun generate(element: PsiElement): String {
        return config.author
    }
}