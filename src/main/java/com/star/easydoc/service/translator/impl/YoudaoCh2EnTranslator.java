package com.star.easydoc.service.translator.impl;

/**
 * 有道翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class YoudaoCh2EnTranslator extends YoudaoTranslator {

    private static final String URL = "http://fanyi.youdao.com/translate?&doctype=json&type=ZH_CN2EN&i=%s";

    @Override
    protected String getUrl() {
        return URL;
    }
}
