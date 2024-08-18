package com.star.easydoc.service.translator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 本地翻译
 *
 * @author Administrator
 * @date 2024/08/03
 */
public class CustomTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(CustomTranslator.class);

    @Override
    protected String translateCh2En(String text) {
        return translate("zh", "en", text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate("en", "zh", text);
    }

    private String translate(String from, String to, String query) {
        String json = null;
        String url = getConfig().getCustomUrl().replace("{from}", from).replace("{to}", to)
            .replace("{query}", HttpUtil.encode(query));
        try {
            json = HttpUtil.get(url, getConfig().getTimeout());
            JSONObject response = JSON.parseObject(json);
            if (response == null || response.getInteger("code") != 0) {
                LOGGER.error(String.format("custom translate error:url:%s,response:%s", url, json));
                return StringUtils.EMPTY;
            }
            return response.getString("data");
        } catch (Exception e) {
            LOGGER.error(String.format("custom translate error:url:%s,response:%s", url, json), e);
            return StringUtils.EMPTY;
        }
    }

}
