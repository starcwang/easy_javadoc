package com.star.easydoc.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.Consts;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.translator.Translator;
import com.star.easydoc.service.translator.impl.BaiduTranslator;
import com.star.easydoc.service.translator.impl.JinshanTranslator;
import com.star.easydoc.service.translator.impl.TencentTranslator;
import com.star.easydoc.service.translator.impl.YoudaoTranslator;
import com.star.easydoc.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class TranslatorService {

    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();
    private Map<String, Translator> translatorMap = ImmutableMap.<String, Translator>builder()
        .put(Consts.BAIDU_TRANSLATOR, new BaiduTranslator())
        .put(Consts.TENCENT_TRANSLATOR, new TencentTranslator())
        .put(Consts.JINSHAN_TRANSLATOR, new JinshanTranslator())
        .put(Consts.YOUDAO_TRANSLATOR, new YoudaoTranslator())
        .build();

    /**
     * 英译中
     *
     * @param source 源
     * @return {@link String}
     */
    public String translate(String source) {
        // 如果自定义了完整的映射，直接使用完整的映射返回
        String custom = getFromCustom(source);
        if (StringUtils.isNotBlank(custom)) {
            return custom;
        }

        List<String> words = split(source);
        if (hasCustomWord(words)) {
            // 有自定义单词，使用默认模式，单个单词翻译
            StringBuilder sb = new StringBuilder();
            for (String word : words) {
                String res = getFromCustom(word);
                if (StringUtils.isBlank(res)) {
                    res = getFromOthers(word);
                }
                if (StringUtils.isBlank(res)) {
                    res = word;
                }
                sb.append(res);
            }
            return sb.toString();
        } else {
            // 没有自定义单词，使用整句翻译，翻译更准确
            return getFromOthers(StringUtils.join(words, StringUtils.SPACE));
        }
    }

    /**
     * 自动翻译
     *
     * @param source 源
     * @return {@link String}
     */
    public String autoTranslate(String source) {
        Translator translator = translatorMap.get(config.getTranslator());
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        return translator.en2Ch(source.replaceAll("\n", " "));
    }

    /**
     * 中译英
     *
     * @param source 源中文
     * @return {@link String}
     */
    public String translateCh2En(String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        String ch = translatorMap.get(config.getTranslator()).ch2En(source);
        String[] chs = StringUtils.split(ch);
        List<String> chList = chs == null ? Lists.newArrayList() : Lists.newArrayList(chs);
        chList = chList.stream()
            .filter(c -> !Consts.STOP_WORDS.contains(c.toLowerCase()))
            .map(str -> str.replaceAll("[,.'\\-+;:`~]+", ""))
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(chList)) {
            return "";
        }
        if (chList.size() == 1) {
            return chList.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chList.size(); i++) {
            if (StringUtils.isBlank(chList.get(i))) {
                continue;
            }
            if (Consts.STOP_WORDS.contains(chList.get(i).toLowerCase())) {
                continue;
            }
            if (i == 0) {
                sb.append(chList.get(i).toLowerCase());
            } else {
                String lowCh = chList.get(i).toLowerCase();
                sb.append(StringUtils.substring(lowCh, 0, 1).toUpperCase()).append(StringUtils.substring(lowCh, 1));
            }
        }
        return sb.toString();
    }

    private List<String> split(String word) {
        word = word.replaceAll("(?<=[^A-Z])[A-Z][^A-Z]", "_$0");
        word = word.replaceAll("[A-Z]{2,}", "_$0");
        word = word.replaceAll("_+", "_");
        return Arrays.stream(word.split("_")).map(String::toLowerCase).collect(Collectors.toList());
    }

    /**
     * 是否有自定义单词
     *
     * @param words 单词
     * @return boolean
     */
    private boolean hasCustomWord(List<String> words) {
        return CollectionUtil.containsAny(config.getWordMap().keySet(), words);
    }

    private String getFromCustom(String word) {
        return config.getWordMap().get(word.toLowerCase());
    }

    private String getFromOthers(String word) {
        Translator translator = translatorMap.get(config.getTranslator());
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        return translator.en2Ch(word);
    }

    public void clearCache() {
        translatorMap.values().forEach(Translator::clearCache);
    }
}
