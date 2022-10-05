package com.star.easydoc.kdoc.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.service.DocGeneratorService
import com.star.easydoc.kdoc.view.inner.GenerateAllView
import com.star.easydoc.service.WriterService
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * 生成所有文档注释
 *
 * @author wangchao
 * @version 1.0.0
 * @since 2019-10-28 00:26:00
 */
class GenerateAllJavadocAction : AnAction() {
    /**
     * 文档服务
     */
    private val docGeneratorService = ServiceManager.getService(DocGeneratorService::class.java)
    private val writerService = ServiceManager.getService(WriterService::class.java)
    private val config = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(LangDataKeys.PROJECT)
        // 前置规则校验
        val psiElement = e.getData(LangDataKeys.PSI_ELEMENT) as? PsiClass ?: return
        // 弹出选择框
        val generateAllView = GenerateAllView()
        generateAllView.classCheckBox.isSelected = Optional.ofNullable(config.genAllClass).orElse(false)
        generateAllView.methodCheckBox.isSelected = Optional.ofNullable(config.genAllMethod).orElse(false)
        generateAllView.fieldCheckBox.isSelected = Optional.ofNullable(config.genAllField).orElse(false)
        generateAllView.innerClassCheckBox.isSelected = Optional.ofNullable(config.genAllInnerClass).orElse(false)
        if (generateAllView.showAndGet()) {
            val isGenClass = generateAllView.classCheckBox.isSelected
            val isGenMethod = generateAllView.methodCheckBox.isSelected
            val isGenField = generateAllView.fieldCheckBox.isSelected
            val isGenInnerClass = generateAllView.innerClassCheckBox.isSelected
            config.genAllClass = isGenClass
            config.genAllMethod = isGenMethod
            config.genAllField = isGenField
            config.genAllInnerClass = isGenInnerClass

            // 生成注释
            genClassJavadoc(project, psiElement, isGenClass, isGenMethod, isGenField, isGenInnerClass)
        }
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
    private fun genClassJavadoc(
        project: Project?, psiClass: PsiClass, isGenClass: Boolean, isGenMethod: Boolean, isGenField: Boolean, isGenInnerClass: Boolean
    ) {
        // 生成类注释
        if (isGenClass) {
            saveJavadoc(project, psiClass)
        }
        // 方法
        Arrays.stream(psiClass.methods).forEach { psiMethod: PsiMethod -> genMethodJavadoc(project, psiMethod, isGenMethod) }
        // 属性
        Arrays.stream(psiClass.fields).forEach { psiField: PsiField -> genFieldJavadoc(project, psiField, isGenField) }
        // 递归遍历子类
        if (isGenInnerClass) {
            val innerClasses = psiClass.innerClasses
            Arrays.stream(innerClasses).forEach { clz: PsiClass -> genClassJavadoc(project, clz, isGenClass, isGenMethod, isGenField, isGenInnerClass) }
        }
    }

    /**
     * 生成方法Javadoc
     *
     * @param project 工程
     * @param psiMethod 当前方法
     * @param isGenMethod 是否生成方法
     */
    private fun genMethodJavadoc(project: Project?, psiMethod: PsiMethod, isGenMethod: Boolean) {
        if (isGenMethod) {
            saveJavadoc(project, psiMethod)
        }
    }

    /**
     * 生成属性Javadoc
     *
     * @param project 工程
     * @param psiField 当前属性
     * @param isGenField 是否生成属性
     */
    private fun genFieldJavadoc(project: Project?, psiField: PsiField, isGenField: Boolean) {
        if (isGenField) {
            saveJavadoc(project, psiField)
        }
    }

    /**
     * 保存Javadoc
     *
     * @param project 工程
     * @param psiElement 当前元素
     */
    private fun saveJavadoc(project: Project?, psiElement: PsiElement?) {
        if (psiElement == null) {
            return
        }
        val comment = docGeneratorService.generate(psiElement)
        if (StringUtils.isBlank(comment)) {
            return
        }
        val factory = PsiElementFactory.SERVICE.getInstance(project)
        val psiDocComment = factory.createDocCommentFromText(comment)
        writerService.write(project, psiElement, psiDocComment)
    }
}