package com.star.easydoc.service;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.service.generator.DocGenerator;
import com.star.easydoc.service.generator.impl.ClassDocGenerator;
import com.star.easydoc.service.generator.impl.FieldDocGenerator;
import com.star.easydoc.service.generator.impl.MethodDocGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class DocGeneratorService {

    private Map<Class<? extends PsiElement>, DocGenerator> docGeneratorMap
        = ImmutableMap.<Class<? extends PsiElement>, DocGenerator>builder()
        .put(PsiClass.class, new ClassDocGenerator())
        .put(PsiMethod.class, new MethodDocGenerator())
        .put(PsiField.class, new FieldDocGenerator())
        .build();

    public String generate(PsiElement psiElement) {
        DocGenerator docGenerator = null;
        for (Entry<Class<? extends PsiElement>, DocGenerator> entry : docGeneratorMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(psiElement.getClass())) {
                docGenerator = entry.getValue();
                break;
            }
        }
        if (Objects.isNull(docGenerator)) {
            return StringUtils.EMPTY;
        }
        return StringUtil.replaceChar(docGenerator.generate(psiElement), '\r', '\0');
    }
}
