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
    //定义了一个名为 LOGGER 的静态常量，用于记录日志。
    /**
     * 定义了两个常量 EN2CH_URL 和 CH2EN_URL，分别表示英译中和中译英的翻译请求 URL。
     */
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
            //创建一个 JSONObject 对象 textObject，并将待翻译的文本放入其中。
            //创建一个 JSONArray 对象 body，并将 textObject 放入其中。
            //创建一个 Map 对象 headers，用于存储请求头信息。
            //在 headers 中添加 Ocp-Apim-Subscription-Key 请求头，其值为通过 getConfig().getMicrosoftKey() 方法获取到的微软翻译的订阅密钥。
            //使用 HttpUtil.postJson() 方法发送 HTTP POST 请求，将 url、headers、以及 body 的 JSON 字符串作为参数传递。
            //将响应的 JSON 字符串存储到 json 变量中。
            return Objects.requireNonNull(response).getJSONObject(0).getJSONArray("translations").getJSONObject(0).getString("text");
            //将 json 字符串解析为 JSON 数组 response。
            //获取数组中的第一个元素（索引为 0），并将其转换为 JSON 对象。
            //从 JSON 对象中获取名为 "translations" 的 JSON 数组，再获取其中的第一个元素（索引为 0）。
            //从 JSON 对象中获取名为 "text" 的字符串，即翻译结果。
        } catch (Exception e) {
            LOGGER.error("请求微软翻译接口异常:请检查本地网络是否可连接外网,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }
}
