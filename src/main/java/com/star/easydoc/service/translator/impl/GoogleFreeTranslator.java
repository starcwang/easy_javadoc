package com.star.easydoc.service.translator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 谷歌翻译
 *
 * @author wangchao
 * @date 2023/04/08
 */
public class GoogleFreeTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(GoogleFreeTranslator.class);

    private static final String EN2CH_URL
        = "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&sl=en&tl=zh-CN&q=%s";
    private static final String CH2EN_URL
        = "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&sl=zh-CN&tl=en&q=%s";

    @Override
    public String translateEn2Ch(String text) {
        return translate(EN2CH_URL, text);
    }

    @Override
    public String translateCh2En(String text) {
        return translate(CH2EN_URL, text);
    }

    private String translate(String url, String text) {
        String json = null;
        try {
            json = HttpUtil.get(String.format(url, HttpUtil.encode(text)), getConfig().getTimeout());
            JSONArray jsonArray = JSON.parseArray(json);
            return jsonArray.getJSONArray(0).getJSONArray(0).getString(0);
        } catch (Exception e) {
            LOGGER.error("google free translate error: please check your network,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }

}
