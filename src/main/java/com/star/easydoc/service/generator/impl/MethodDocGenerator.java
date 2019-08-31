package com.star.easydoc.service.generator.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.generator.DocGenerator;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/31
 */
public class MethodDocGenerator implements DocGenerator {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiMethod psiMethod = (PsiMethod)psiElement;
        List<String> paramNameList = Arrays.stream(psiMethod.getParameters())
            .map(JvmParameter::getName).collect(Collectors.toList());
        String returnName = psiMethod.getReturnType() == null ? "" : psiMethod.getReturnType().getCanonicalText();

        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append("* ").append(translatorService.translate(psiMethod.getName())).append("\n");
        sb.append("*\n");
        for (String paramName : paramNameList) {
            sb.append("* @param ").append(paramName).append(" ").append(translatorService.translate(paramName))
                .append("\n");
        }
        if (returnName.length() > 0 && !"void".equals(returnName)) {
            sb.append("* @return ").append(returnName);
        }
        sb.append("*/\n");
        return sb.toString();
    }
}
