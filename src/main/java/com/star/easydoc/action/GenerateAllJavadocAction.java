package com.star.easydoc.action;

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
import com.star.easydoc.service.DocService;
import com.star.easydoc.service.DocWriterService;
import com.star.easydoc.view.inner.GenerateAllView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
    private DocService docService = ServiceManager.getService(DocService.class);

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
        // 弹出选择框
        GenerateAllView generateAllView = new GenerateAllView();
        if (generateAllView.showAndGet()) {
            // 生成注释
            Project project = e.getData(LangDataKeys.PROJECT);
            genClassJavadoc(project, (PsiClass) psiElement,
                    generateAllView.getClassCheckBox().isSelected(),
                    generateAllView.getMethodCheckBox().isSelected(),
                    generateAllView.getFieldCheckBox().isSelected());
        }
    }

    /**
     * 生成类Javadoc
     *
     * @param project     工程
     * @param psiClass    当前类
     * @param isGenClass  是否生成类
     * @param isGenMethod 是否生成方法
     * @param isGenField  是否生成属性
     */
    private void genClassJavadoc(Project project, PsiClass psiClass, boolean isGenClass, boolean isGenMethod, boolean isGenField) {
        // 生成类注释
        if (isGenClass) {
            saveJavadoc(project, psiClass);
        }
        // 方法
        Arrays.stream(psiClass.getAllMethods()).forEach(psiMethod -> genMethodJavadoc(project, psiMethod, isGenMethod));
        // 属性
        Arrays.stream(psiClass.getAllFields()).forEach(psiField -> genFieldJavadoc(project, psiField, isGenField));
        // 递归遍历子类
        PsiClass[] innerClasses = psiClass.getInnerClasses();
        Arrays.stream(innerClasses).forEach(clz -> genClassJavadoc(project, clz, isGenClass, isGenMethod, isGenField));
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
        String comment = docService.generate(psiElement);
        if (StringUtils.isBlank(comment)) {
            return;
        }
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiDocComment psiDocComment = factory.createDocCommentFromText(comment);

        DocWriterService.write(project, psiElement, psiDocComment);
    }
}
