package com.star.easydoc.service.translator.impl;

import org.junit.Test;

/**
 * @author wangchao
 * @date 2020/08/23
 */
public class AliyunTranslatorTest {

    @Test
    public void translate() {
        AliyunTranslator translator = new AliyunTranslator();
        System.out.println(translator.en2Ch("Hello"));
        System.out.println(translator.en2Ch("你好"));
    }
}