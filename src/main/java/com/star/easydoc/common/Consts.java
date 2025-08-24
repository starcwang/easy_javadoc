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
public final class Consts {

    /** 私有构造 */
    private Consts() {}

    /**
     * 基础类型集
     */
    public static final Set<String> BASE_TYPE_SET = Sets.newHashSet("byte", "short", "int", "long", "char", "float",
        "double", "boolean");
    /** 停止词 */
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the", "of");
    /** 默认日期格式 */
    public static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";

    /** 可用翻译设置 */
    public static final Set<String> ENABLE_TRANSLATOR_SET = ImmutableSet.of(Consts.YOUDAO_TRANSLATOR,
        Consts.BAIDU_TRANSLATOR, Consts.TENCENT_TRANSLATOR, Consts.ALIYUN_TRANSLATOR, Consts.YOUDAO_AI_TRANSLATOR,
        Consts.MICROSOFT_TRANSLATOR, Consts.MICROSOFT_FREE_TRANSLATOR, Consts.GOOGLE_TRANSLATOR,
        Consts.GOOGLE_FREE_TRANSLATOR, Consts.CHATGLM_GPT, Consts.OPENAI_GPT, Consts.LOCAL_DICTIONARY, Consts.SIMPLE_SPLITTER,
        Consts.CLOSE_TRANSLATOR, Consts.CUSTOM_URL);

    /** AI翻译集合 */
    public static final Set<String> AI_TRANSLATOR = ImmutableSet.of(Consts.CHATGLM_GPT, Consts.OPENAI_GPT);

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
     * 微软翻译
     */
    public static final String MICROSOFT_TRANSLATOR = "微软翻译";
    /**
     * 微软免费翻译
     */
    public static final String MICROSOFT_FREE_TRANSLATOR = "微软免费翻译";
    /**
     * 谷歌翻译
     */
    public static final String GOOGLE_TRANSLATOR = "谷歌翻译";
    /**
     * 谷歌免费翻译
     */
    public static final String GOOGLE_FREE_TRANSLATOR = "谷歌免费翻译";
    /**
     * 智谱清言
     */
    public static final String CHATGLM_GPT = "智谱清言";
    /**
     * OpenAI API
     */
    public static final String OPENAI_GPT = "OpenAI API";
    /**
     * 本地词典
     */
    public static final String LOCAL_DICTIONARY = "本地词典";
    /**
     * 自定义HTTP接口
     */
    public static final String CUSTOM_URL = "自定义HTTP接口";
    /**
     * 仅单词分割
     */
    public static final String SIMPLE_SPLITTER = "仅单词分割";
    /**
     * 关闭翻译
     */
    public static final String CLOSE_TRANSLATOR = "关闭（只使用自定义翻译）";
}
