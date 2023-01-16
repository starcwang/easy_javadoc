package com.star.easydoc.common;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * 常量
 *
 * @author wangchao
 * @date 2019/12/08
 */
public class Consts {

    /** 私有构造 */
    private Consts() {}

    /**
     * 基础类型集
     */
    public static final Set<String> BASE_TYPE_SET = Sets.newHashSet("byte", "short", "int", "long", "char", "float",
        "double", "boolean");
    /** 停止词 */
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the");
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

    /** 可用翻译设置 */
    public static final Set<String> ENABLE_TRANSLATOR_SET = ImmutableSet.of(Consts.YOUDAO_TRANSLATOR,
        Consts.BAIDU_TRANSLATOR, Consts.TENCENT_TRANSLATOR, Consts.ALIYUN_TRANSLATOR, Consts.YOUDAO_AI_TRANSLATOR,
        Consts.CLOSE_TRANSLATOR);

    /**
     * 腾讯翻译
     */
    public static final String TENCENT_TRANSLATOR = "腾讯翻译";
    /**
     * 百度翻译
     */
    public static final String BAIDU_TRANSLATOR = "百度翻译";
    /**
     * 有道翻译
     */
    public static final String YOUDAO_TRANSLATOR = "有道翻译";
    /**
     * 金山翻译
     */
    public static final String JINSHAN_TRANSLATOR = "金山翻译";
    /**
     * 阿里云翻译
     */
    public static final String ALIYUN_TRANSLATOR = "阿里云翻译";
    /**
     * 有道智云翻译
     */
    public static final String YOUDAO_AI_TRANSLATOR = "有道智云翻译";
    /**
     * 关闭翻译
     */
    public static final String CLOSE_TRANSLATOR = "关闭（只使用自定义翻译）";
}
