package com.star.easydoc.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.component.TranslatorComponent;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class TranslatorService {

    private final Map<String, String> map = new HashMap<String, String>() {
        {
            put("build", "构建");
            put("save", "保存");
            put("param", "参数");
            put("list", "数组");
            put("id", "序列");
            put("dao", "DAO");
        }
    };

    public String translate(String source) {
        List<String> words = split(source);
        StringBuilder res = new StringBuilder();
        for (String word : words) {
            res.append(Objects.requireNonNull(ServiceManager.getService(TranslatorComponent.class)
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
