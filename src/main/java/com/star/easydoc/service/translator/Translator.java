package com.star.easydoc.service.translator;

/**
 * 翻译
 *
 * @author wangchao
 * @date 2019/11/25
 */
public interface Translator {

    /**
     * 英译中
     *
     * @param text 文本
     * @return {@link java.lang.String}
     */
    String en2Ch(String text);

    /**
     * 中译英
     *
     * @param text 文本
     * @return {@link java.lang.String}
     */
    String ch2En(String text);

    /**
     * 清除缓存
     */
    void clearCache();

}
