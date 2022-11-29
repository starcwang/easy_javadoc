package com.star.easydoc.service.translator.impl;

import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 谷歌翻译
 *
 * @author wangchao
 * @date 2021/08/11
 */
public class GoogleTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(GoogleTranslator.class);

    /*
    1：通过网络请求
    http://translate.google.cn/translate_a/single?client=at&sl=en&tl=zh-CN&dt=t&q=google
    http://translate.google.cn/translate_a/single?client=gtx&sl=en&tl=zh-CN&dt=t&q=google

    dt参数的作用，这里说明一下，dt决定了最终返回的数据，可以包含多个dt参数，以下是dt的一些值：

    t - 源text的翻译
    at - 会额外返回一些近义词
    ex - examples
    ss - 如果翻译的是单个词，会返回与该词相关的动词、形容词、名词
    md - 如果翻译的是单个词，返回该词的定义
    rw - 组词
    bd
    rm
    dt - 似乎是设定返回数据的格式
    可以用这个API，优点是不需要tk，缺点是返回的内容比较简单
    还有如果次数限制了，client=at修改为：client=gtx
    一般at和t正常一点。其它格式可能无法正常得到翻译结果或正确的翻译结果

    返回：
    en2ch
    [[["我的宝贝","my baby",null,null,1]],null,"en",null,null,null,null,[]]
    ch2en
    [[["my darling","我的宝贝",null,null,10]],null,"zh-CN",null,null,null,null,[]]
     */

    private static final String EN2CH_URL = "http://translate.google.cn/translate_a/single?client=gtx&sl=en&tl=zh-CN&dt=t&q=%s";
    private static final String CH2EN_URL = "http://translate.google.cn/translate_a/single?client=gtx&sl=zh-CN&tl=en&dt=t&q=%s";

    @Override
    public String translateEn2Ch(String text) {
        try {
            JSONArray response = JSON.parseArray(HttpUtil.get(String.format(EN2CH_URL, HttpUtil.encode(text))));
            return Objects.requireNonNull(response).getJSONArray(0).getJSONArray(0).getJSONArray(0).toJSONString();
        } catch (Exception e) {
            LOGGER.error("请求谷歌翻译接口异常：请检查本地网络是否可连接外网，也有可能已经被谷歌限流", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String translateCh2En(String text) {
        try {
            JSONArray response = JSON.parseArray(HttpUtil.get(String.format(CH2EN_URL, HttpUtil.encode(text))));
            return Objects.requireNonNull(response).getJSONArray(0).getJSONArray(0).getJSONArray(0).toJSONString();
        } catch (Exception e) {
            LOGGER.error("请求谷歌翻译接口异常：请检查本地网络是否可连接外网，也有可能已经被谷歌限流", e);
            return StringUtils.EMPTY;
        }
    }

}
