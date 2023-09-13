package com.star.easydoc.service.translator.impl;

import java.util.Base64;
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
 * 微软免费翻译
 *
 * @author wangchao
 * @date 2023/09/13
 */
public class MicrosoftFreeTranslator extends AbstractTranslator {

    /** 日志 */
    private static final Logger LOGGER = Logger.getInstance(MicrosoftFreeTranslator.class);

    /** 令牌 */
    private String token = null;

    /** 令牌过期时间 */
    private Long exp = null;

    /** 重试次数 */
    private static final int RETRY_TIMES = 10;

    /** 锁 */
    private static final Object LOCK = new Object();

    /** 令牌url */
    private static final String TOKEN_URL = "https://edge.microsoft.com/translate/auth";

    /** 英译中url */
    private static final String EN2CH_URL
        = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=en&to=zh-Hans";

    /** 中译英url */
    private static final String CH2EN_URL
        = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=zh-Hans&to=en";

    /**
     * 刷新令牌
     */
    private void refreshToken() {
        String thisToken = null;
        for (int i = 1; i <= RETRY_TIMES; i++) {
            try {
                thisToken = HttpUtil.get(TOKEN_URL, 1000, 3000);
                if (thisToken != null && !thisToken.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                LOGGER.warn("获取微软翻译token失败,重试第" + i + "次");
            }
        }
        if (thisToken == null || thisToken.isEmpty()) {
            throw new RuntimeException("重试" + RETRY_TIMES + "次后获取微软token仍失败");
        }
        Long thisExp = JSON.parseObject(Base64.getUrlDecoder().decode(thisToken.split("\\.")[1])).getLong("exp");
        token = thisToken;
        exp = thisExp;
    }

    /**
     * 获取令牌
     *
     * @return 字符串
     */
    private String getToken() {
        if (token != null && exp != null && exp * 1000L > System.currentTimeMillis()) {
            return token;
        }
        synchronized (LOCK) {
            if (token != null && exp != null && exp * 1000L > System.currentTimeMillis()) {
                return token;
            }
            refreshToken();
        }
        return token;
    }

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
            headers.put("Authorization", "Bearer " + getToken());
            json = HttpUtil.postJson(url, headers, JSON.toJSONString(body));
            JSONArray response = JSON.parseArray(json);
            return Objects.requireNonNull(response).getJSONObject(0).getJSONArray("translations").getJSONObject(0)
                .getString("text");
        } catch (Exception e) {
            LOGGER.error("请求微软免费翻译接口异常:请检查本地网络是否可连接外网,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }
}
