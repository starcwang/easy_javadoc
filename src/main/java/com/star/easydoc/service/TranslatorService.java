package com.star.easydoc.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.EasyJavadocConfigComponent;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class TranslatorService {

    public String translate(String source) {
        List<String> words = split(source);
        StringBuilder res = new StringBuilder();
        for (String word : words) {
            res.append(Objects.requireNonNull(ServiceManager.getService(EasyJavadocConfigComponent.class)
                .getState()).getWordMap().getOrDefault(word.toLowerCase(), word));
        }
        return res.toString();
    }

    private List<String> split(String word) {
        word = word.replaceAll("(?<=[^A-Z])[A-Z][^A-Z]", "_$0");
        word = word.replaceAll("[A-Z]{2,}", "_$0");
        word = word.replaceAll("_+", "_");
        return Arrays.asList(word.split("_"));
    }
}
