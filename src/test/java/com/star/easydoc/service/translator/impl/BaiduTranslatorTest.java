package com.star.easydoc.service.translator.impl;

import org.junit.Test;

/**
 * @author wangchao
 * @date 2020/08/23
 */
public class BaiduTranslatorTest {

    @Test
    public void translate() {
        BaiduTranslator translator = new BaiduTranslator();
        System.out.println(translator.en2Ch("Hello"));
        System.out.println(translator.en2Ch("你好"));
    }
}