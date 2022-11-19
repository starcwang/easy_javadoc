package com.star.easydoc.javadoc.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.common.util.LanguageUtil;
import com.star.easydoc.javadoc.service.DocGeneratorService;
import com.star.easydoc.service.WriterService;
import com.star.easydoc.javadoc.view.inner.TranslateResultView;
import com.star.easydoc.service.translator.TranslatorService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangchao
 * @date 2019/09/01
 */
public class GenerateJavadocAction extends AnAction {

    private DocGeneratorService docGeneratorService = ServiceManager.getService(DocGeneratorService.class);
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private WriterService writerService = ServiceManager.getService(WriterService.class);

    /**
     * 初始化
     */
    public GenerateJavadocAction() {
        super();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(LangDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        // 选中翻译功能
        Editor editor = anActionEvent.getData(LangDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText(true);
            if (StringUtils.isNotBlank(selectedText)) {
                // 中译英
                if (LanguageUtil.isAllChinese(selectedText)) {
                    writerService.write(project, editor, translatorService.translateCh2En(selectedText));
                }
                // 自动翻译
                else {
                    String result = translatorService.autoTranslate(selectedText);
                    new TranslateResultView(result).show();
                }
                return;
            }
        }

        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        //选中文件夹则判断包里面是否需要创建package-info.java，创建package-info 并携带注释
        if (psiElement instanceof PsiDirectory) {
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory)psiElement);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            PackageInfoHandle.handle(psiPackage, comment);
            return;
        }
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        //判断是否是package-info.java若是则进行package-info注释
        if (psiFile != null && PackageInfoHandle.INFO_FILE_NAME.equals(psiFile.getName())) {
            PsiDirectory psiDirectory = psiFile.getParent();
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            PackageInfoHandle.handle(psiPackage, comment);
            return;
        }

        if (psiElement == null || psiElement.getNode() == null) {
            return;
        }
        String comment = docGeneratorService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        writerService.writeJavadoc(project, psiElement, psiDocComment);
    }
}
