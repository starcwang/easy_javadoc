package com.star.easydoc.service.translator.impl;

import com.star.easydoc.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 简单的单词分割
 *
 * @author wangchao
 * @date 2023/06/24
 */
public class SimpleSplitterTranslator extends AbstractTranslator {

    @Override
    protected String translateCh2En(String text) {
        return StringUtils.join(StringUtil.split(text), StringUtils.SPACE);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return StringUtils.join(StringUtil.split(text), StringUtils.SPACE);
    }

}
