package com.star.easydoc.service.translator.impl;

import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.intellij.openapi.diagnostic.Logger;
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
    //声明了一个静态常量LOGGER来记录日志
    /**
     * 定义了两个常量 EN2CH_URL 和 CH2EN_URL，分别表示英译中和中译英的翻译请求 URL。
     */
    private static final String EN2CH_URL
            = "https://translation.googleapis.com/language/translate/v2?q=%s&source=en&target=zh&key=%s&format=text";
    private static final String CH2EN_URL
            = "https://translation.googleapis.com/language/translate/v2?q=%s&source=zh&target=en&key=%s&format=text";

    @Override
    public String translateEn2Ch(String text) {
        return translate(EN2CH_URL, text);
    }

    @Override
    public String translateCh2En(String text) {
        return translate(CH2EN_URL, text);
    }

    /**
     *
     * 该类的作用是通过调用谷歌翻译接口实现中英文互译功能。
     * 它使用了 FastJSON 库进行 JSON 数据的处理，使用了自定义的 HTTP 请求工具类 HttpUtil 发送 HTTP 请求。
     * 在异常处理中，使用日志记录器输出错误信息。
     */
    private String translate(String url, String text) {
        String json = null;
        try {
            json = HttpUtil.get(String.format(url, HttpUtil.encode(text), getConfig().getGoogleKey()), 1000, 3000);
            JSONObject response = JSON.parseObject(json);
            //使用 HttpUtil.get() 方法发送 HTTP GET 请求，将格式化后的 url 字符串作为请求地址。
            //在请求地址中，使用 %s 占位符将待翻译的文本、谷歌翻译的订阅密钥进行格式化。
            //将请求的结果存储到 json 变量中。
            return Objects.requireNonNull(response).getJSONObject("data").getJSONArray("translations")
                    .getJSONObject(0).getString("translatedText");
            //将 json 字符串解析为 JSON 对象 response。
            //从 JSON 对象中获取名为 "data" 的 JSON 对象。
            //从 "data" 对象中获取名为 "translations" 的 JSON 数组，再获取其中的第一个元素（索引为 0）。
            //从 JSON 对象中获取名为 "translatedText" 的字符串，即翻译结果。
        } catch (Exception e) {
            LOGGER.error("请求谷歌翻译接口异常:请检查本地网络是否可连接外网(需翻墙),也有可能是国内网络不稳定,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }

}
