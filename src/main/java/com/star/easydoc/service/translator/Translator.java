package com.star.easydoc.service.translator;

/**
 * 翻译
 *
 * @author wangchao
 * @date 2019/11/25
 */
public interface Translator {

    /**
     * 翻译
     *
     * @param text 文本
     * @return {@link java.lang.String}
     */
    String translate(String text);

}
