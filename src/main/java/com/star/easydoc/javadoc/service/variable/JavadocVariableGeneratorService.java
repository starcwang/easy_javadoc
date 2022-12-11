package com.star.easydoc.javadoc.service.variable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.star.easydoc.config.EasyDocConfig.CustomValue;
import com.star.easydoc.javadoc.service.variable.impl.AuthorVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.DateVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.DocVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.ParamsVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.ReturnVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.SeeVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.SinceVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.ThrowsVariableGenerator;
import com.star.easydoc.javadoc.service.variable.impl.VersionVariableGenerator;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang3.StringUtils;

/**
 * 变量生成器服务
 *
 * @author wangchao
 * @date 2019/12/08
 */
public class JavadocVariableGeneratorService {
    private static final Logger LOGGER = Logger.getInstance(JavadocVariableGeneratorService.class);
    private Pattern pattern = Pattern.compile("\\$[a-zA-Z0-9_-]*\\$");

    /**
     * 变量生成器映射
     */
    private Map<String, VariableGenerator> variableGeneratorMap = ImmutableMap.<String, VariableGenerator>builder()
        .put("author", new AuthorVariableGenerator())
        .put("date", new DateVariableGenerator())
        .put("doc", new DocVariableGenerator())
        .put("params", new ParamsVariableGenerator())
        .put("return", new ReturnVariableGenerator())
        .put("see", new SeeVariableGenerator())
        .put("since", new SinceVariableGenerator())
        .put("throws", new ThrowsVariableGenerator())
        .put("version", new VersionVariableGenerator())
        .build();

    /**
     * 生成
     *
     * @param psiElement 当前元素
     * @return {@link java.lang.String}
     */
    public String generate(PsiElement psiElement, String template, Map<String, CustomValue> customValueMap,
        Map<String, Object> innerVariableMap) {

        if (StringUtils.isBlank(template)) {
            return "";
        }

        // 匹配占位符
        Matcher matcher = pattern.matcher(template);
        Map<String, String> variableMap = Maps.newHashMap();
        while (matcher.find()) {
            String placeholder = matcher.group();
            String key = StringUtils.substring(placeholder, 1, -1);
            if (StringUtils.isBlank(key)) {
                return "";
            }
            VariableGenerator variableGenerator = variableGeneratorMap.get(key.toLowerCase());
            if (variableGenerator == null) {
                variableMap.put(placeholder, generateCustomVariable(customValueMap, innerVariableMap, placeholder));
            } else {
                variableMap.put(placeholder, variableGenerator.generate(psiElement));
            }
        }

        // 占位符替换
        List<String> keyList = Lists.newArrayList();
        List<String> valueList = Lists.newArrayList();
        for (Entry<String, String> entry : variableMap.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }
        return StringUtils.replaceEach(template, keyList.toArray(new String[0]), valueList.toArray(new String[0]));
    }

    /**
     * 生成自定义变量
     *
     * @param customValueMap 自定义值
     * @param placeholder 占位符
     * @param innerVariableMap 内部变量映射
     * @return {@link String}
     */
    private String generateCustomVariable(Map<String, CustomValue> customValueMap, Map<String, Object> innerVariableMap,
        String placeholder) {
        Optional<CustomValue> valueOptional = customValueMap.entrySet().stream()
            .filter(entry -> placeholder.equalsIgnoreCase(entry.getKey())).map(Entry::getValue).findAny();
        // 找不到自定义方法，返回原占位符
        if (!valueOptional.isPresent()) {
            return placeholder;
        }
        CustomValue value = valueOptional.get();
        switch (value.getType()) {
            case STRING:
                return value.getValue();
            case GROOVY:
                try {
                    return new GroovyShell(new Binding(innerVariableMap)).evaluate(value.getValue()).toString();
                } catch (Exception e) {
                    LOGGER.error(String.format("自定义变量%s的groovy脚本执行异常，请检查语法是否正确且有正确返回值:%s", placeholder,
                        value.getValue()), e);
                    return value.getValue();
                }
            default:
                return "";
        }
    }

}
