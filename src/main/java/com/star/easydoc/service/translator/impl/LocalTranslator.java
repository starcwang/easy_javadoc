package com.star.easydoc.service.translator.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ResourceUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 本地翻译
 *
 * @author Administrator
 * @date 2024/08/03
 */
public class LocalTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(LocalTranslator.class);

    private static Map<String, String> en2ChMap;
    private static Map<String, String> ch2EnMap;

    private final Object lock = new Object();

    @Override
    protected String translateCh2En(String text) {
        initLocalMap();
        return ch2EnMap.getOrDefault(text, text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        initLocalMap();
        String[] splits = text.split(StringUtils.SPACE);
        return Arrays.stream(splits).map(en2ChMap::get).filter(StringUtils::isNotBlank)
            .collect(Collectors.joining());
    }

    private void initLocalMap() {
        if (en2ChMap != null && ch2EnMap != null) {
            return;
        }
        synchronized (lock) {
            if (en2ChMap != null && ch2EnMap != null) {
                return;
            }

            try {
                String json = IOUtils.toString(ResourceUtil.getResource(getClass(), "", "words.json"), StandardCharsets.UTF_8);
                Map<String, String> jsonMap = JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
                en2ChMap = Maps.newHashMap();
                ch2EnMap = Maps.newHashMap();
                jsonMap.forEach((k, v) -> {
                    en2ChMap.put(k, v);
                    ch2EnMap.put(v, k);
                });
            } catch (IOException e) {
                LOGGER.error("Local dictionary loading failed.", e);
            }
        }
    }
}
