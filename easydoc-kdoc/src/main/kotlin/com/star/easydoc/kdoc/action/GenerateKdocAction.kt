package com.star.easydoc.kdoc.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElementFactory
import com.star.easydoc.common.util.LanguageUtil
import com.star.easydoc.kdoc.service.DocGeneratorService
import com.star.easydoc.kdoc.view.inner.TranslateResultView
import com.star.easydoc.service.WriterService
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass

/**
 * @author wangchao
 * @date 2019/09/01
 */
class GenerateKdocAction
/**
 * 初始化
 */
    : AnAction() {
    private val docGeneratorService = ServiceManager.getService(DocGeneratorService::class.java)
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    private val writerService = ServiceManager.getService(WriterService::class.java)
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.getData(LangDataKeys.PROJECT) ?: return

        // 选中翻译功能
        val editor = anActionEvent.getData(LangDataKeys.EDITOR)
        if (editor != null) {
            val selectedText = editor.selectionModel.getSelectedText(true)
            if (StringUtils.isNotBlank(selectedText)) {
                // 中译英
                if (LanguageUtil.isAllChinese(selectedText)) {
                    writerService.write(project, editor, translatorService.translateCh2En(selectedText))
                } else {
                    val result = translatorService.autoTranslate(selectedText)
                    TranslateResultView(result).show()
                }
                return
            }
        }
        val psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT)
        val ktClass = psiElement as KtClass?

        if (psiElement == null || psiElement.getNode() == null) {
            return
        }
        val comment = docGeneratorService.generate(psiElement)
        if (StringUtils.isEmpty(comment)) {
            return
        }
        val factory = PsiElementFactory.SERVICE.getInstance(project)
        val psiDocComment = factory.createDocCommentFromText(comment)
        writerService.write(project, psiElement, psiDocComment)
    }
}