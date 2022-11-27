package com.star.easydoc.kdoc.service

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.ThrowableRunnable
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement

/**
 * @author wangchao
 * @date 2022/11/28
 */
class WriterService {

    fun writeKdoc(project: Project, psiElement: KtElement, comment: KDoc) {
        try {
            WriteCommandAction.writeCommandAction(project).run(
                ThrowableRunnable<Throwable?> {
                    if (psiElement.containingFile == null) {
                        return@ThrowableRunnable
                    }

                    // 写入文档注释
                    if (psiElement is KtDeclaration) {
                        val kDoc = psiElement.docComment
                        if (kDoc == null) {
                            psiElement.getNode().addChild(comment.node, psiElement.getFirstChild().node)
                        } else {
                            kDoc.replace(comment)
                        }
                    }

                    // 格式化文档注释
                    val codeStyleManager = CodeStyleManager.getInstance(psiElement.project)
                    val javadocElement = psiElement.firstChild
                    val startOffset = javadocElement.textOffset
                    val endOffset = javadocElement.textOffset + javadocElement.text.length
                    codeStyleManager.reformatText(psiElement.containingFile, startOffset, endOffset + 1)
                }
            )
        } catch (throwable: Throwable) {
            LOGGER.error("写入错误", throwable)
        }
    }

    fun write(project: Project, editor: Editor, text: String) {
        if (StringUtils.isBlank(text)) {
            return
        }
        try {
            WriteCommandAction.writeCommandAction(project).run(
                ThrowableRunnable<Throwable?> {
                    val start = editor.getSelectionModel().selectionStart
                    EditorModificationUtil.insertStringAtCaret(editor, text)
                    editor.getSelectionModel().setSelection(start, start + text.length)
                }
            )
        } catch (throwable: Throwable) {
            LOGGER.error("写入错误", throwable)
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(
            WriterService::class.java
        )
    }
}