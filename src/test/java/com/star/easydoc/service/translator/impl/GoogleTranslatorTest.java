package com.star.easydoc.service.translator.impl;

import org.junit.Test;

/**
 * @author wangchao
 * @date 2020/08/23
 */
public class GoogleTranslatorTest {

    @Test
    public void translate() {
        GoogleTranslator translator = new GoogleTranslator();
        System.out.println(translator.en2Ch("Hello"));
        System.out.println(translator.ch2En("你好"));
    }
}