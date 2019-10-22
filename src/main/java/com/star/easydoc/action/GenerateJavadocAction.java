package com.star.easydoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.service.DocService;
import com.star.easydoc.service.DocWriterService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class GenerateJavadocAction extends AnAction {

    private DocService docService = ServiceManager.getService(DocService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);

        if (psiElement == null) {
            return;
        }

        Project project = anActionEvent.getData(LangDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        String comment = docService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        DocWriterService.write(project, psiElement, psiDocComment);
    }
}
