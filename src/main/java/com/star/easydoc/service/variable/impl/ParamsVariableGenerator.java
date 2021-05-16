package com.star.easydoc.service.variable.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.star.easydoc.service.TranslatorService;
import com.star.easydoc.service.variable.VariableGenerator;
import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
public class ParamsVariableGenerator implements VariableGenerator {
    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return "";
        }

        List<String> paramNameList = Arrays.stream(((PsiMethod)element).getParameterList().getParameters())
            .map(PsiParameter::getName).collect(Collectors.toList());
        if (paramNameList.isEmpty()) {
            return "";
        }

        List<ParamGroup> paramGroupList = new ArrayList<>();
        PsiDocComment docComment = ((PsiMethodImpl)element).getDocComment();
        // {"paramName":PsiDocTag}
        Map<String, PsiDocTag> psiDocTagMap = new HashMap<>();
        if (docComment != null) {
            PsiDocTag[] paramsDocArray = docComment.findTagsByName("param");
            psiDocTagMap = Arrays.stream(paramsDocArray).collect(Collectors.toMap(q -> q.getDataElements()[0].getText(), tag -> tag));
        }

        for (String paramName : paramNameList) {
            PsiDocTag psiDocTag = psiDocTagMap.get(paramName);
            if (psiDocTag == null) {
                // 不存在则插入一个需要翻译的
                paramGroupList.add(new ParamGroup(paramName, translatorService.translate(paramName)));
                continue;
            }
            PsiElement eleParamDesc = psiDocTag.getDataElements()[1];
            String desc = eleParamDesc.getText();
            if (StringUtils.isNotEmpty(desc)) {
                // 如果已经存在注释则直接返回
                paramGroupList.add(new ParamGroup(paramName, desc));
            } else {
                // 不存在注释则翻译
                paramGroupList.add(new ParamGroup(paramName, translatorService.translate(paramName)));
            }
        }
        return paramGroupList.stream()
            .map(paramGroup -> "@param " + paramGroup.getParam() + " " + paramGroup.getDesc())
            .collect(Collectors.joining("\n"));
    }

    /**
     * 参数名注释组合
     */
    static class ParamGroup {
        private String param;
        private String desc;

        public ParamGroup(String param, String desc) {
            this.param = param;
            this.desc = desc;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
