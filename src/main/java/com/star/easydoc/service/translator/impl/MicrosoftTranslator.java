package com.star.easydoc.service.translator.impl;

import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 微软翻译
 *
 * @author wangchao
 * @date 2023/04/08
 */
public class MicrosoftTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(MicrosoftTranslator.class);

    private static final String EN2CH_URL
        = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=en&to=zh-Hans";
    private static final String CH2EN_URL
        = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=zh-Hans&to=en";

    @Override
    protected String translateCh2En(String text) {
        return translate(CH2EN_URL, text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate(EN2CH_URL, text);
    }

    private String translate(String url, String text) {
        String json = null;
        try {
            JSONObject textObject = new JSONObject();
            textObject.put("Text", text);
            JSONArray body = new JSONArray();
            body.add(textObject);
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Ocp-Apim-Subscription-Key", getConfig().getMicrosoftKey());
            json = HttpUtil.postJson(url, headers, JSON.toJSONString(body));
            JSONArray response = JSON.parseArray(json);
            return Objects.requireNonNull(response).getJSONObject(0).getJSONArray("translations").getJSONObject(0).getString("text");
        } catch (Exception e) {
            LOGGER.error("请求微软翻译接口异常:请检查本地网络是否可连接外网,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }
}
