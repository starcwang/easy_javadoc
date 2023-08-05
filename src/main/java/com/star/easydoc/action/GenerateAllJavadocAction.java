package com.star.easydoc.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.common.util.StringUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.JavaDocGeneratorServiceImpl;
import com.star.easydoc.service.PackageInfoService;
import com.star.easydoc.service.WriterService;
import com.star.easydoc.service.translator.TranslatorService;
import com.star.easydoc.view.inner.GenerateAllView;
import com.star.easydoc.view.inner.PackageDescribeView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtFile;

/**
 * 生成所有文档注释
 *
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-10-28 00:26:00
 */
public class GenerateAllJavadocAction extends AnAction {

    /**
     * 文档服务
     */
    private JavaDocGeneratorServiceImpl docGeneratorService = ServiceManager.getService(JavaDocGeneratorServiceImpl.class);
    private WriterService writerService = ServiceManager.getService(WriterService.class);
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
    private PackageInfoService packageInfoService = ServiceManager.getService(PackageInfoService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(LangDataKeys.PROJECT);
        // 前置规则校验
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null || psiElement == null) {
            return;
        }

        if (psiFile instanceof PsiJavaFile) {
            javadocProcess(project, psiFile, psiElement);
        } else if (psiFile instanceof KtFile) {
            kdocProcess(project, (KtFile)psiFile, (KtElement)psiElement);
        }
    }

    /**
     * javadoc处理
     */
    private void javadocProcess(Project project, PsiFile psiFile, PsiElement psiElement) {

        //对文件夹选择的额外处理下
        if (psiElement instanceof PsiDirectory) {
            PackageChooserDialog selector = new PackageChooserDialog("选择多个Packages创建package-info", project);
            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage((PsiDirectory)psiElement);
            if (psiPackage != null) {
                selector.selectPackage(psiPackage.getQualifiedName());
            }
            selector.show();

            List<PsiPackage> packages = selector.getSelectedPackages();
            if (packages == null || packages.isEmpty()) {
                return;
            }
            //执行
            Map<PsiPackage, String> packMap = packages.stream()
                .collect(Collectors.toMap(s -> s, s -> translatorService.autoTranslate(s.getName())));
            //显示列表，一个个的修改后再提交写入更好

            PackageDescribeView packageDescribeView = new PackageDescribeView(packMap);
            if (packageDescribeView.showAndGet()) {
                //重新获取一次
                Map<PsiPackage, String> finalMap = packageDescribeView.getFinalMap();
                //下面是执行，可以考虑并发
                for (Map.Entry<PsiPackage, String> entry : finalMap.entrySet()) {
                    packageInfoService.handle(entry.getKey(), entry.getValue());
                }
            }
            return;
        }

        if (!(psiElement instanceof PsiClass)) {
            return;
        }
        // 弹出选择框
        GenerateAllView generateAllView = new GenerateAllView();
        generateAllView.getClassCheckBox().setSelected(Optional.ofNullable(config.getGenAllClass()).orElse(false));
        generateAllView.getMethodCheckBox().setSelected(Optional.ofNullable(config.getGenAllMethod()).orElse(false));
        generateAllView.getFieldCheckBox().setSelected(Optional.ofNullable(config.getGenAllField()).orElse(false));
        generateAllView.getInnerClassCheckBox().setSelected(Optional.ofNullable(config.getGenAllInnerClass()).orElse(false));

        if (generateAllView.showAndGet()) {

            boolean isGenClass = generateAllView.getClassCheckBox().isSelected();
            boolean isGenMethod = generateAllView.getMethodCheckBox().isSelected();
            boolean isGenField = generateAllView.getFieldCheckBox().isSelected();
            boolean isGenInnerClass = generateAllView.getInnerClassCheckBox().isSelected();

            config.setGenAllClass(isGenClass);
            config.setGenAllMethod(isGenMethod);
            config.setGenAllField(isGenField);
            config.setGenAllInnerClass(isGenInnerClass);

            // 生成注释
            genClassJavadoc(project, (PsiClass)psiElement, isGenClass, isGenMethod, isGenField, isGenInnerClass);
        }
    }

    /**
     * kdoc处理
     */
    private void kdocProcess(Project project, KtFile psiFile, KtElement psiElement) {
        // TODO: 2022/12/4 实现kdoc批量
    }

    /**
     * 生成类Javadoc
     *
     * @param project 项目
     * @param psiClass 当前类
     * @param isGenClass 是否生成类
     * @param isGenMethod 是否生成方法
     * @param isGenField 是否生成属性
     * @param isGenInnerClass 是否生成内部类
     */
    private void genClassJavadoc(Project project, PsiClass psiClass, boolean isGenClass, boolean isGenMethod, boolean isGenField,
        boolean isGenInnerClass) {
        // 生成类注释
        if (isGenClass) {
            saveJavadoc(project, psiClass);
        }
        // 方法
        Arrays.stream(psiClass.getMethods()).forEach(psiMethod -> genMethodJavadoc(project, psiMethod, isGenMethod));
        // 属性
        Arrays.stream(psiClass.getFields()).forEach(psiField -> genFieldJavadoc(project, psiField, isGenField));
        // 递归遍历子类
        if (isGenInnerClass) {
            PsiClass[] innerClasses = psiClass.getInnerClasses();
            Arrays.stream(innerClasses).forEach(clz -> genClassJavadoc(project, clz, isGenClass, isGenMethod, isGenField, isGenInnerClass));
        }
    }

    /**
     * 生成方法Javadoc
     *
     * @param project 工程
     * @param psiMethod 当前方法
     * @param isGenMethod 是否生成方法
     */
    private void genMethodJavadoc(Project project, PsiMethod psiMethod, boolean isGenMethod) {
        if (isGenMethod) {
            saveJavadoc(project, psiMethod);
        }
    }

    /**
     * 生成属性Javadoc
     *
     * @param project 工程
     * @param psiField 当前属性
     * @param isGenField 是否生成属性
     */
    private void genFieldJavadoc(Project project, PsiField psiField, boolean isGenField) {
        if (isGenField) {
            saveJavadoc(project, psiField);
        }
    }

    /**
     * 保存Javadoc
     *
     * @param project 工程
     * @param psiElement 当前元素
     */
    private void saveJavadoc(Project project, PsiElement psiElement) {
        if (psiElement == null) {
            return;
        }
        String comment = docGeneratorService.generate(psiElement);
        if (StringUtils.isBlank(comment)) {
            return;
        }
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        writerService.writeJavadoc(project, psiElement, psiDocComment, StringUtil.endCount(comment, '\n'));
    }
}
