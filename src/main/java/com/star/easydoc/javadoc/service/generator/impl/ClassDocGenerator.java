package com.star.easydoc.javadoc.service.generator.impl;

import java.util.Map;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import org.apache.commons.lang3.StringUtils;

/**
 * 类文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class ClassDocGenerator extends AbstractDocGenerator {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(
        JavadocVariableGeneratorService.class);

    private static final String DEFAULT_TEMPLATE = "/**\n"
        + "* $DOC$\n"
        + "* \n"
        + "* @author $AUTHOR$\n"
        + "* @date $DATE$\n"
        + "*/";

    @Override
    public String generate(PsiElement psiElement) {
        if (!(psiElement instanceof PsiClass)) {
            return StringUtils.EMPTY;
        }
        PsiClass psiClass = (PsiClass)psiElement;
        PsiDocComment docComment = psiClass.getDocComment();
        if (EasyDocConfig.COVER_MODE_IGNORE.equals(config.getCoverMode()) && docComment != null) {
            return null;
        }
        String template = DEFAULT_TEMPLATE;
        if (config.getClassTemplateConfig() != null
            && Boolean.FALSE.equals(config.getClassTemplateConfig().getIsDefault())) {
            template = config.getClassTemplateConfig().getTemplate();
        }
        String targetJavadoc = javadocVariableGeneratorService.generate(psiClass, template,
            config.getClassTemplateConfig().getCustomMap(), getClassInnerVariable(psiClass));
        return merge(psiClass, targetJavadoc);
    }

    /**
     * 获取类内部变量
     *
     * @param psiClass psi类
     * @return {@link java.util.Map<java.lang.String,java.lang.Object>}
     */
    private Map<String, Object> getClassInnerVariable(PsiClass psiClass) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("author", config.getAuthor());
        map.put("className", psiClass.getQualifiedName());
        map.put("simpleClassName", psiClass.getName());
        map.put("branch", VcsUtil.getCurrentBranch(psiClass.getProject()));
        return map;
    }

    @Override
    protected EasyDocConfig getConfig() {
        return config;
    }
}
