package com.star.easydoc.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class EasyJavadocConfiguration {
    private String author;
    private String dateFormat;
    private Boolean simpleFieldDoc;
    private String translator;
    private Map<String, String> wordMap;

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Boolean getSimpleFieldDoc() {
        return simpleFieldDoc;
    }

    public void setSimpleFieldDoc(Boolean simpleFieldDoc) {
        this.simpleFieldDoc = simpleFieldDoc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Map<String, String> getWordMap() {
        if (wordMap == null) {
            wordMap = new HashMap<>(8);
        }
        return wordMap;
    }

    public void setWordMap(Map<String, String> wordMap) {
        this.wordMap = wordMap;
    }
}
