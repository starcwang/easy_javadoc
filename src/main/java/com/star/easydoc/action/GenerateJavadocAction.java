package com.star.easydoc.action;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.messages.MessageBusConnection;
import com.star.easydoc.common.util.LanguageUtil;
import com.star.easydoc.common.util.StringUtil;
import com.star.easydoc.javadoc.service.JavaDocGeneratorServiceImpl;
import com.star.easydoc.kdoc.service.KdocGeneratorServiceImpl;
import com.star.easydoc.listener.AppActivationListener;
import com.star.easydoc.service.PackageInfoService;
import com.star.easydoc.service.WriterService;
import com.star.easydoc.service.translator.TranslatorService;
import com.star.easydoc.view.inner.TranslateResultView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.psi.KtPsiFactory;

import static org.jetbrains.kotlin.psi.KtPsiFactoryKt.KtPsiFactory;

/**
 * @author wangchao
 * @date 2019/09/01
 */
public class GenerateJavadocAction extends AnAction {

    private JavaDocGeneratorServiceImpl javaDocGeneratorService = ServiceManager.getService(JavaDocGeneratorServiceImpl.class);
    private KdocGeneratorServiceImpl kdocGeneratorService = ServiceManager.getService(KdocGeneratorServiceImpl.class);
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private WriterService writerService = ServiceManager.getService(WriterService.class);
    private PackageInfoService packageInfoService = ServiceManager.getService(PackageInfoService.class);

    /**
     * 初始化
     */
    public GenerateJavadocAction() {
        super();

        // 设置消息监听
        AppActivationListener listener = new AppActivationListener();
        Application app = ApplicationManager.getApplication();
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(app, disposable);
        MessageBusConnection connection = app.getMessageBus().connect(disposable);
        connection.subscribe(ApplicationActivationListener.TOPIC, listener);
        listener.activate();
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
                    String eng = selectedText;
                    if (!selectedText.contains(StringUtils.SPACE)) {
                        eng = StringUtils.join(StringUtil.split(selectedText), StringUtils.SPACE);
                    }
                    String result = translatorService.autoTranslate(eng);
                    new TranslateResultView(result).show();
                }
                return;
            }
        }

        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (psiFile == null || psiElement == null) {
            return;
        }

        if (psiFile instanceof PsiJavaFile) {
            javadocProcess(project, psiFile, psiElement);
        } else if (psiFile instanceof KtFile) {
            kdocProcess(project, (KtFile)psiFile, psiElement);
        }

    }

    private void javadocProcess(Project project, PsiFile psiFile, PsiElement psiElement) {

        //选中文件夹则判断包里面是否需要创建package-info.java，创建package-info 并携带注释
        if (psiElement instanceof PsiDirectory) {
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory)psiElement);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            packageInfoService.handle(psiPackage, comment);
            return;
        }
        //判断是否是package-info.java若是则进行package-info注释
        if (psiFile != null && PackageInfoService.INFO_FILE_NAME.equals(psiFile.getName())) {
            PsiDirectory psiDirectory = psiFile.getParent();
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            packageInfoService.handle(psiPackage, comment);
            return;
        }

        if (psiElement == null || psiElement.getNode() == null) {
            return;
        }
        String comment = javaDocGeneratorService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        writerService.writeJavadoc(project, psiElement, psiDocComment, StringUtil.endCount(comment, '\n'));
    }

    private void kdocProcess(Project project, KtFile psiFile, PsiElement psiElement) {

        String comment = kdocGeneratorService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }
        KtPsiFactory factory = KtPsiFactory(project);
        PsiComment psiDocComment = factory.createComment(comment);

        writerService.writeKdoc(project, (KtElement)psiElement, (KDoc)psiDocComment);
    }
}
