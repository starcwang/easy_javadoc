package com.star.easydoc.service.translator.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.intellij.psi.PsiElement;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.service.translator.Translator;

/**
 * @author wangchao
 * @date 2020/08/28
 */
public abstract class AbstractTranslator implements Translator {

    private final Map<String, String> en2chCacheMap = new ConcurrentHashMap<>();
    private final Map<String, String> ch2enCacheMap = new ConcurrentHashMap<>();

    /** 配置 */
    private EasyDocConfig config;

    @Override
    public String en2Ch(String text, PsiElement psiElement) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String res = en2chCacheMap.get(text);
        if (res != null && !res.isEmpty()) {
            return res;
        }
        res = translateEn2Ch(text, psiElement);
        if (res != null && !res.isEmpty()) {
            en2chCacheMap.put(text, res);
        }
        return res == null ? "" : res;
    }

    @Override
    public String ch2En(String text, PsiElement psiElement) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String res = ch2enCacheMap.get(text);
        if (res != null && !res.isEmpty()) {
            return res;
        }
        res = translateCh2En(text, psiElement);
        if (res != null && !res.isEmpty()) {
            ch2enCacheMap.put(text, res);
        }
        return res == null ? "" : res;
    }

    @Override
    public Translator init(EasyDocConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public EasyDocConfig getConfig() {
        return this.config;
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
     * @param psiElement 翻译位置
     * @return {@link String}
     */
    protected abstract String translateCh2En(String text, PsiElement psiElement);

    /**
     * 英译中
     *
     * @param text 文本
     * @param psiElement 翻译位置
     * @return {@link String}
     */
    protected abstract String translateEn2Ch(String text, PsiElement psiElement);
}
