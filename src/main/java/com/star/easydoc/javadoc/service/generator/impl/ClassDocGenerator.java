package com.star.easydoc.javadoc.service.generator.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ResourceUtil;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.util.VcsUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.javadoc.service.variable.JavadocVariableGeneratorService;
import com.star.easydoc.service.gpt.GptService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 类文档生成器
 *
 * @author wangchao
 * @date 2019/11/12
 */
public class ClassDocGenerator extends AbstractDocGenerator {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();
    private GptService gptService = ServiceManager.getService(GptService.class);
    private JavadocVariableGeneratorService javadocVariableGeneratorService = ServiceManager.getService(
        JavadocVariableGeneratorService.class);

    /** 默认模板 */
    private static final String DEFAULT_TEMPLATE = "/**\n"
        + "* $DOC$\n"
        + "*\n"
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

        // AI
        if (Consts.AI_TRANSLATOR.contains(config.getTranslator())) {
            return generateWithAI(psiClass);
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
     * 使用AI生成文档注释
     *
     * @param psiElement psi元素
     * @return 返回生成的字符串 {@link String}
     */
    private String generateWithAI(PsiElement psiElement) {
        String prompt; // 定义提示变量
        try {
            // 将资源文件中的文本读取为字符串
            prompt = IOUtils.toString(ResourceUtil.getResource(getClass(), "prompts/chatglm", "class.prompt"));
        } catch (IOException e) {
            // 如果发生IO异常，则抛出运行时异常
            throw new RuntimeException(e);
        }
        // 替换模板中的作者信息
        prompt = prompt.replace("{author}", getConfig().getAuthor());
        // 替换模板中的日期信息
        prompt = prompt.replace("{date}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        // 替换模板中的代码片段
        prompt = prompt.replace("{code}", psiElement.getText());
        // 调用GPT服务并返回结果
        return gptService.chat(prompt);
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
        map.put("projectName", psiClass.getProject().getName());
        return map;
    }

    @Override
    protected EasyDocConfig getConfig() {
        return config;
    }
}
