package com.star.easydoc.service.translator.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.star.easydoc.service.translator.Translator;

/**
 * @author wangchao
 * @date 2020/08/28
 */
public abstract class AbstractTranslator implements Translator {

    private final Map<String, String> en2chCacheMap = new ConcurrentHashMap<>();
    private final Map<String, String> ch2enCacheMap = new ConcurrentHashMap<>();

    @Override
    public String en2Ch(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String res = en2chCacheMap.get(text);
        if (res != null && res.length() > 0) {
            return res;
        }
        res = translateEn2Ch(text);
        if (res != null && res.length() > 0) {
            en2chCacheMap.put(text, res);
        }
        return res;
    }

    @Override
    public String ch2En(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String res = ch2enCacheMap.get(text);
        if (res != null && res.length() > 0) {
            return res;
        }
        res = translateCh2En(text);
        if (res != null && res.length() > 0) {
            ch2enCacheMap.put(text, res);
        }
        return res;
    }

    /**
     * 清除缓存
     */
    @Override
    public void clearCache() {
        en2chCacheMap.clear();
        ch2enCacheMap.clear();
    }

    /**
     * 中译英
     *
     * @param text 文本
     * @return {@link String}
     */
    protected abstract String translateCh2En(String text);

    /**
     * 英译中
     *
     * @param text 文本
     * @return {@link String}
     */
    protected abstract String translateEn2Ch(String text);
}
