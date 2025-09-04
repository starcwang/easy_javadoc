package com.star.easydoc.service.translator;

import com.intellij.psi.PsiElement;
import com.star.easydoc.config.EasyDocConfig;
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
     * @param psiElement 所在位置
     * @return {@link java.lang.String}
     */
    String en2Ch(String text, PsiElement psiElement);

    /**
     * 中译英
     *
     * @param text 文本
     * @param psiElement 所在位置
     * @return {@link java.lang.String}
     */
    String ch2En(String text, PsiElement psiElement);

    /**
     * 初始化
     *
     * @param config 配置
     * @return {@link Translator}
     */
    Translator init(EasyDocConfig config);

    /**
     * 获取配置
     *
     * @return {@link EasyDocConfig}
     */
    EasyDocConfig getConfig();

    /**
     * 清除缓存
     */
    void clearCache();

}
