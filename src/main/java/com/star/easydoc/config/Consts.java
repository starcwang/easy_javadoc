package com.star.easydoc.config;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 常量
 *
 * @author wangchao
 * @date 2019/12/08
 */
public class Consts {
    /**
     * 基础类型集
     */
    public static final Set<String> BASE_TYPE_SET = Sets.newHashSet("byte", "short", "int", "long", "char", "float",
            "double", "boolean");
    /** 停止词 */
    public static final Set<String> STOP_WORDS = Sets.newHashSet("the");
}
