package com.star.easydoc.kdoc.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.ServiceManager
import com.star.easydoc.common.util.LanguageUtil
import com.star.easydoc.kdoc.service.DocGeneratorService
import com.star.easydoc.kdoc.service.WriterService
import com.star.easydoc.kdoc.view.inner.TranslateResultView
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

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

        if (psiElement?.node == null) {
            return
        }
        val comment = docGeneratorService.generate(psiElement)
        if (StringUtils.isEmpty(comment)) {
            return
        }
        val factory = KtPsiFactory(project)
        val psiDocComment = factory.createComment(comment)

        writerService.writeKdoc(project,  psiElement as KtElement, psiDocComment as KDoc)
    }
}