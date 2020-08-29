package com.star.easydoc.action;

import java.util.Arrays;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.service.DocGeneratorService;
import com.star.easydoc.service.WriterService;
import com.star.easydoc.view.inner.GenerateAllView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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
    private DocGeneratorService docGeneratorService = ServiceManager.getService(DocGeneratorService.class);
    private WriterService writerService = ServiceManager.getService(WriterService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 前置规则校验
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return;
        }
        if (!(psiElement instanceof PsiClass)) {
            return;
        }
        Project project = e.getData(LangDataKeys.PROJECT);
        // 弹出选择框
        GenerateAllView generateAllView = new GenerateAllView();
        if (generateAllView.showAndGet()) {
            // 生成注释
            genClassJavadoc(project, (PsiClass) psiElement,
                    generateAllView.getClassCheckBox().isSelected(),
                    generateAllView.getMethodCheckBox().isSelected(),
                    generateAllView.getFieldCheckBox().isSelected(),
                    generateAllView.getInnerClassCheckBox().isSelected());
        }
    }

    /**
     * 生成类Javadoc
     *
     * @param project         项目
     * @param psiClass        当前类
     * @param isGenClass      是否生成类
     * @param isGenMethod     是否生成方法
     * @param isGenField      是否生成属性
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
     * @param project     工程
     * @param psiMethod   当前方法
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
     * @param project    工程
     * @param psiField   当前属性
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
     * @param project    工程
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

        writerService.write(project, psiElement, psiDocComment);
    }
}
