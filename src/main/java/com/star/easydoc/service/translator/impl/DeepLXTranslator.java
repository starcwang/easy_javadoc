package com.star.easydoc.service.translator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * DeepLX 翻译
 * 文档参考: https://deeplx.owo.network/endpoints/free.html
 *
 * 使用说明：
 * - 默认使用公共免费端点：https://deeplx.owo.network/translate
 * - 如需自建或替换，请在设置中为“DeepLX 翻译”配置 DeepLX 地址（独立字段，不再复用自定义HTTP）
 * - 如有鉴权令牌，可在设置中“密钥”处填写（将作为 Authorization: Bearer ${token} 发送）
 */
public class DeepLXTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(DeepLXTranslator.class);
    private static final String DEFAULT_URL = "https://deeplx.owo.network/translate";

    @Override
    protected String translateCh2En(String text) {
        return translate("ZH", "EN", text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate("EN", "ZH", text);
    }

    private String translate(String from, String to, String query) {
        String cfg = getConfig().getDeepLxBaseUrl();
        String url = StringUtils.isBlank(cfg) ? DEFAULT_URL : cfg;
        String json = null;
        try {
            Map<String, String> headers = Maps.newHashMap();
            if (StringUtils.isNotBlank(getConfig().getDeepLxToken())) {
                headers.put("Authorization", "Bearer " + getConfig().getDeepLxToken());
            }
            JSONObject body = new JSONObject();
            body.put("text", query);
            body.put("source_lang", from);
            body.put("target_lang", to);

            json = HttpUtil.postJson(url, headers, JSON.toJSONString(body), getConfig().getTimeout());
            JSONObject resp = JSON.parseObject(json);
            if (resp == null) {
                return StringUtils.EMPTY;
            }
            Integer code = resp.getInteger("code");
            if (code != null && code == 200) {
                return StringUtils.defaultString(resp.getString("data"));
            }
            LOGGER.error(String.format("deeplx translate error:url:%s,response:%s", url, json));
            return StringUtils.EMPTY;
        } catch (Exception e) {
            LOGGER.error(String.format("deeplx translate error:url:%s,response:%s", url, json), e);
            return StringUtils.EMPTY;
        }
    }
}

