package com.star.easydoc.action;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.service.DocService;
import com.star.easydoc.service.DocWriterService;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangchao
 * @date 2019/08/24
 */
public class GeneratorJavadocAction extends AnAction {

    private DocService docService = ServiceManager.getService(DocService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            return;
        }

        Project project = anActionEvent.getData(LangDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;

        List<String> paramNameList = Arrays.stream(psiMethod.getParameters())
            .map(JvmParameter::getName).collect(Collectors.toList());
        String returnName = psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getCanonicalText();
        String comment = docService.generate(psiMethod.getName(), paramNameList, returnName);

        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        DocWriterService.write(project, psiMethod, psiDocComment);
    }
}
