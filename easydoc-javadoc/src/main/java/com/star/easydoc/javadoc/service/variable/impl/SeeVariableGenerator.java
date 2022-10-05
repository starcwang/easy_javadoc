package com.star.easydoc.javadoc.service.variable.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiTypeElement;
import com.star.easydoc.common.Consts;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:17:00
 */
public class SeeVariableGenerator extends AbstractVariableGenerator {

    @Override
    public String generate(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass superClass = ((PsiClass)element).getSuperClass();
            PsiClass[] interfaces = ((PsiClass)element).getInterfaces();
            List<String> superList = Lists.newArrayList();
            if (superClass != null) {
                if (!"Object".equalsIgnoreCase(superClass.getName())) {
                    superList.add(superClass.getName());
                }
            }
            if (interfaces.length > 0) {
                superList.addAll(Arrays.stream(interfaces).map(PsiClass::getName).collect(Collectors.toList()));
            }
            return superList.stream().map(sup -> "@see " + sup).collect(Collectors.joining("\n"));
        } else if (element instanceof PsiMethod) {
            StringBuilder seeString = new StringBuilder();
            PsiParameterList parameterList = ((PsiMethod)element).getParameterList();
            for (PsiParameter parameter : parameterList.getParameters()) {
                if (parameter == null || parameter.getTypeElement() == null) {
                    continue;
                }
                seeString.append("@see ").append(parameter.getTypeElement().getText()).append("\n");
            }
            PsiTypeElement returnTypeElement = ((PsiMethod)element).getReturnTypeElement();
            if (returnTypeElement != null && !"void".equals(returnTypeElement.getText())) {
                seeString.append("@see ").append(returnTypeElement.getText()).append("\n");
            }

            return seeString.toString();
        } else if (element instanceof PsiField) {
            String type = ((PsiField)element).getType().getPresentableText();
            if (Consts.BASE_TYPE_SET.contains(type)) {
                return "";
            }
            return "@see " + type;
        } else {
            return "";
        }
    }
}