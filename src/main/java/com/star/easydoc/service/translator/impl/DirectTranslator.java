package com.star.easydoc.service.translator.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirectTranslator extends AbstractTranslator {
    @Override
    protected String translateCh2En(String text) {
        return StringUtils.join(split(text), StringUtils.SPACE);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return StringUtils.join(split(text), StringUtils.SPACE);
    }

    private List<String> split(String word) {
        word = word.replaceAll("(?<=[^A-Z])[A-Z][^A-Z]", "_$0");
        word = word.replaceAll("[A-Z]{2,}", "_$0");
        word = word.replaceAll("_+", "_");
        return Arrays.stream(word.split("_")).map(String::toLowerCase).collect(Collectors.toList());
    }
}
