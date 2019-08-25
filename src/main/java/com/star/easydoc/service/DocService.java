package com.star.easydoc.service;

import java.util.List;

import com.intellij.openapi.components.ServiceManager;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class DocService {

    private TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);

    public String generate(String methodName, List<String> paramNames, String returnName) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append("* ").append(translatorService.translate(methodName)).append("\n");
        sb.append("*\n");
        for (String paramName : paramNames) {
            sb.append("* @param ").append(paramName).append(" ").append(translatorService.translate(paramName))
                .append("\n");
        }
        if (returnName != null && returnName.length() > 0 && !"void".equals(returnName)) {
            sb.append("* @return ").append(returnName);
        }
        sb.append("*/\n");
        return sb.toString();
    }
}
