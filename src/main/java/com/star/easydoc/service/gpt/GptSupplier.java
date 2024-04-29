package com.star.easydoc.service.gpt;

import com.star.easydoc.config.EasyDocConfig;

/**
 * @author wangchao
 * @date 2024/03/05
 */
public interface GptSupplier {

    /**
     * 获取回答
     *
     * @param content 问题
     * @return 答案
     */
    String chat(String content);

    /**
     * 初始化
     *
     * @param config 配置
     * @return {@link GptSupplier}
     */
    GptSupplier init(EasyDocConfig config);

    /**
     * 获取配置
     *
     * @return {@link EasyDocConfig}
     */
    EasyDocConfig getConfig();

}
