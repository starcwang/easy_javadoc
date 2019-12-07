package com.star.easydoc.service.translator.impl;

/**
 * 有道翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class YoudaoEn2ChTranslator extends YoudaoTranslator {

    private static final String URL = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i=%s";

    @Override
    protected String getUrl() {
        return URL;
    }
}
