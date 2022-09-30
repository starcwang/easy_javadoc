package com.star.easydoc.service.translator;

import java.util.Map;

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
     * 初始化
     *
     * @param config 配置
     * @return {@link Translator}
     */
    Translator init(Map<String, String> config);

    /**
     * 清除缓存
     */
    void clearCache();

}
