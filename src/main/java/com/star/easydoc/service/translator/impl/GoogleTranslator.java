package com.star.easydoc.service.translator.impl;

import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 谷歌翻译
 *
 * @author wangchao
 * @date 2023/04/08
 */
public class GoogleTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(GoogleTranslator.class);

    private static final String EN2CH_URL
        = "https://translation.googleapis.com/language/translate/v2?q=%s&source=en&target=zh&key=%s&format=text";
    private static final String CH2EN_URL
        = "https://translation.googleapis.com/language/translate/v2?q=%s&source=zh&target=en&key=%s&format=text";

    @Override
    public String translateEn2Ch(String text, PsiElement psiElement) {
        return translate(EN2CH_URL, text);
    }

    @Override
    public String translateCh2En(String text, PsiElement psiElement) {
        return translate(CH2EN_URL, text);
    }

    private String translate(String url, String text) {
        String json = null;
        try {
            json = HttpUtil.get(String.format(url, HttpUtil.encode(text), getConfig().getGoogleKey()),
                getConfig().getTimeout());
            JSONObject response = JSON.parseObject(json);
            return Objects.requireNonNull(response).getJSONObject("data").getJSONArray("translations")
                .getJSONObject(0).getString("translatedText");
        } catch (Exception e) {
            LOGGER.error("google translate error: please check your appkey and network,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }

}
