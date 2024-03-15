package com.star.easydoc.service.gpt.impl;

import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.service.gpt.GptSupplier;

/**
 * @author wangchao
 * @date 2024/03/05
 */
public abstract class AbstractGptSupplier implements GptSupplier {

    /** 配置 */
    private EasyDocConfig config;

    @Override
    public GptSupplier init(EasyDocConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public EasyDocConfig getConfig() {
        return this.config;
    }
}
