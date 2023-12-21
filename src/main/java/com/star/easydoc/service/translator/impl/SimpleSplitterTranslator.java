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
    //首先，该方法接收一个参数 text，表示待翻译的中文文本。
    //在方法体中，调用了 StringUtils.join() 方法来将拆分后的文本重新连接起来。
    //使用 StringUtil.split(text) 方法将中文文本拆分为一个字符串数组。
    //使用 StringUtils.SPACE 作为连接符，将拆分后的字符串数组连接成一个字符串。
    //返回连接后的字符串，即翻译结果。
    @Override
    protected String translateEn2Ch(String text) {
        return StringUtils.join(StringUtil.split(text), StringUtils.SPACE);
    }
    //同上

}
