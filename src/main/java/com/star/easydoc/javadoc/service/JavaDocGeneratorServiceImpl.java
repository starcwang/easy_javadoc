package com.star.easydoc.javadoc.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.javadoc.service.generator.DocGenerator;
import com.star.easydoc.javadoc.service.generator.impl.ClassDocGenerator;
import com.star.easydoc.javadoc.service.generator.impl.FieldDocGenerator;
import com.star.easydoc.javadoc.service.generator.impl.MethodDocGenerator;
import com.star.easydoc.javadoc.service.generator.impl.PackageInfoDocGenerator;
import com.star.easydoc.service.DocGeneratorService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class JavaDocGeneratorServiceImpl implements DocGeneratorService {

    private Map<Class<? extends PsiElement>, DocGenerator> docGeneratorMap
        = ImmutableMap.<Class<? extends PsiElement>, DocGenerator>builder()
        .put(PsiClass.class, new ClassDocGenerator())
        .put(PsiMethod.class, new MethodDocGenerator())
        .put(PsiField.class, new FieldDocGenerator())
        .put(PsiPackage.class, new PackageInfoDocGenerator())
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
        return docGenerator.generate(psiElement);
    }
}
