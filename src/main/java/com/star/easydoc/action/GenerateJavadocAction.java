package com.star.easydoc.javadoc.service.action;

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

    /**
     * 动作监听
     * @param anActionEvent Carries information on the invocation place
     */

    /**
     * 获取当前的项目对象。
     *
     * 获取编辑器对象，并判断是否选中了文本。
     *
     * 如果选中了文本，根据文本内容是中文还是其他语言进行不同的处理：
     * 如果是中文，则调用翻译服务将中文翻译成英文，并将结果写入编辑器。
     * 如果是其他语言，则对文本进行处理，将没有空格的文本通过空格分隔单词，并调用翻译服务自动翻译，并显示结果。
     *
     * 如果没有选中文本，则处理 PsiFile 和 PsiElement。
     *
     * 对于 Java 文件，调用 javadocProcess 方法进行处理。
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取当前的项目对象
        Project project = anActionEvent.getData(LangDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        // 选中翻译功能
        Editor editor = anActionEvent.getData(LangDataKeys.EDITOR);
        if (editor != null) {
            // 获取选中的文本
            String selectedText = editor.getSelectionModel().getSelectedText(true);
            if (StringUtils.isNotBlank(selectedText)) {
                // 中译英
                if (LanguageUtil.isAllChinese(selectedText)) {
                    // 调用翻译服务将中文翻译成英文并将结果写入编辑器
                    writerService.write(project, editor, translatorService.translateCh2En(selectedText));
                }
                // 自动翻译
                else {
                    String eng = selectedText;
                    // 对于没有空格的文本，通过空格分隔单词
                    if (!selectedText.contains(StringUtils.SPACE)) {
                        eng = StringUtils.join(StringUtil.split(selectedText), StringUtils.SPACE);
                    }
                    // 调用翻译服务自动翻译并显示结果
                    String result = translatorService.autoTranslate(eng);
                    new TranslateResultView(result).show();
                }
                return;
            }
        }

        // 处理 PsiFile 和 PsiElement
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        PsiElement psiElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (psiFile == null || psiElement == null) {
            return;
        }

        // 处理 Java 文件
        if (psiFile instanceof PsiJavaFile) {
            javadocProcess(project, psiFile, psiElement);
        }
        // 处理 Kotlin 文件
        else if (psiFile instanceof KtFile) {
            kdocProcess(project, (KtFile)psiFile, psiElement);
        }
    }

    /**
     * 如果选中了文件夹，判断其中是否需要创建 package-info.java 文件，并生成携带注释的 package-info 文件。
     * 如果当前文件是 package-info.java 文件，则生成携带注释的 package-info 注释。
     * 如果选中了 PsiElement（变量、方法、类等），生成 Javadoc 注释并写入对应的位置。
     * @param project
     * @param psiFile
     * @param psiElement
     */
    private void javadocProcess(Project project, PsiFile psiFile, PsiElement psiElement) {

        // 选中文件夹则判断包里面是否需要创建 package-info.java，创建 package-info 并携带注释
        if (psiElement instanceof PsiDirectory) {
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory)psiElement);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            packageInfoService.handle(psiPackage, comment);
            return;
        }
        // 判断是否是 package-info.java，若是则进行 package-info 注释
        if (psiFile != null && PackageInfoService.INFO_FILE_NAME.equals(psiFile.getName())) {
            PsiDirectory psiDirectory = psiFile.getParent();
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
            String comment = translatorService.autoTranslate(psiPackage.getName());
            packageInfoService.handle(psiPackage, comment);
            return;
        }

        // 判断 PsiElement 是否为空
        if (psiElement == null || psiElement.getNode() == null) {
            return;
        }
        // 生成 Javadoc 注释
        String comment = javaDocGeneratorService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        writerService.writeJavadoc(project, psiElement, psiDocComment, StringUtil.endCount(comment, '\n'));
    }

    /**
     *生成KDoc注释，如果生成的注释为空则退出方法。
     *使用KtPsiFactory创建KDoc注释。
     *将KDoc注释写入到指定的元素中
     * 处理指定的PsiElement的Kotlin文档。
     *
     * @param project 当前项目
     * @param psiFile 包含该元素的Kotlin文件
     * @param psiElement 要处理的PsiElement
     */
    private void kdocProcess(Project project, KtFile psiFile, PsiElement psiElement) {
        // 生成KDoc注释
        String comment = kdocGeneratorService.generate(psiElement);
        if (StringUtils.isEmpty(comment)) {
            return;
        }
        // 创建KDoc注释
        KtPsiFactory factory = new KtPsiFactory(project);
        PsiComment psiDocComment = factory.createComment(comment);

        // 将KDoc注释写入元素
        writerService.writeKdoc(project, (KtElement)psiElement, (KDoc)psiDocComment);
    }
}
