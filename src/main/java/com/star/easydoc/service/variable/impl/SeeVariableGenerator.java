package com.star.easydoc.service.variable.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.service.variable.VariableGenerator;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:17:00
 */
public class SeeVariableGenerator implements VariableGenerator {

    @Override
    public String generate(PsiElement element) {
        if (element instanceof PsiClass) {
            PsiClass superClass = ((PsiClass) element).getSuperClass();
            PsiClass[] interfaces = ((PsiClass) element).getInterfaces();
            List<String> superList = Lists.newArrayList();
            if (superClass != null) {
                superList.add(superClass.getQualifiedName());
            }
            if (interfaces.length > 0) {
                superList.addAll(Arrays.stream(interfaces).map(PsiClass::getQualifiedName).collect(Collectors.toList()));
            }
            return superList.stream().map(sup -> "@see " + sup).collect(Collectors.joining(System.lineSeparator()));
        } else if (element instanceof PsiMethod) {
            return "";
        } else if (element instanceof PsiField) {
            return "@see " + ((PsiField) element).getType().getCanonicalText();
        } else {
            return "";
        }
    }
}